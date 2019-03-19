package be.valuya.winbooks.api.extra;

import be.valuya.jbooks.model.WbAccount;
import be.valuya.jbooks.model.WbBookYearFull;
import be.valuya.jbooks.model.WbBookYearStatus;
import be.valuya.jbooks.model.WbClientSupplier;
import be.valuya.jbooks.model.WbDocument;
import be.valuya.jbooks.model.WbEntry;
import be.valuya.jbooks.model.WbParam;
import be.valuya.jbooks.model.WbPeriod;
import be.valuya.winbooks.domain.error.WinbooksError;
import be.valuya.winbooks.domain.error.WinbooksException;
import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.PdfCopyFields;
import com.lowagie.text.pdf.PdfReader;
import net.iryndin.jdbf.core.DbfRecord;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;
import java.text.MessageFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class WinbooksExtraService {

    private static Logger LOGGER = Logger.getLogger(WinbooksExtraService.class.getName());

    // some invalid String that Winbooks likes to have
    public static final String CHAR0_STRING = Character.toString((char) 0);
    private static final String PARAM_TABLE_NAME = "param";
    private static final String BOOKYEARS_TABLE_NAME = "SLBKY";
    private static final String PERIOD_TABLE_NAME = "SLPRD";
    private static final String ACCOUNT_TABLE_NAME = "ACF";
    private static final String CUSTOMER_SUPPLIER_TABLE_NAME = "CSF";
    private static final String DEFAULT_TABLE_FILE_NAME_REGEX = "^(.*)_" + ACCOUNT_TABLE_NAME + ".DBF$";
    private static final Pattern DEFAULT_TABLE_FILE_NAME_PATTERN = Pattern.compile(DEFAULT_TABLE_FILE_NAME_REGEX, Pattern.CASE_INSENSITIVE);
    private static final String ACCOUNTING_ENTRY_TABLE_NAME = "ACT";
    private static final String DBF_EXTENSION = ".dbf";
    private static final DateTimeFormatter PERIOD_FORMATTER = DateTimeFormatter.ofPattern("ddMMyyyy");

    public WinbooksSession createSession(WinbooksFileConfiguration winbooksFileConfiguration) {
        List<WbBookYearFull> bookYears = streamBookYears(winbooksFileConfiguration)
                .collect(Collectors.toList());
        return new WinbooksSession(winbooksFileConfiguration, bookYears);
    }

    public LocalDateTime getActModificationDateTime(WinbooksFileConfiguration winbooksFileConfiguration) {
        Path path = resolveTablePath(winbooksFileConfiguration, ACCOUNTING_ENTRY_TABLE_NAME);
        try {
            FileTime lastModifiedTime = Files.getLastModifiedTime(path);
            Instant lastModificationInstant = lastModifiedTime.toInstant();
            return LocalDateTime.ofInstant(lastModificationInstant, ZoneId.systemDefault());
        } catch (IOException exception) {
            throw new WinbooksException(WinbooksError.UNKNOWN_ERROR, exception);
        }
    }

    public Stream<WbEntry> streamAct(WinbooksFileConfiguration winbooksFileConfiguration, WinbooksEventHandler winbooksEventHandler) {
        List<WbBookYearFull> wbBookYearFullList = streamBookYears(winbooksFileConfiguration)
                .collect(Collectors.toList());
        List<WbBookYearFull> archivedBookYearFullList = wbBookYearFullList
                .stream()
                .filter(this::isArchived)
                .collect(Collectors.toList());

        PeriodResolver periodResolver = new PeriodResolver();
        periodResolver.init(wbBookYearFullList);

        WbEntryDbfReader wbEntryDbfReader = new WbEntryDbfReader(periodResolver);

        Stream<WbEntry> archivedEntryStream = archivedBookYearFullList
                .stream()
                .map(WbBookYearFull::getArchivePathNameOptional)
                .flatMap(this::streamOptional)
                .flatMap(archivePathName -> streamArchivedTable(winbooksFileConfiguration, ACCOUNTING_ENTRY_TABLE_NAME, archivePathName, winbooksEventHandler))
                .filter(this::isValidActRecord)
                .map(wbEntryDbfReader::readWbEntryFromActDbfRecord)
                .flatMap(this::streamOptional);

        Stream<WbEntry> unarchivedEntryStream = streamTable(winbooksFileConfiguration, ACCOUNTING_ENTRY_TABLE_NAME)
                .filter(this::isValidActRecord)
                .map(wbEntryDbfReader::readWbEntryFromActDbfRecord)
                .flatMap(this::streamOptional)
                .filter(this::isCurrent);

        return Stream.concat(archivedEntryStream, unarchivedEntryStream);
    }

    public Stream<WbAccount> streamAcf(WinbooksFileConfiguration winbooksFileConfiguration) {
        WbAccountDbfReader wbAccountDbfReader = new WbAccountDbfReader();
        return streamTable(winbooksFileConfiguration, ACCOUNT_TABLE_NAME)
                .map(wbAccountDbfReader::readWbAccountFromAcfDbfRecord);
    }

    public Stream<WbClientSupplier> streamCsf(WinbooksFileConfiguration winbooksFileConfiguration) {
        WbClientSupplierDbfReader wbClientSupplierDbfReader = new WbClientSupplierDbfReader();
        return streamTable(winbooksFileConfiguration, CUSTOMER_SUPPLIER_TABLE_NAME)
                .map(wbClientSupplierDbfReader::readWbClientSupplierFromAcfDbfRecord);
    }

    public Stream<WbBookYearFull> streamBookYears(WinbooksFileConfiguration winbooksFileConfiguration) {
        if (false && tableExists(winbooksFileConfiguration, BOOKYEARS_TABLE_NAME)) { //TODO: currently, we can findWbBookYearFull more info out of the badly structured param table
            return streamBookYearsFromBookyearsTable(winbooksFileConfiguration);
        }
        // fall-back: a lot of customers seem not to have table above
        return listBookYearsFromParamTable(winbooksFileConfiguration).stream();
    }

    public List<WbBookYearFull> listBookYearsFromParamTable(WinbooksFileConfiguration winbooksFileConfiguration) {
        Map<String, String> paramMap = getParamMap(winbooksFileConfiguration);

        List<WbBookYearFull> wbBookYearFullList = new ArrayList<>();

        String bookYearCountStr = paramMap.get("BOOKYEARCOUNT");
        int bookYearCount = Integer.parseInt(bookYearCountStr);
        for (int i = 1; i <= bookYearCount; i++) {
            String bookYearParamPrefix = "BOOKYEAR" + i;
            String bookYearLongLabel = paramMap.get(bookYearParamPrefix + "." + "LONGLABEL");
            String bookYearShortLabel = paramMap.get(bookYearParamPrefix + "." + "SHORTLABEL");
            String archivePathName = paramMap.get(bookYearParamPrefix + "." + "PATHARCH");
            Optional<String> archivePathNameNullable = Optional.ofNullable(archivePathName);

            String perDatesStr = paramMap.get(bookYearParamPrefix + "." + "PERDATE");
            List<LocalDate> periodDates = parsePeriodDates(perDatesStr);

            int periodCount = periodDates.size() - 2;
            int durationInMonths = 12 / periodCount;

            String concatenatedPeriodNames = paramMap.get(bookYearParamPrefix + "." + "PERLIB1");
            List<String> periodNames = parsePeriodNames(concatenatedPeriodNames);

            List<WbPeriod> wbPeriodList = convertWinbooksPeriods(periodNames, periodDates, durationInMonths);

            LocalDate startDate = periodDates.stream()
                    .findFirst()
                    .orElseThrow(IllegalArgumentException::new);
            LocalDate endDate = periodDates.stream()
                    .max(LocalDate::compareTo)
                    .map(date -> date.plusDays(1)) // exclusive upper bound is day after
                    .orElseThrow(IllegalArgumentException::new);

            int startYear = startDate.getYear();
            int endYear = endDate.getYear();

            String statusStrNullable = paramMap.get(bookYearParamPrefix + "." + "STATUS");

            WbBookYearFull wbBookYearFull = new WbBookYearFull();
            wbBookYearFull.setLongName(bookYearLongLabel);
            wbBookYearFull.setShortName(bookYearShortLabel);
            wbBookYearFull.setArchivePathNameOptional(archivePathNameNullable);
            wbBookYearFull.setIndex(i);
            wbBookYearFull.setStartDate(startDate);
            wbBookYearFull.setEndDate(endDate);
            wbBookYearFull.setYearBeginInt(startYear);
            wbBookYearFull.setYearEndInt(endYear);
            wbBookYearFull.setPeriods(periodCount);
            wbBookYearFull.setPeriodList(wbPeriodList);

            wbPeriodList.forEach(wbPeriod -> wbPeriod.setWbBookYearFull(wbBookYearFull));

            Optional.ofNullable(statusStrNullable)
                    .flatMap(WbBookYearStatus::fromValueStr)
                    .ifPresent(wbBookYearFull::setWbBookYearStatus);

            wbBookYearFullList.add(wbBookYearFull);
        }

        return wbBookYearFullList;
    }

    public Stream<WbBookYearFull> streamBookYearsFromBookyearsTable(WinbooksFileConfiguration winbooksFileConfiguration) {
        WbBookYearFullDbfReader wbBookYearFullDbfReader = new WbBookYearFullDbfReader();
        return streamTable(winbooksFileConfiguration, BOOKYEARS_TABLE_NAME)
                .map(wbBookYearFullDbfReader::readWbBookYearFromSlbkyDbfRecord);
    }

    public Stream<DbfRecord> streamArchivedTable(WinbooksFileConfiguration winbooksFileConfiguration, String tableName, String archivePathName, WinbooksEventHandler winbooksEventHandler) {
        Charset charset = winbooksFileConfiguration.getCharset();

        return getArchivedTableInputStreamOptional(winbooksFileConfiguration, tableName, archivePathName, winbooksEventHandler)
                .map(tableInputStream -> DbfUtils.streamDbf(tableInputStream, charset))
                .orElseGet(Stream::empty);
    }

    public Stream<DbfRecord> streamTable(WinbooksFileConfiguration winbooksFileConfiguration, String tableName) {
        InputStream tableInputStream = getTableInputStream(winbooksFileConfiguration, tableName);
        Charset charset = winbooksFileConfiguration.getCharset();
        return DbfUtils.streamDbf(tableInputStream, charset);
    }

    public Path resolveTablePath(WinbooksFileConfiguration winbooksFileConfiguration, String tableName) {
        return resolveTablePathOptional(winbooksFileConfiguration, tableName)
                .orElseThrow(() -> {
                    Path baseFolderPath = winbooksFileConfiguration.getBaseFolderPath();
                    String baseFolderPathName = getPathFileNameString(baseFolderPath);

                    String fileName = getTableFileName(winbooksFileConfiguration, tableName);

                    String message = MessageFormat.format("Could not find file {0} in folder {1}", fileName, baseFolderPathName);
                    return new WinbooksException(WinbooksError.DOSSIER_NOT_FOUND, message);
                });
    }

    public Optional<Path> resolveCaseInsensitiveSibilingPathOptional(Path folderPath, String fileName) {
        Path parentPath = folderPath.getParent();
        return resolveCaseInsensitivePathOptional(parentPath, fileName);
    }

    public Optional<Path> resolveCaseInsensitivePathOptional(Path parentPath, String fileName) {
        try {
            boolean parentExists = Files.exists(parentPath);
            if (!parentExists) {
                return Optional.empty();
            }
            Path defaultPath = parentPath.resolve(fileName);
            if (Files.exists(defaultPath)) {
                return Optional.of(defaultPath);
            }
            if (fileName.endsWith(".dbf")) {
                String capitalizedExtensionFileName = fileName.replace(".dbf", ".DBF");
                Path capitalizedExtensionPath = parentPath.resolve(capitalizedExtensionFileName);
                if (Files.exists(capitalizedExtensionPath)) {
                    return Optional.of(capitalizedExtensionPath);
                }
            }

            long time0 = System.currentTimeMillis();
            Optional<Path> firstFoundPathOptional = Files.find(parentPath, 1,
                    (path, attr) -> isSamePathName(path, fileName))
                    .findFirst();
            long timeAfterWalk = System.currentTimeMillis();
            long deltaTimeWalk = timeAfterWalk - time0;
            LOGGER.log(Level.FINE, "****FIND TIME (" + fileName + "): " + deltaTimeWalk);
            return firstFoundPathOptional;
        } catch (IOException exception) {
            throw new WinbooksException(WinbooksError.UNKNOWN_ERROR, exception);
        }
    }

    private boolean isCurrent(WbEntry wbEntry) {
        WbBookYearFull wbBookYearFull = wbEntry.getWbBookYearFull();
        return !isArchived(wbBookYearFull);
    }

    private boolean isArchived(WbBookYearFull wbBookYearFull) {
        Optional<String> archivePathNameOptional = Optional.ofNullable(wbBookYearFull)
                .flatMap(WbBookYearFull::getArchivePathNameOptional);
        return archivePathNameOptional.isPresent();
    }

    private <T> Stream<T> streamOptional(Optional<T> optional) {
        return optional.map(Stream::of)
                .orElseGet(Stream::empty);
    }

    private Map<String, String> getParamMap(WinbooksFileConfiguration winbooksFileConfiguration) {
        return streamTable(winbooksFileConfiguration, PARAM_TABLE_NAME)
                .map(new WbParamDbfReader()::readWbParamFromDbfRecord)
                .filter(wbParam -> wbParam.getValue() != null)
                .collect(Collectors.toMap(WbParam::getId, WbParam::getValue, (id1, id2) -> id2));
    }

    private boolean isValidActRecord(DbfRecord dbfRecord) {
        String docOrderNullable = dbfRecord.getString("DOCORDER");
        return Optional.ofNullable(docOrderNullable)
                .map(this::isWbValidString)
                .orElse(true);
    }

    private boolean isWbValidString(String str) {
        return str == null || !str.startsWith(CHAR0_STRING);
    }

    public Optional<WinbooksFileConfiguration> createWinbooksFileConfigurationOptional(Path parentPath, String baseName) {
        return resolveCaseInsensitivePathOptional(parentPath, baseName)
                .flatMap(this::createWinbooksFileConfigurationOptional);
    }

    public Optional<WinbooksFileConfiguration> createWinbooksFileConfigurationOptional(Path customerWinbooksPath) {
        Optional<String> customerWinbooksPathNameOptional = Optional.ofNullable(customerWinbooksPath.getFileName())
                .map(Path::toString);
        if (!customerWinbooksPathNameOptional.isPresent()) {
            return Optional.empty();
        }
        String customerWinbooksPathName = customerWinbooksPathNameOptional.get();

        WinbooksFileConfiguration winbooksFileConfiguration = new WinbooksFileConfiguration();
        winbooksFileConfiguration.setBaseFolderPath(customerWinbooksPath);
        winbooksFileConfiguration.setBaseName(customerWinbooksPathName);

        if (!tableExists(winbooksFileConfiguration, ACCOUNTING_ENTRY_TABLE_NAME)) {
            return Optional.empty();
        }

        return Optional.of(winbooksFileConfiguration);
    }

    private boolean isSamePathName(Path path, String fileName) {
        return Optional.ofNullable(path.getFileName())
                .map(Path::toString)
                .map(fileName::equalsIgnoreCase)
                .orElse(false);
    }

    private List<WbPeriod> convertWinbooksPeriods(List<String> periodNames, List<LocalDate> periodDates, int durationInMonths) {
        int periodCount = periodNames.size();
        if (periodDates.size() != periodCount) {
            throw new WinbooksException(WinbooksError.PERIOD_DATE_MISMATCH, "Different sizes for period names and period dates.");
        }

        List<WbPeriod> periods = new ArrayList<>();
        for (int i = 0; i < periodCount; i++) {
            String periodName = periodNames.get(i);
            LocalDate periodStartDate = periodDates.get(i);
            LocalDate periodEndDate = periodStartDate.plusMonths(durationInMonths);

            WbPeriod period = new WbPeriod();
            period.setStartDate(periodStartDate);
            period.setEndDate(periodEndDate);
            period.setShortName(periodName);
            period.setIndex(i);

            periods.add(period);
        }

        WbPeriod lasterWbPeriod = periods.get(periodCount - 1);
        lasterWbPeriod.setIndex(99);

        return periods;
    }

    private List<String> parsePeriodNames(String concatenatedPeriodNames) {
        List<String> periodNames = new ArrayList<>();

        int length = concatenatedPeriodNames.length();
        for (int i = 0; i + 8 <= length; i += 8) {
            String periodName = concatenatedPeriodNames.substring(i, i + 8);
            periodNames.add(periodName);
        }

        return periodNames;
    }

    private List<LocalDate> parsePeriodDates(String allPeriodDatesStr) {
        List<LocalDate> periodDates = new ArrayList<>();
        int allPeriodLength = allPeriodDatesStr.length();
        int i = 0;
        while (i < allPeriodLength) {
            char currentChar = allPeriodDatesStr.charAt(i);
            if (currentChar == ' ') {
                i++;
                continue;
            }
            String periodDateStr = allPeriodDatesStr.substring(i, i + 8);
            LocalDate periodDate = LocalDate.parse(periodDateStr, PERIOD_FORMATTER);

            if (i == allPeriodLength - 1) {
                periodDate = periodDate.plusDays(1);
            }

            periodDates.add(periodDate);
            i += 8;
        }

        return periodDates;
    }

    private Optional<InputStream> getArchivedTableInputStreamOptional(WinbooksFileConfiguration winbooksFileConfiguration, String tableName, String archivePathName, WinbooksEventHandler winbooksEventHandler) {
        Path archivedTablePath = resolveArchivedTablePath(winbooksFileConfiguration, tableName, archivePathName);

        boolean ignoreMissingArchives = winbooksFileConfiguration.isIgnoreMissingArchives();
        if (!Files.exists(archivedTablePath) && ignoreMissingArchives) {
            Path archiveFolderPath = archivedTablePath.getParent();
            String archiveFolderName = getPathFileNameString(archiveFolderPath);
            WinbooksEvent winbooksEvent = new WinbooksEvent(WinbooksEventCategory.ARCHIVE_NOT_FOUND, "Archive not found at expected path {0}. Ignoring as per configuration.", archiveFolderName);
            winbooksEventHandler.handleEvent(winbooksEvent);
            return Optional.empty();
        }

        InputStream inputStream = getFastInputStream(winbooksFileConfiguration, archivedTablePath);

        return Optional.of(inputStream);
    }

    private String getPathFileNameString(Path archiveFolderPath) {
        return Optional.ofNullable(archiveFolderPath.getFileName())
                .map(Path::toString)
                .orElse("");
    }

    private InputStream getTableInputStream(WinbooksFileConfiguration winbooksFileConfiguration, String tableName) {
        Path path = resolveTablePath(winbooksFileConfiguration, tableName);
        return getFastInputStream(winbooksFileConfiguration, path);
    }

    private InputStream getFastInputStream(WinbooksFileConfiguration winbooksFileConfiguration, Path path) {
        try {
            if (!winbooksFileConfiguration.isReadTablesToMemory()) {
                return Files.newInputStream(path);
            }

            long time0 = System.currentTimeMillis();
            byte[] bytes = Files.readAllBytes(path);
            long time1 = System.currentTimeMillis();
            long deltaTime = time1 - time0;
            LOGGER.log(Level.FINE, "READ TIME (" + path + "): " + deltaTime);

            return new ByteArrayInputStream(bytes);
        } catch (IOException exception) {
            throw new WinbooksException(WinbooksError.UNKNOWN_ERROR, exception);

        }
    }

    private Path resolveArchivedTablePath(WinbooksFileConfiguration winbooksFileConfiguration, String tableName, String archivePathName) {
        String archiveFolderName = archivePathName
                .replace("\\", "/")
                .replaceAll("/$", "")
                .replaceAll("^.*/", "");
        String tableFileName = archiveFolderName + "_" + tableName + ".dbf";

        Path baseFolderPath = winbooksFileConfiguration.getBaseFolderPath();
        Path archiveFolderPath = resolveCaseInsensitiveSibilingPathOptional(baseFolderPath, archiveFolderName)
                .orElseGet(() -> baseFolderPath.resolveSibling(archiveFolderName)); // we return an unexisting path if needed, to at least show a relevant error

        return resolveCaseInsensitivePathOptional(archiveFolderPath, tableFileName)
                .orElseGet(() -> archiveFolderPath.resolve(tableFileName));  // we return an unexisting path if needed, to at least show a relevant error
    }

    private boolean tableExists(WinbooksFileConfiguration winbooksFileConfiguration, String tableName) {
        return resolveTablePathOptional(winbooksFileConfiguration, tableName)
                .isPresent();
    }

    private Optional<Path> resolveTablePathOptional(WinbooksFileConfiguration winbooksFileConfiguration, String tableName) {
        String fileName = getTableFileName(winbooksFileConfiguration, tableName);

        Path basePath = winbooksFileConfiguration.getBaseFolderPath();

        return resolveCaseInsensitivePathOptional(basePath, fileName);
    }

    private String getTableFileName(WinbooksFileConfiguration winbooksFileConfiguration, String tableName) {
        String baseName = winbooksFileConfiguration.getBaseName();
        String tablePrefix = baseName.replace("_", "");
        return tablePrefix + "_" + tableName + DBF_EXTENSION;
    }

    public Stream<WbDocument> streamDocuments(WinbooksSession winbooksSession) {
        WinbooksFileConfiguration winbooksFileConfiguration = winbooksSession.getWinbooksFileConfiguration();
        Path baseFolderPath = winbooksFileConfiguration.getBaseFolderPath();
        Path documentFolderPath = resolveCaseInsensitiveSibilingPathOptional(baseFolderPath, "Documents")
                .orElseGet(() -> baseFolderPath.resolve("Document")); // we return an unexisting path if needed, to at least show a relevant error
        return winbooksSession.getBookYears()
                .stream()
                .flatMap(bookYear -> streamBookYearDocuments(documentFolderPath, bookYear));
    }

    private Stream<WbDocument> streamBookYearDocuments(Path documentFolderPath, WbBookYearFull bookYear) {
        String bookYearName = bookYear.getShortName();
        Path bookYearDocumentFolderPath = documentFolderPath.resolve(bookYearName);

        if (!Files.exists(bookYearDocumentFolderPath)) {
            return Stream.empty();
        }

        try {
            return Files.list(bookYearDocumentFolderPath)
                    .flatMap(bookYearDbkPath -> streamDbkBookYearDocuments(bookYearDbkPath, bookYear));
        } catch (IOException e) {
            return Stream.empty();
        }
    }

    private Stream<WbDocument> streamDbkBookYearDocuments(Path path, WbBookYearFull bookYear) {
        String dbkCode = path.getFileName().toString();

        Collector<WbDocument, ?, Optional<WbDocument>> maxPageNrComparator = Collectors.maxBy(Comparator.comparing(WbDocument::getPageCount));
        return streamDirectory(path)
                .map(documentPath -> getDocument(documentPath, bookYear, dbkCode))
                .flatMap(this::streamOptional)
                .collect(Collectors.groupingBy(Function.identity(), maxPageNrComparator))
                .values()
                .stream()
                .flatMap(this::streamOptional);
    }


    public Optional<byte[]> getDocumentData(WinbooksSession winbooksSession, WbDocument document) {
        WinbooksFileConfiguration winbooksFileConfiguration = winbooksSession.getWinbooksFileConfiguration();
        Path baseFolderPath = winbooksFileConfiguration.getBaseFolderPath();
        Path documentFolderPath = resolveCaseInsensitiveSibilingPathOptional(baseFolderPath, "Documents")
                .orElseGet(() -> baseFolderPath.resolve("Document")); // we return an unexisting path if needed, to at least show a relevant error

        WbBookYearFull wbBookYearFull = document.getWbPeriod().getWbBookYearFull();
        String bookYearShortName = wbBookYearFull.getShortName();
        String dbCode = document.getDbCode();
        Path documentPagesFolderPath = documentFolderPath.resolve(bookYearShortName).resolve(dbCode);
        return this.streamDocumentPagesPaths(document)
                .map(documentPagesFolderPath::resolve)
                .map(this::readAllBytes)
                .reduce(this::mergePdf);
    }

    private byte[] readAllBytes(Path path) {
        try {
            return Files.readAllBytes(path);
        } catch (IOException exception) {
            throw new WinbooksException(WinbooksError.USER_FILE_ERROR, exception);
        }
    }

    private byte[] mergePdf(byte[] pdfData1, byte[] pdfData2) {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            PdfCopyFields pdfCopyFields = new PdfCopyFields(byteArrayOutputStream);

            PdfReader pdfReader1 = new PdfReader(pdfData1);
            pdfCopyFields.addDocument(pdfReader1);

            PdfReader pdfReader2 = new PdfReader(pdfData2);
            pdfCopyFields.addDocument(pdfReader2);

            pdfCopyFields.close();
            pdfReader1.close();
            pdfReader2.close();

            return byteArrayOutputStream.toByteArray();
        } catch (IOException | DocumentException exception) {
            throw new WinbooksException(WinbooksError.UNKNOWN_ERROR, "Erreur de traitement PDF.", exception);
        }
    }

    private Stream<Path> streamDirectory(Path path) {
        try {
            return Files.walk(path);
        } catch (IOException exception) {
            throw new WinbooksException(WinbooksError.USER_FILE_ERROR, exception);
        }
    }

    private Stream<Path> streamDocumentPagesPaths(WbDocument document) {
        int pageCount = document.getPageCount();

        return IntStream.range(0, pageCount)
                .mapToObj(pageIndex -> getDocumentPagePath(pageIndex, document));
    }

    private Path getDocumentPagePath(int pageIndex, WbDocument document) {
        WbPeriod wbPeriod = document.getWbPeriod();
        int wbPeriodIndex = wbPeriod.getIndex();
        String periodIndexName = String.format("%02d", wbPeriodIndex);
        String pageIndexName = String.format("%02d", pageIndex);

        String fileName = MessageFormat.format("{0}_{1}_{2}_{3}.pdf",
                document.getDbCode(),
                periodIndexName,
                document.getName(),
                pageIndexName
        );
        return Paths.get(fileName);
    }

    private Optional<WbDocument> getDocument(Path documentPath, WbBookYearFull bookYear, String expectedDbkCode) {
        String fileName = documentPath.getFileName().toString();
        Pattern pattern = Pattern.compile("^(\\w+)_(\\d+)_(\\d+)_(\\d+).pdf$");
        Matcher matcher = pattern.matcher(fileName);
        if (!matcher.matches()) {
            return Optional.empty();
        }
        FileTime lastModifiedFileTime = null;
        try {
            lastModifiedFileTime = Files.getLastModifiedTime(documentPath);
        } catch (IOException e) {
            // TODO: rethrow? ignore?
            return Optional.empty();
        }
        LocalDateTime lastModifiedLocalTime = LocalDateTime.ofInstant(lastModifiedFileTime.toInstant(), ZoneId.systemDefault());

        String actualDbkCode = matcher.group(1);
        String periodName = matcher.group(2);
        String name = matcher.group(3);
        String pageNrStr = matcher.group(4);
        int pageNr = Integer.valueOf(pageNrStr);

        WbDocument wbDocument = new WbDocument();
        wbDocument.setDbCode(actualDbkCode);
        wbDocument.setName(name);
        wbDocument.setPageCount(pageNr);
        wbDocument.setWbPeriod(getWbPeriod(bookYear, periodName));
        wbDocument.setUpdatedTime(lastModifiedLocalTime);
        // FIXME: creation time
        wbDocument.setCreationTime(lastModifiedLocalTime);

        return Optional.of(wbDocument);
    }

    private WbPeriod getWbPeriod(WbBookYearFull bookYear, String periodName) {
        return bookYear.getPeriodList()
                .stream()
                .filter(wbPeriod -> isPeriodIndex(wbPeriod, periodName))
                .findFirst()
                .orElseThrow(() -> new WinbooksException(WinbooksError.ILLEGAL_DOCUMENT, "Period not found: " + periodName));
    }

    private boolean isPeriodIndex(WbPeriod wbPeriod, String expectedPeriodName) {
        int periodIndex = wbPeriod.getIndex();
        String periodIndexName = String.format("%02d", periodIndex);
        return expectedPeriodName.equals(periodIndexName);
    }

}


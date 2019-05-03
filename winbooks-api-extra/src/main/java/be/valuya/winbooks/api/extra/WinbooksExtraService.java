package be.valuya.winbooks.api.extra;

import be.valuya.accountingtroll.AccountingEventListener;
import be.valuya.accountingtroll.event.ArchiveFileNotFoundIgnoredEvent;
import be.valuya.accountingtroll.event.ArchiveFolderNotFoundIgnoredEvent;
import be.valuya.jbooks.model.WbAccount;
import be.valuya.jbooks.model.WbBookYearFull;
import be.valuya.jbooks.model.WbBookYearStatus;
import be.valuya.jbooks.model.WbClientSupplier;
import be.valuya.jbooks.model.WbDocument;
import be.valuya.jbooks.model.WbEntry;
import be.valuya.jbooks.model.WbParam;
import be.valuya.jbooks.model.WbPeriod;
import be.valuya.winbooks.api.extra.config.WinbooksFileConfiguration;
import be.valuya.winbooks.domain.error.ArchivePathNotFoundException;
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
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.text.MessageFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class WinbooksExtraService {

    public static final Pattern BOOK_YEAR_DOCUMENT_PATTERN = Pattern.compile("^(\\w+)_(\\d+)_(\\d+)_(\\d+).pdf$");
    public static final String ACCOUNT_PICTURE_PARAM_LENGEN = "LENGEN";
    public static final int ACCOUNT_NUMBER_DEFAULT_LENGTH = 6;
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
//
//    public WinbooksSession createSession(WinbooksFileConfiguration winbooksFileConfiguration) {
//        List<WbBookYearFull> bookYears = streamBookYears(winbooksFileConfiguration)
//                .collect(Collectors.toList());
//        return new WinbooksSession(winbooksFileConfiguration, bookYears);
//    }

    public LocalDateTime getActModificationDateTime(WinbooksFileConfiguration winbooksFileConfiguration) {
        Path path = resolveTablePath(winbooksFileConfiguration, ACCOUNTING_ENTRY_TABLE_NAME);
        try {
            FileTime lastModifiedTime = Files.getLastModifiedTime(path);
            return toLocalDateTime(lastModifiedTime);
        } catch (IOException exception) {
            throw new WinbooksException(WinbooksError.UNKNOWN_ERROR, exception);
        }
    }

    public Stream<WbEntry> streamAct(WinbooksFileConfiguration winbooksFileConfiguration, AccountingEventListener eventListener) {
        List<WbBookYearFull> wbBookYearFullList = streamBookYears(winbooksFileConfiguration)
                .collect(Collectors.toList());
        List<WbBookYearFull> archivedBookYearFullList = wbBookYearFullList
                .stream()
                .filter(this::isArchived)
                .collect(Collectors.toList());

        boolean resolveUnmappedPeriodFromEntryDate = winbooksFileConfiguration.isResolveUnmappedPeriodFromEntryDate();
        PeriodResolver periodResolver = new PeriodResolver(resolveUnmappedPeriodFromEntryDate);
        periodResolver.init(wbBookYearFullList);

        WbEntryDbfReader wbEntryDbfReader = new WbEntryDbfReader(periodResolver);

        Stream<WbEntry> archivedEntryStream = archivedBookYearFullList
                .stream()
                .map(WbBookYearFull::getArchivePathNameOptional)
                .flatMap(this::streamOptional)
                .flatMap(archivePathName -> streamArchivedTable(winbooksFileConfiguration, ACCOUNTING_ENTRY_TABLE_NAME, archivePathName, eventListener))
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
            // some book year data may not be present in archived dossiers
            long bookyearKeysCount = paramMap.keySet().stream()
                    .filter(k -> k.startsWith(bookYearParamPrefix))
                    .count();
            if (bookyearKeysCount == 0) {
                continue;
            }
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

    public int getAccountNumberLength(WinbooksFileConfiguration winbooksFileConfiguration) {
        Map<String, String> paramMap = getParamMap(winbooksFileConfiguration);
        String accountPictureValueNullable = paramMap.get("AccountPicture");
        return Optional.ofNullable(accountPictureValueNullable)
                .flatMap(this::getAccountNumberLengthFromAccountPictureParamValue)
                .orElse(ACCOUNT_NUMBER_DEFAULT_LENGTH);
    }

    private Optional<Integer> getAccountNumberLengthFromAccountPictureParamValue(String accountPictureValue) {
        String[] paramValues = accountPictureValue.split(",");
        Map<String, String> accountPictureParams = Arrays.stream(paramValues)
                .map(keyValue -> keyValue.split("="))
                .collect(Collectors.toMap(
                        keyVal -> keyVal[0],
                        keyVal -> keyVal.length < 2 ? null : keyVal[1]
                ));
        return Optional.ofNullable(accountPictureParams.get(ACCOUNT_PICTURE_PARAM_LENGEN))
                .map(Integer::parseInt);
    }


    private Stream<WbBookYearFull> streamBookYearsFromBookyearsTable(WinbooksFileConfiguration winbooksFileConfiguration) {
        WbBookYearFullDbfReader wbBookYearFullDbfReader = new WbBookYearFullDbfReader();
        return streamTable(winbooksFileConfiguration, BOOKYEARS_TABLE_NAME)
                .map(wbBookYearFullDbfReader::readWbBookYearFromSlbkyDbfRecord);
    }

    private Stream<DbfRecord> streamArchivedTable(WinbooksFileConfiguration winbooksFileConfiguration,
                                                  String tableName, String archivePathName,
                                                  AccountingEventListener eventListener) {
        Charset charset = winbooksFileConfiguration.getCharset();

        return getArchivedTableInputStreamOptional(winbooksFileConfiguration, tableName, archivePathName, eventListener)
                .map(tableInputStream -> DbfUtils.streamDbf(tableInputStream, charset))
                .orElseGet(Stream::empty);
    }

    public Stream<DbfRecord> streamTable(WinbooksFileConfiguration winbooksFileConfiguration, String tableName) {
        InputStream tableInputStream = getTableInputStream(winbooksFileConfiguration, tableName);
        Charset charset = winbooksFileConfiguration.getCharset();
        return DbfUtils.streamDbf(tableInputStream, charset);
    }

    public Optional<WinbooksFileConfiguration> createWinbooksFileConfigurationOptional(Path parentPath, String baseName) {
        return resolvePath(parentPath, baseName, true)
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
        winbooksFileConfiguration.setResolveCaseInsensitiveSiblings(true);

        if (!tableExists(winbooksFileConfiguration, ACCOUNTING_ENTRY_TABLE_NAME)) {
            return Optional.empty();
        }

        return Optional.of(winbooksFileConfiguration);
    }

    public Optional<byte[]> getDocumentData(WinbooksFileConfiguration fileConfiguration, WbDocument document, AccountingEventListener winbooksEventHandler) {
        boolean resolveCaseInsensitiveSiblings = fileConfiguration.isResolveCaseInsensitiveSiblings();
        return getDocumentAbsolutePath(fileConfiguration, document, winbooksEventHandler)
                .flatMap(docPath -> getDocumentAllPagesPdfContent(docPath, document, resolveCaseInsensitiveSiblings));
    }

    public Optional<Path> getDocumentAbsolutePath(WinbooksFileConfiguration fileConfiguration, WbDocument document, AccountingEventListener winbooksEventHandler) {
        boolean resolveCaseInsensitiveSiblings = fileConfiguration.isResolveCaseInsensitiveSiblings();
        WbBookYearFull bookYearFull = document.getWbPeriod().getWbBookYearFull();
        return this.streamBasePaths(fileConfiguration, bookYearFull, winbooksEventHandler)
                .map(basePath -> resolveDocumentsPath(basePath, resolveCaseInsensitiveSiblings))
                .map(documentsPath -> resolveDocumentDirectoryPath(documentsPath, document, resolveCaseInsensitiveSiblings))
                .findFirst();
    }

    public Stream<WbDocument> streamBookYearDocuments(WinbooksFileConfiguration fileConfiguration, WbBookYearFull bookYear, AccountingEventListener winbooksEventHandler) {
        String bookYearName = bookYear.getShortName();
        boolean resolveCaseInsensitiveSiblings = fileConfiguration.isResolveCaseInsensitiveSiblings();

        // Stream documents at /${basePath}/${documentsPath}/${bookYearName}/<book year doc format>
        List<Path> rootPathSources = streamBasePaths(fileConfiguration, bookYear, winbooksEventHandler)
                .map(basePath -> resolveDocumentsPath(basePath, resolveCaseInsensitiveSiblings))
                .map(documentsPath -> resolvePath(documentsPath, bookYearName, resolveCaseInsensitiveSiblings))
                .flatMap(this::streamOptional)
                .collect(Collectors.toList());

        return rootPathSources.stream()
                .flatMap(root -> this.streamBookYearDocuments(root, bookYear));
    }

    // Should not be exposed
    @Deprecated
    public Optional<Path> resolvePath(Path parentPath, String fileName, boolean resolveCaseInsensitiveSiblings) {
        if (parentPath == null) {
            return Optional.empty();
        }
        Path defaultPath = parentPath.resolve(fileName);
        if (Files.exists(defaultPath)) {
            return Optional.of(defaultPath);
        }
        boolean parentExists = Files.exists(parentPath);
        if (!parentExists) {
            return Optional.empty();
        }
        if (fileName.endsWith(".dbf")) {
            String capitalizedExtensionFileName = fileName.replace(".dbf", ".DBF");
            Path capitalizedExtensionPath = parentPath.resolve(capitalizedExtensionFileName);
            if (Files.exists(capitalizedExtensionPath)) {
                return Optional.of(capitalizedExtensionPath);
            }
        }
        if (resolveCaseInsensitiveSiblings) {
            return this.resolveCaseInsensitivePathOptional(parentPath, fileName);
        } else {
            return Optional.empty();
        }
    }


    private Optional<Path> findSiblingWithSameName(Path parentPath, String fileName) throws IOException {
        BiPredicate<Path, BasicFileAttributes> predicate = (path, attr) -> isSamePathNameIgnoreCase(path, fileName);
        return Files.find(parentPath, 1, predicate).findFirst();
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

    private Optional<Path> resolveCaseInsensitivePathOptional(Path parentPath, String fileName) {
        try {
            // Find another child of parent with a similar name
            long time0 = System.currentTimeMillis();
            Optional<Path> firstFoundPathOptional = findSiblingWithSameName(parentPath, fileName);
            long timeAfterWalk = System.currentTimeMillis();
            long deltaTimeWalk = timeAfterWalk - time0;
            LOGGER.log(Level.FINE, "****FIND TIME (" + fileName + "): " + deltaTimeWalk);
            return firstFoundPathOptional;
        } catch (IOException exception) {
            throw new WinbooksException(WinbooksError.UNKNOWN_ERROR, exception);
        }
    }

    private boolean isSamePathNameIgnoreCase(Path path, String fileName) {
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

    private Optional<InputStream> getArchivedTableInputStreamOptional(WinbooksFileConfiguration winbooksFileConfiguration,
                                                                      String tableName, String archivePathName,
                                                                      AccountingEventListener eventListener) {
        Path archivedTablePath = resolveArchivedTablePath(winbooksFileConfiguration, tableName, archivePathName);

        boolean ignoreMissingArchives = winbooksFileConfiguration.isIgnoreMissingArchives();
        if (!Files.exists(archivedTablePath) && ignoreMissingArchives) {
            Path archiveFolderPath = archivedTablePath.getParent();
            String archiveName = getPathFileNameString(archiveFolderPath);
            ArchiveFileNotFoundIgnoredEvent ignoredEvent = new ArchiveFileNotFoundIgnoredEvent(archiveFolderPath, archiveName);
            eventListener.handleArchiveFileNotFoundIgnoredEvent(ignoredEvent);
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

    private Optional<Path> resolveArchivePath(Path baseFolderPath, WbBookYearFull wbBookYearFull, boolean resolveCaseInsensitiveSibling) {
        if (!isArchived(wbBookYearFull)) {
            return Optional.empty();
        }
        String archiveFileName = wbBookYearFull.getArchivePathNameOptional().orElseThrow(IllegalStateException::new);
        return resolveArchivePath(baseFolderPath, archiveFileName, resolveCaseInsensitiveSibling);
    }

    private Optional<Path> resolveArchivePath(Path baseFolderPath, String archivePathName, boolean resolveCaseInsensitiveSiblings) {
        String archiveFolderName = archivePathName
                .replace("\\", "/")
                .replaceAll("/$", "")
                .replaceAll("^.*/", "");
        Path baseParent = baseFolderPath.getParent();
        return Optional.ofNullable(baseParent)
                .flatMap(parent -> this.resolvePath(parent, archiveFolderName, resolveCaseInsensitiveSiblings));
    }

    private Path resolveArchivedTablePath(WinbooksFileConfiguration winbooksFileConfiguration, String tableName, String archivePathName) {
        boolean resolveCaseInsensitiveSiblings = winbooksFileConfiguration.isResolveCaseInsensitiveSiblings();
        Path baseFolderPath = winbooksFileConfiguration.getBaseFolderPath();
        Path baseParent = baseFolderPath.getParent();
        Path archivePath = resolveArchivePath(baseFolderPath, archivePathName, resolveCaseInsensitiveSiblings)
                .orElse(baseFolderPath.resolveSibling(archivePathName));
        String archiveFolderName = archivePath.getFileName().toString();

        // Look for archived table in /${archivePath}/${tableFileName}
        Path archiveFolderPath = resolvePath(baseParent, archiveFolderName, resolveCaseInsensitiveSiblings)
                .orElse(baseFolderPath.resolveSibling(archiveFolderName));
        String tableFileName = archiveFolderName + "_" + tableName + ".dbf";

        return resolvePath(archiveFolderPath, tableFileName, resolveCaseInsensitiveSiblings)
                .orElseGet(() -> archiveFolderPath.resolve(tableFileName));  // we return an unexisting path if needed, to at least show a relevant error
    }

    private boolean tableExists(WinbooksFileConfiguration winbooksFileConfiguration, String tableName) {
        return resolveTablePathOptional(winbooksFileConfiguration, tableName)
                .isPresent();
    }

    private Optional<Path> resolveTablePathOptional(WinbooksFileConfiguration winbooksFileConfiguration, String tableName) {
        Path basePath = winbooksFileConfiguration.getBaseFolderPath();
        boolean resolveCaseInsensitiveSiblings = winbooksFileConfiguration.isResolveCaseInsensitiveSiblings();
        String fileName = getTableFileName(winbooksFileConfiguration, tableName);

        // Look for table in /${base}/${fileName}
        return resolvePath(basePath, fileName, resolveCaseInsensitiveSiblings);
    }

    private String getTableFileName(WinbooksFileConfiguration winbooksFileConfiguration, String tableName) {
        String baseName = winbooksFileConfiguration.getBaseName();
        String tablePrefix = baseName.replace("_", "");
        return tablePrefix + "_" + tableName + DBF_EXTENSION;
    }


    private Stream<WbDocument> streamBookYearDocuments(Path bookYearDocumentFolderPath, WbBookYearFull bookYear) {
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
        return streamDirectoryContent(path, this::isDocument)
                .map(documentPath -> getDocumentOptional(documentPath, bookYear, dbkCode))
                .flatMap(this::streamOptional)
                .collect(Collectors.groupingBy(Function.identity(), maxPageNrComparator))
                .values()
                .stream()
                .flatMap(this::streamOptional)
                .map(this::getPageNumberDocument);
    }

    private boolean isDocument(Path documentPath) {
        String fileName = documentPath.getFileName().toString();
        Matcher matcher = BOOK_YEAR_DOCUMENT_PATTERN.matcher(fileName);
        return matcher.matches();
    }

    private WbDocument getPageNumberDocument(WbDocument pageIndexDocument) {
        WbPeriod wbPeriod = pageIndexDocument.getWbPeriod();
        int pageCount = pageIndexDocument.getPageCount() + 1;
        String documentNumber = pageIndexDocument.getDocumentNumber();
        LocalDateTime creationTime = pageIndexDocument.getCreationTime();
        LocalDateTime updatedTime = pageIndexDocument.getUpdatedTime();
        String dbkCode = pageIndexDocument.getDbkCode();

        WbDocument pageNumberDocument = new WbDocument();
        pageNumberDocument.setWbPeriod(wbPeriod);
        pageNumberDocument.setPageCount(pageCount);
        pageNumberDocument.setDocumentNumber(documentNumber);
        pageNumberDocument.setCreationTime(creationTime);
        pageNumberDocument.setUpdatedTime(updatedTime);
        pageNumberDocument.setDbkCode(dbkCode);

        return pageNumberDocument;
    }

    private Optional<byte[]> getDocumentAllPagesPdfContent(Path documentPath, WbDocument document, boolean resolveCaseInsitiveSiblings) {
        return streamDocumentPagesPaths(documentPath, document, resolveCaseInsitiveSiblings)
                .map(this::readAllBytes)
                .reduce(this::mergePdf);
    }

    private Stream<Path> streamBasePaths(WinbooksFileConfiguration fileConfiguration, WbBookYearFull wbBookYearFull, AccountingEventListener winbooksEventHandler) {
        boolean resolveArchivePaths = fileConfiguration.isResolveArchivePaths();
        boolean ignoreMissingArchives = fileConfiguration.isIgnoreMissingArchives();
        boolean resolveCaseInsensitiveSiblings = fileConfiguration.isResolveCaseInsensitiveSiblings();

        Path baseFolderPath = fileConfiguration.getBaseFolderPath();

        // Try to resolve the book year archive path (at /${basePath/../${bookyearArchivePathName})
        // Return this archive path - if any - and the base path
        try {
            if (resolveArchivePaths && isArchived(wbBookYearFull)) {
                Path archivePath = resolveArchivePathOrThrow(baseFolderPath, wbBookYearFull, resolveCaseInsensitiveSiblings);
                return Stream.of(archivePath, baseFolderPath);
            } else {
                return Stream.of(baseFolderPath);
            }
        } catch (ArchivePathNotFoundException archivePathNotFoundException) {
            if (ignoreMissingArchives) {
                String archivePathName = wbBookYearFull.getArchivePathNameOptional()
                        .orElseThrow(IllegalStateException::new);
                Path archivePath = baseFolderPath.resolveSibling(archivePathName);

                ArchiveFolderNotFoundIgnoredEvent ignoredEvent = new ArchiveFolderNotFoundIgnoredEvent(baseFolderPath, archivePath);
                winbooksEventHandler.handleArchiveFolderNotFoundIgnoredEvent(ignoredEvent);
                return Stream.empty();
            } else {
                throw new WinbooksException(WinbooksError.BOOKYEAR_NOT_FOUND, archivePathNotFoundException);
            }
        }
    }

    private Path resolveArchivePathOrThrow(Path baseFolderPath, WbBookYearFull wbBookYearFull, boolean resolveCaseInsitiveSiblings) throws ArchivePathNotFoundException {
        return resolveArchivePath(baseFolderPath, wbBookYearFull, resolveCaseInsitiveSiblings)
                .orElseThrow(() -> new ArchivePathNotFoundException(baseFolderPath, wbBookYearFull));
    }

    private Path resolveDocumentDirectoryPath(Path baseDocumentPath, WbDocument document, boolean resolveCaseInsensitiveSiblings) {
        WbBookYearFull wbBookYearFull = document.getWbPeriod().getWbBookYearFull();
        String bookYearShortName = wbBookYearFull.getShortName();
        String dbCode = document.getDbkCode();
        return resolvePath(baseDocumentPath, bookYearShortName, resolveCaseInsensitiveSiblings)
                .flatMap(bookYearPath -> resolvePath(bookYearPath, dbCode, resolveCaseInsensitiveSiblings))
                .orElseGet(() -> baseDocumentPath.resolve(bookYearShortName).resolve(dbCode));
    }

    private Path resolveDocumentsPath(Path basePath, boolean resolveCaseInsensitiveSiblings) {
        return resolvePath(basePath, "Document", resolveCaseInsensitiveSiblings)
                .orElseGet(() -> basePath.resolve("Document"));
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
            throw new WinbooksException(WinbooksError.USER_FILE_ERROR, "Error while processing pdf files", exception);
        }
    }

    private Stream<Path> streamDirectoryContent(Path path, Predicate<Path> acceptFilePredicate) {
        try {
            return Files.find(path, Integer.MAX_VALUE,
                    (visitedPath, attr) -> checkFindDirectoryPredicate(acceptFilePredicate, visitedPath, attr));
        } catch (IOException exception) {
            throw new WinbooksException(WinbooksError.USER_FILE_ERROR, exception);
        }
    }

    private boolean checkFindDirectoryPredicate(Predicate<Path> acceptPredicate, Path visitedPath, BasicFileAttributes attr) {
        boolean directory = attr.isDirectory();
        if (!directory) {
            return acceptPredicate.test(visitedPath);
        } else {
            return false;
        }
    }

    private Stream<Path> streamDocumentPagesPaths(Path basePath, WbDocument document, boolean resolveCaseInsensitiveSiblings) {
        int pageCount = document.getPageCount();

        return IntStream.range(0, pageCount)
                .mapToObj(pageIndex -> getDocumentPagePathName(pageIndex, document))
                .map(pagePathName -> resolvePath(basePath, pagePathName, resolveCaseInsensitiveSiblings))
                .flatMap(this::streamOptional);
    }

    private String getDocumentPagePathName(int pageIndex, WbDocument document) {
        WbPeriod wbPeriod = document.getWbPeriod();
        int wbPeriodIndex = wbPeriod.getIndex();
        String periodIndexName = String.format("%02d", wbPeriodIndex);
        String pageIndexName = String.format("%02d", pageIndex);

        String fileName = MessageFormat.format("{0}_{1}_{2}_{3}.pdf",
                document.getDbkCode(),
                periodIndexName,
                document.getDocumentNumber(),
                pageIndexName
        );
        return fileName;
    }

    private Optional<WbDocument> getDocumentOptional(Path documentPath, WbBookYearFull bookYear, String expectedDbkCode) {
        String fileName = documentPath.getFileName().toString();
        Matcher matcher = BOOK_YEAR_DOCUMENT_PATTERN.matcher(fileName);
        if (!matcher.matches()) {
            return Optional.empty();
        }
        LocalDateTime lastModifiedLocalTime = getLastModifiedTime(documentPath);
        LocalDateTime creationTime = getCreationTime(documentPath);

        String actualDbkCode = matcher.group(1);
        String periodName = matcher.group(2);
        String documentNumber = matcher.group(3);
        String pageNrStr = matcher.group(4);
        int pageNr = Integer.valueOf(pageNrStr);

        WbDocument wbDocument = new WbDocument();
        wbDocument.setDbkCode(actualDbkCode);
        wbDocument.setDocumentNumber(documentNumber);
        wbDocument.setPageCount(pageNr);
        wbDocument.setWbPeriod(getWbPeriod(bookYear, periodName));
        wbDocument.setUpdatedTime(lastModifiedLocalTime);
        wbDocument.setCreationTime(creationTime);

        return Optional.of(wbDocument);
    }

    private LocalDateTime getLastModifiedTime(Path documentPath) {
        try {
            FileTime lastModifiedTime = Files.getLastModifiedTime(documentPath);
            return toLocalDateTime(lastModifiedTime);
        } catch (IOException exception) {
            throw new WinbooksException(WinbooksError.USER_FILE_ERROR, exception);
        }
    }

    private LocalDateTime getCreationTime(Path documentPath) {
        try {
            BasicFileAttributes basicFileAttributes = Files.readAttributes(documentPath, BasicFileAttributes.class);
            FileTime creationFileTime = basicFileAttributes.creationTime();
            return toLocalDateTime(creationFileTime);
        } catch (IOException exception) {
            throw new WinbooksException(WinbooksError.USER_FILE_ERROR, exception);
        }
    }

    private WbPeriod getWbPeriod(WbBookYearFull bookYear, String periodName) {
        return bookYear.getPeriodList()
                .stream()
                .filter(wbPeriod -> isPeriodIndex(wbPeriod, periodName))
                .findFirst()
                .orElseThrow(() -> new WinbooksException(WinbooksError.NO_PERIOD, "Period not found: " + periodName));
    }

    private boolean isPeriodIndex(WbPeriod wbPeriod, String expectedPeriodName) {
        int periodIndex = wbPeriod.getIndex();
        String periodIndexName = String.format("%02d", periodIndex);
        return expectedPeriodName.equals(periodIndexName);
    }


    private LocalDateTime toLocalDateTime(FileTime fileTime) {
        Instant lastModifiedInstant = fileTime.toInstant();
        return LocalDateTime.ofInstant(lastModifiedInstant, ZoneId.systemDefault());
    }


    private Path resolveTablePath(WinbooksFileConfiguration winbooksFileConfiguration, String tableName) {
        return resolveTablePathOptional(winbooksFileConfiguration, tableName)
                .orElseThrow(() -> {
                    Path baseFolderPath = winbooksFileConfiguration.getBaseFolderPath();
                    String baseFolderPathName = getPathFileNameString(baseFolderPath);

                    String fileName = getTableFileName(winbooksFileConfiguration, tableName);

                    String message = MessageFormat.format("Could not find file {0} in folder {1}", fileName, baseFolderPathName);
                    return new WinbooksException(WinbooksError.DOSSIER_NOT_FOUND, message);
                });
    }


}


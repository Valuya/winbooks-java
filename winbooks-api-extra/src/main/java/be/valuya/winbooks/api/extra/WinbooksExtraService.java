package be.valuya.winbooks.api.extra;

import be.valuya.jbooks.model.WbAccount;
import be.valuya.jbooks.model.WbBookYearFull;
import be.valuya.jbooks.model.WbBookYearStatus;
import be.valuya.jbooks.model.WbEntry;
import be.valuya.jbooks.model.WbParam;
import be.valuya.jbooks.model.WbPeriod;
import be.valuya.winbooks.domain.error.WinbooksError;
import be.valuya.winbooks.domain.error.WinbooksException;
import net.iryndin.jdbf.core.DbfRecord;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.text.MessageFormat;
import java.text.ParseException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class WinbooksExtraService {

    // some invalid String that Winbooks likes to have
    public static final String CHAR0_STRING = Character.toString((char) 0);
    private static final String PARAM_TABLE_NAME = "param";
    private static final String BOOKYEARS_TABLE_NAME = "SLBKY";
    private static final String PERIOD_TABLE_NAME = "SLPRD";
    private static final String ACCOUNT_TABLE_NAME = "ACF";
    private static final String DEFAULT_TABLE_FILE_NAME_REGEX = "^(.*)_" + ACCOUNT_TABLE_NAME + ".DBF$";
    private static final Pattern DEFAULT_TABLE_FILE_NAME_PATTERN = Pattern.compile(DEFAULT_TABLE_FILE_NAME_REGEX, Pattern.CASE_INSENSITIVE);
    private static final String ACCOUNTING_ENTRY_TABLE_NAME = "ACT";
    private static final String DBF_EXTENSION = ".dbf";
    private static final DateTimeFormatter PERIOD_FORMATTER = DateTimeFormatter.ofPattern("ddMMyyyy");

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

    public Stream<WbEntry> streamAct(WinbooksFileConfiguration winbooksFileConfiguration) {
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
                .flatMap(archivePathName -> streamArchivedTable(winbooksFileConfiguration, ACCOUNTING_ENTRY_TABLE_NAME, archivePathName))
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
        return streamTable(winbooksFileConfiguration, ACCOUNT_TABLE_NAME)
                .map(new WbAccountDbfReader()::readWbAccountFromAcfDbfRecord);
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
        return streamTable(winbooksFileConfiguration, BOOKYEARS_TABLE_NAME)
                .map(new WbBookYearFullDbfReader()::readWbBookYearFromSlbkyDbfRecord);
    }

    public Stream<DbfRecord> streamArchivedTable(WinbooksFileConfiguration winbooksFileConfiguration, String tableName, String archivePathName) {
        try {
            InputStream tableInputStream = getArchivedTableInputStream(winbooksFileConfiguration, tableName, archivePathName);
            Charset charset = winbooksFileConfiguration.getCharset();
            return DbfUtils.streamDbf(tableInputStream, charset);
        } catch (WinbooksException winbooksException) {
            WinbooksError winbooksError = winbooksException.getWinbooksError();
            if (winbooksError == WinbooksError.DOSSIER_NOT_FOUND) {
                return Stream.empty(); // TODO: at least report this!!!
            }
            throw winbooksException;
        }
    }

    public Stream<DbfRecord> streamTable(WinbooksFileConfiguration winbooksFileConfiguration, String tableName) {
        InputStream tableInputStream = getTableInputStream(winbooksFileConfiguration, tableName);
        Charset charset = winbooksFileConfiguration.getCharset();
        return DbfUtils.streamDbf(tableInputStream, charset);
    }

    public void dumpDbf(WinbooksFileConfiguration winbooksFileConfiguration, String tableName) {
        try (Stream<DbfRecord> streamTable = streamTable(winbooksFileConfiguration, tableName)) {
            streamTable.forEach(this::dumpDbfRecord);
        }
    }

    public Path resolveTablePath(WinbooksFileConfiguration winbooksFileConfiguration, String tableName) {
        return resolveTablePathOptional(winbooksFileConfiguration, tableName)
                .orElseThrow(() -> {
                    Path baseFolderPath = winbooksFileConfiguration.getBaseFolderPath();
                    String fileName = getTableFileName(winbooksFileConfiguration, tableName);

                    String message = MessageFormat.format("Could not find file {0} in folder {1}", fileName, baseFolderPath.toString());
                    return new WinbooksException(WinbooksError.DOSSIER_NOT_FOUND, message);
                });
    }

    public Optional<Path> resolveCaseInsensitivePathOptional(Path folderPath, String fileName) {
        try {
            if (!Files.exists(folderPath)) {
                return Optional.empty();
            }
            return Files.find(folderPath, 1,
                    (path, attr) -> isSamePathName(path, fileName))
                    .findFirst();
        } catch (IOException exception) {
            throw new WinbooksException(WinbooksError.UNKNOWN_ERROR, exception);
        }
    }

    public Optional<String> findBaseNameOptional(Path customerWinbooksPath) {
        return findBaseNameFromPathOptional(customerWinbooksPath)
                .map(Optional::of)
                .orElseGet(() -> findBaseNameFromPathAndDefaultTableOptional(customerWinbooksPath));
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

    private Optional<String> findBaseNameFromPathAndDefaultTableOptional(Path customerWinbooksPath) {
        try {
            if (!Files.exists(customerWinbooksPath)) {
                return Optional.empty();
            }
            List<String> baseNameCandidates = Files.walk(customerWinbooksPath, 1)
                    .filter(this::isDefaultTablePath)
                    .map(this::getPrefixFromTablePath)
                    .collect(Collectors.toList());
            if (baseNameCandidates.size() > 1) {
                return Optional.empty();
            }

            return baseNameCandidates
                    .stream()
                    .findFirst();
        } catch (IOException exception) {
            throw new WinbooksException(WinbooksError.USER_FILE_ERROR, exception);
        }
    }

    private String getPrefixFromTablePath(Path path) {
        Path fileNamePath = path.getFileName();
        String fileName = fileNamePath.toString();

        Matcher matcher = DEFAULT_TABLE_FILE_NAME_PATTERN.matcher(fileName);
        if (!matcher.matches()) {
            throw new IllegalArgumentException();
        }
        return matcher.group(1);
    }

    private boolean isDefaultTablePath(Path path) {
        Path fileNamePath = path.getFileName();
        String fileName = fileNamePath.toString();
        Matcher matcher = DEFAULT_TABLE_FILE_NAME_PATTERN.matcher(fileName);

        return matcher.matches();
    }

    private Optional<String> findBaseNameFromPathOptional(Path customerWinbooksPath) {
        Path folderNamePath = customerWinbooksPath.getFileName();
        String folderName = folderNamePath.toString();

        WinbooksFileConfiguration winbooksFileConfiguration = new WinbooksFileConfiguration();
        winbooksFileConfiguration.setBaseFolderPath(customerWinbooksPath);
        winbooksFileConfiguration.setBaseName(folderName);

        if (!tableExists(winbooksFileConfiguration, ACCOUNTING_ENTRY_TABLE_NAME)) {
            return Optional.empty();
        }

        return Optional.of(folderName);
    }

    private boolean isSamePathName(Path path, String fileName) {
        return path.getFileName().toString().equalsIgnoreCase(fileName);
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

    private InputStream getArchivedTableInputStream(WinbooksFileConfiguration winbooksFileConfiguration, String tableName, String archivePathName) {
        try {
            Path archivedTablePath = resolveArchivedTablePath(winbooksFileConfiguration, tableName, archivePathName);

            return Files.newInputStream(archivedTablePath);
        } catch (IOException exception) {
            String message = MessageFormat.format("Erreur de lecture d''une table archivée : {0}", archivePathName);
            throw new WinbooksException(WinbooksError.DOSSIER_NOT_FOUND, message, exception);
        }
    }

    private InputStream getTableInputStream(WinbooksFileConfiguration winbooksFileConfiguration, String tableName) {
        try {
            Path path = resolveTablePath(winbooksFileConfiguration, tableName);
            return Files.newInputStream(path);
        } catch (IOException exception) {
            throw new WinbooksException(WinbooksError.UNKNOWN_ERROR, exception);
        }
    }

    private Path resolveArchivedTablePath(WinbooksFileConfiguration winbooksFileConfiguration, String tableName, String archivePathName) {
        String archiveFolderName = archivePathName
                .replace("\\", "/")
                .replaceAll("/$", "")
                .replaceAll("^.*/", "");
        Path unarchivedTablePath = resolveTablePath(winbooksFileConfiguration, tableName);

        String tableFileName = archiveFolderName + "_" + tableName + ".dbf";

        Path parentPath = unarchivedTablePath.getParent();
        Path archiveFolderPath = resolveCaseInsensitivePathOptional(parentPath, archiveFolderName)
                .orElseGet(() -> parentPath.resolveSibling(archiveFolderName)); // we return an unexisting path if needed, to at least show a relevant error

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

    private void dumpDbfRecord(DbfRecord dbfRecord) {
        try {
            int recordNumber = dbfRecord.getRecordNumber();
            Map<String, Object> valueMap = dbfRecord.toMap();

            System.out.println("Record #" + recordNumber + ": " + valueMap);
        } catch (ParseException parseException) {
            throw new WinbooksException(WinbooksError.UNKNOWN_ERROR, parseException);
        }
    }

}


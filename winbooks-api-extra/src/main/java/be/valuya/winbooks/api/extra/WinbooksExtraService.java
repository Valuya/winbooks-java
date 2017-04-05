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
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class WinbooksExtraService {

    private static final String PARAM_TABLE_NAME = "param";
    private static final String BOOKYEARS_TABLE_NAME = "SLBKY";
    private static final String PERIOD_TABLE_NAME = "SLPRD";
    private static final String ACCOUNT_TABLE_NAME = "ACF";
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
        return streamTable(winbooksFileConfiguration, ACCOUNTING_ENTRY_TABLE_NAME, new WbEntryDbfReader()::readWbEntryFromActDbfRecord);
    }

    public Stream<WbAccount> streamAcf(WinbooksFileConfiguration winbooksFileConfiguration) {
        return streamTable(winbooksFileConfiguration, ACCOUNT_TABLE_NAME, new WbAccountDbfReader()::readWbAccountFromAcfDbfRecord);
    }

    public Stream<WbBookYearFull> streamBookYears(WinbooksFileConfiguration winbooksFileConfiguration) {
        if (false && tableExists(winbooksFileConfiguration, BOOKYEARS_TABLE_NAME)) { //TODO: currently, we can get more info out of the badly structured param table
            return streamBookYearsFromBookyearsTable(winbooksFileConfiguration);
        }
        // fall-back: a lot of customers seem not to have table above
        return streamBookYearsFromParamTable(winbooksFileConfiguration).stream();
    }

    public List<WbBookYearFull> streamBookYearsFromParamTable(WinbooksFileConfiguration winbooksFileConfiguration) {
        Map<String, String> paramMap = streamTable(winbooksFileConfiguration, PARAM_TABLE_NAME, new WbParamDbfReader()::readWbParamFromDbfRecord)
                .filter(wbParam -> wbParam.getValue() != null)
                .collect(Collectors.toMap(WbParam::getId, WbParam::getValue, (id1, id2) -> id2));

        List<WbBookYearFull> wbBookYearFullList = new ArrayList<>();

        String bookYearCountStr = paramMap.get("BOOKYEARCOUNT");
        int bookYearCount = Integer.parseInt(bookYearCountStr);
        for (int i = 1; i <= bookYearCount; i++) {
            String bookYearParamPrefix = "BOOKYEAR" + i;
            String bookYearLongLabel = paramMap.get(bookYearParamPrefix + "." + "LONGLABEL");
            String bookYearShortLabel = paramMap.get(bookYearParamPrefix + "." + "SHORTLABEL");

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

    private List<WbPeriod> convertWinbooksPeriods(List<String> periodNames, List<LocalDate> periodDates, int durationInMonths) {
        int periodCount = periodNames.size();
        if (periodDates.size() != periodCount) {
            throw new IllegalArgumentException("Different sizes for period names and period dates.");
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
        int allPeriodCount = allPeriodDatesStr.length();
        for (int i = 0; i + 8 <= allPeriodCount; i += 8) {
            String periodDateStr = allPeriodDatesStr.substring(i, i + 8);
            LocalDate periodDate = LocalDate.parse(periodDateStr, PERIOD_FORMATTER);

            if (i == allPeriodCount - 1) {
                periodDate = periodDate.plusDays(1);
            }

            periodDates.add(periodDate);
        }

        return periodDates;
    }

    public Stream<WbBookYearFull> streamBookYearsFromBookyearsTable(WinbooksFileConfiguration winbooksFileConfiguration) {
        return streamTable(winbooksFileConfiguration, BOOKYEARS_TABLE_NAME, new WbBookYearFullDbfReader()::readWbBookYearFromSlbkyDbfRecord);
    }

    public <T> Stream<T> streamTable(WinbooksFileConfiguration winbooksFileConfiguration, String tableName, Function<DbfRecord, T> readFunction) {
        InputStream tableInputStream = getTableInputStream(winbooksFileConfiguration, tableName);
        Charset charset = winbooksFileConfiguration.getCharset();
        return DbfUtils.streamDbf(tableInputStream, charset)
                .map(readFunction);
    }

    public void dumpDbf(WinbooksFileConfiguration winbooksFileConfiguration, String tableName) {
        try (Stream<DbfRecord> streamTable = streamTable(winbooksFileConfiguration, tableName, Function.identity())) {
            streamTable.forEach(this::dumpDbfRecord);
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

    private boolean tableExists(WinbooksFileConfiguration winbooksFileConfiguration, String tableName) {
        return resolveTablePathOptional(winbooksFileConfiguration, tableName)
                .map(path -> true)
                .orElse(false);
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

    private Optional<Path> resolveTablePathOptional(WinbooksFileConfiguration winbooksFileConfiguration, String tableName) {
        Path basePath = winbooksFileConfiguration.getBaseFolderPath();
        String fileName = getTableFileName(winbooksFileConfiguration, tableName);
        return resolvePathOptional(basePath, fileName);
    }

    public Path resolvePath(Path folderPath, String subFolderName) {
        return Optional.of(folderPath)
                .filter(Files::exists)
                .flatMap(existingFolderPath -> resolvePathOptional(existingFolderPath, subFolderName))
                .orElseGet(() -> folderPath.resolve(subFolderName));
    }

    public Optional<Path> resolvePathOptional(Path folderPath, String fileName) {
        try {
            return Files.find(folderPath, 1,
                    (path, attr) -> isSamePathName(path, fileName))
                    .findFirst();
        } catch (IOException exception) {
            throw new WinbooksException(WinbooksError.UNKNOWN_ERROR, exception);
        }
    }

    protected boolean isSamePathName(Path path, String fileName) {
        return path.getFileName().toString().equalsIgnoreCase(fileName);
    }

    private String getTableFileName(WinbooksFileConfiguration winbooksFileConfiguration, String tableName) {
        String baseName = winbooksFileConfiguration.getBaseName();
        return baseName + "_" + tableName + DBF_EXTENSION;
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

    private void closeInputStream(InputStream tableInputStream) {
        try {
            tableInputStream.close();
        } catch (IOException exception) {
            throw new WinbooksException(WinbooksError.UNKNOWN_ERROR, exception);
        }
    }

}

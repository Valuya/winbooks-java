package be.valuya.winbooks.api.extra;

import be.valuya.jbooks.model.WbAccount;
import be.valuya.jbooks.model.WbBookYearFull;
import be.valuya.jbooks.model.WbEntry;
import be.valuya.jbooks.model.WbParam;
import be.valuya.winbooks.domain.error.WinbooksError;
import be.valuya.winbooks.domain.error.WinbooksException;
import net.iryndin.jdbf.core.DbfRecord;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.MessageFormat;
import java.text.ParseException;
import java.time.LocalDate;
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
    private static final String ACCOUNT_TABLE_NAME = "ACF";
    private static final String ACCOUNTING_ENTRY_TABLE_NAME = "ACT";
    private static final String DBF_EXTENSION = ".dbf";
    private static final DateTimeFormatter PERIOD_FORMATTER = DateTimeFormatter.ofPattern("ddMMyyyy");

    public Stream<WbEntry> streamAct(WinbooksFileConfiguration winbooksFileConfiguration) {
        return streamTable(winbooksFileConfiguration, ACCOUNTING_ENTRY_TABLE_NAME, new WbEntryDbfReader()::readWbEntryFromActDbfRecord);
    }

    public Stream<WbAccount> streamAcf(WinbooksFileConfiguration winbooksFileConfiguration) {
        return streamTable(winbooksFileConfiguration, ACCOUNT_TABLE_NAME, new WbAccountDbfReader()::readWbAccountFromAcfDbfRecord);
    }

    public Stream<WbBookYearFull> streamBookYears(WinbooksFileConfiguration winbooksFileConfiguration) {
        if (tableExists(winbooksFileConfiguration, BOOKYEARS_TABLE_NAME)) {
            return streamBookYearsFromBookyearsTable(winbooksFileConfiguration);
        }
        // fall-back: a lot of customers seem not to have table above
        return streamBookYearsFromParamTable(winbooksFileConfiguration).stream();
    }

    public List<WbBookYearFull> streamBookYearsFromParamTable(WinbooksFileConfiguration winbooksFileConfiguration) {
        Map<String, String> paramMap = streamTable(winbooksFileConfiguration, PARAM_TABLE_NAME, new WbParamDbfReader()::readWbParamFromDbfRecord)
                .filter(wbParam -> wbParam.getValue() != null)
                .collect(Collectors.toMap(WbParam::getId, WbParam::getValue));

        List<WbBookYearFull> wbBookYearFullList = new ArrayList<>();

        String bookYearCountStr = paramMap.get("BOOKYEARCOUNT");
        int bookYearCount = Integer.parseInt(bookYearCountStr);
        for (int i = 1; i <= bookYearCount; i++) {
            String bookYearParamPrefix = "BOOKYEAR" + i;
            String bookYearLongLabel = paramMap.get(bookYearParamPrefix + "." + "LONGLABEL");
            String bookYearShortLabel = paramMap.get(bookYearParamPrefix + "." + "SHORTLABEL");
            String perDatesStr = paramMap.get(bookYearParamPrefix + "." + "PERDATE");

            List<LocalDate> periodDates = parsePeriodDates(perDatesStr);
            LocalDate startDate = periodDates.stream().findFirst()
                    .orElseThrow(IllegalArgumentException::new);
            LocalDate endDate = periodDates.stream().max(LocalDate::compareTo)
                    .map(date -> date.plusDays(1)) // exclusive upper bound is day after
                    .orElseThrow(IllegalArgumentException::new);

            int startYear = startDate.getYear();
            int endYear = endDate.getYear();

            WbBookYearFull wbBookYearFull = new WbBookYearFull();
            wbBookYearFull.setLongName(bookYearLongLabel);
            wbBookYearFull.setShortName(bookYearShortLabel);
            wbBookYearFull.setIndex(i);
            wbBookYearFull.setStartDate(startDate);
            wbBookYearFull.setEndDate(endDate);
            wbBookYearFull.setYearBeginInt(startYear);
            wbBookYearFull.setYearEndInt(endYear);
            wbBookYearFull.setPeriods(12);

            wbBookYearFullList.add(wbBookYearFull);
        }

        return wbBookYearFullList;
    }

    private List<LocalDate> parsePeriodDates(String allPeriodDatesStr) {
        List<LocalDate> periodDates = new ArrayList<>();
        for (int i = 0; i + 8 <= allPeriodDatesStr.length(); i += 8) {
            String periodDateStr = allPeriodDatesStr.substring(i, i + 8);
            LocalDate periodDate = LocalDate.parse(periodDateStr, PERIOD_FORMATTER);
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

    private Path resolveTablePath(WinbooksFileConfiguration winbooksFileConfiguration, String tableName) {
        return resolveTablePathOptional(winbooksFileConfiguration, tableName)
                .orElseThrow(() -> {
                    Path baseFolderPath = winbooksFileConfiguration.getBaseFolderPath();
                    String fileName = getTableFileName(winbooksFileConfiguration, tableName);

                    String message = MessageFormat.format("Could not find file {0} in folder {1}", fileName, baseFolderPath.toString());
                    return new WinbooksException(WinbooksError.DOSSIER_NOT_FOUND, message);
                });
    }

    private Optional<Path> resolveTablePathOptional(WinbooksFileConfiguration winbooksFileConfiguration, String tableName) {
        try {
            Path baseFolderPath = winbooksFileConfiguration.getBaseFolderPath();
            String fileName = getTableFileName(winbooksFileConfiguration, tableName);
            return Files.find(baseFolderPath, 1,
                    (path, attr) -> path.getFileName().toString().equalsIgnoreCase(fileName))
                    .findFirst();
        } catch (IOException exception) {
            throw new WinbooksException(WinbooksError.UNKNOWN_ERROR, exception);
        }
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

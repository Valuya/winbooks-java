package be.valuya.winbooks.api.extra;

import be.valuya.jbooks.model.*;
import be.valuya.winbooks.domain.error.WinbooksError;
import be.valuya.winbooks.domain.error.WinbooksException;
import net.iryndin.jdbf.core.DbfRecord;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.text.ParseException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class WinbooksExtraServiceLocalTest {

    private WinbooksExtraService winbooksExtraService;
    private WinbooksFileConfiguration winbooksFileConfiguration;

    private Logger logger = Logger.getLogger(WinbooksExtraServiceLocalTest.class.getName());

    @Before
    public void setup() {
        winbooksExtraService = new WinbooksExtraService();

        String baseFolderLocation = System.getProperty("winbooks.test.folder");
        String baseName = System.getProperty("winbooks.test.base.name");

        Path baseFolderPath = Paths.get(baseFolderLocation)
                .resolve(baseName);
        winbooksFileConfiguration = winbooksExtraService.createWinbooksFileConfigurationOptional(baseFolderPath, baseName)
                .orElseThrow(AssertionError::new);
        winbooksFileConfiguration.setReadTablesToMemory(true);
    }

    @Test
    public void testStreamDocuments() {
        WinbooksSession winbooksSession = winbooksExtraService.createSession(winbooksFileConfiguration);
        winbooksExtraService.streamDocuments(winbooksSession)
        .forEach(this::printDocument);
    }

    @Test
    public void testReadDBF() {
        dumpDbf(winbooksFileConfiguration, "act");
        dumpDbf(winbooksFileConfiguration, "acf");
        dumpDbf(winbooksFileConfiguration, "csf");
    }

    @Test
    public void testStreamBookYears() {
        winbooksExtraService.streamBookYears(winbooksFileConfiguration)
                .forEach(this::printBookYear);
    }

    @Test
    public void testListCustomers() {
        winbooksExtraService.streamCsf(winbooksFileConfiguration)
                .map(WbClientSupplier::getName1)
                .forEach(logger::info);
    }

    @Test
    public void testFindDistinctDocOrder() {
        winbooksExtraService.streamAct(winbooksFileConfiguration, this::logWinbooksEvent)
                .map(WbEntry::getWbDocOrderType)
                .distinct()
                .map(WbDocOrderType::name)
                .forEach(logger::info);
    }

    @Test
    public void testFindDistinctDocStatus() {
        winbooksExtraService.streamAct(winbooksFileConfiguration, this::logWinbooksEvent)
                .map(WbEntry::getDocStatus)
                .distinct()
                .map(WbDocStatus::name)
                .forEach(logger::info);
    }

    @Test
    public void testAccountTotal() {
        Date startDate = new Date(119, Calendar.JANUARY, 01);
        Date endDate = new Date(999, Calendar.JANUARY, 01);
        TreeMap<String, Map<Integer, BigDecimal>> categoryMonthTotalMap = winbooksExtraService.streamAct(winbooksFileConfiguration, this::logWinbooksEvent)
                .filter(wbEntry -> wbEntry.getDate() != null)
                .filter(wbEntry -> !wbEntry.getDate().before(startDate))
                .filter(wbEntry -> wbEntry.getDate().before(endDate))
//                .filter(wbEntry -> wbEntry.getComment() != null && wbEntry.getComment().equals("LOYER 20/06-19/07/2016"))
                .filter(wbEntry -> wbEntry.getAccountGl() != null)
                .filter(wbEntry -> wbEntry.getAccountGl().substring(0, 2).equals("70"))
                .peek(wbEntry -> logger.info(wbEntry.toString()))
                .collect(
                        Collectors.groupingBy(
                                wbEntry -> wbEntry.getAccountGl().substring(0, 2),
                                TreeMap::new,
                                Collectors.groupingBy(
                                        wbEntry -> wbEntry.getDate().getMonth(),
                                        Collectors.reducing(BigDecimal.ZERO, WbEntry::getAmountEur, BigDecimal::add))
                        )
                );

        categoryMonthTotalMap.forEach((accountNumber, monthTotalMap) -> {
            BigDecimal accountTotal = monthTotalMap.values().stream()
                    .collect(Collectors.reducing(BigDecimal::add))
                    .orElse(BigDecimal.ZERO);
            monthTotalMap.forEach((month, total) -> logger.info("Account " + accountNumber + ", month " + month + ": " + total));
            logger.info("Account " + accountNumber + ": " + accountTotal);
        });
    }

    @Test
    public void testGetAccountDescription() {
        String description = winbooksExtraService.streamAcf(winbooksFileConfiguration)
                .filter(wbAccount -> wbAccount.getAccountNumber() != null)
                .filter(wbAccount -> wbAccount.getAccountNumber().equals("700000"))
                .map(WbAccount::getName11)
                .findAny()
                .orElseThrow(IllegalArgumentException::new);

        logger.info("Account: " + description);
    }

    private void printBookYear(WbBookYearFull wbBookYearFull) {
        logger.info(wbBookYearFull.toString());
        wbBookYearFull.getPeriodList()
                .stream()
                .map(WbPeriod::toString)
                .forEach(logger::info);
    }

    private void logWinbooksEvent(WinbooksEvent winbooksEvent) {
        WinbooksEventCategory winbooksEventCategory = winbooksEvent.getWinbooksEventCategory();
        String message = winbooksEvent.getMessage();
        List<Object> arguments = winbooksEvent.getArguments();
        WinbooksEventType winbooksEventType = winbooksEventCategory.getWinbooksEventType();

        Level level;
        switch (winbooksEventType) {
            case INFO:
                level = Level.INFO;
                break;
            case WARNING:
                level = Level.WARNING;
                break;
            case ERROR:
                level = Level.SEVERE;
                break;
            default:
                throw new AssertionError("Unknown winbooks event type: " + winbooksEventType);
        }

        Object[] argumentArray = arguments.toArray();
        logger.log(level, message, argumentArray);
    }

    private void dumpDbf(WinbooksFileConfiguration winbooksFileConfiguration, String tableName) {
        try (Stream<DbfRecord> streamTable = winbooksExtraService.streamTable(winbooksFileConfiguration, tableName)) {
            streamTable.forEach(this::dumpDbfRecord);
        }
    }

    private void dumpDbfRecord(DbfRecord dbfRecord) {
        try {
            int recordNumber = dbfRecord.getRecordNumber();
            Map<String, Object> valueMap = dbfRecord.toMap();

            logger.info("Record #" + recordNumber + ": " + valueMap);
        } catch (ParseException parseException) {
            throw new WinbooksException(WinbooksError.UNKNOWN_ERROR, parseException);
        }
    }

    private void printDocument(WbDocument wbDocument) {
        String name = wbDocument.getName();
        String dbCode = wbDocument.getDbCode();
        int pageCount = wbDocument.getPageCount();
        WbPeriod wbPeriod = wbDocument.getWbPeriod();
        String periodName = wbPeriod.getShortName();
        String message = MessageFormat.format("Document: {0}, dbk = {1}, period = {2}, page count = {3}", name, dbCode, periodName, pageCount);
        logger.info(message);
    }
}

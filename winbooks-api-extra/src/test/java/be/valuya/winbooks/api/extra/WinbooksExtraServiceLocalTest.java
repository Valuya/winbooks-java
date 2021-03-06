package be.valuya.winbooks.api.extra;

import be.valuya.jbooks.model.WbAccount;
import be.valuya.jbooks.model.WbBookYearFull;
import be.valuya.jbooks.model.WbClientSupplier;
import be.valuya.jbooks.model.WbDocOrderType;
import be.valuya.jbooks.model.WbDocStatus;
import be.valuya.jbooks.model.WbDocument;
import be.valuya.jbooks.model.WbEntry;
import be.valuya.jbooks.model.WbPeriod;
import be.valuya.winbooks.api.LocalWinbooksDossierCategory;
import be.valuya.winbooks.api.extra.config.WinbooksFileConfiguration;
import be.valuya.winbooks.domain.error.WinbooksConfigurationException;
import be.valuya.winbooks.domain.error.WinbooksError;
import be.valuya.winbooks.domain.error.WinbooksException;
import com.lowagie.text.pdf.PdfReader;
import net.iryndin.jdbf.core.DbfRecord;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.math.BigDecimal;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RunWith(JUnit4.class)
@Category(LocalWinbooksDossierCategory.class)
public class WinbooksExtraServiceLocalTest {

    private WinbooksExtraService winbooksExtraService;
    private WinbooksFileConfiguration winbooksFileConfiguration;

    private Logger logger = Logger.getLogger(WinbooksExtraServiceLocalTest.class.getName());


    @Before
    public void setup() throws WinbooksConfigurationException {
        winbooksExtraService = new WinbooksExtraService();

        String rootPath = System.getProperty("winbooks.test.folder");
//        String basePath = System.getProperty("winbooks.test.base.path");
        String baseName = System.getProperty("winbooks.test.base.name");

        winbooksFileConfiguration = winbooksExtraService.createWinbooksFileConfiguration(
                Paths.get(rootPath), baseName, Map.of());
        winbooksFileConfiguration.setReadTablesToMemory(true);
    }

    @Test
    public void testStreamDocuments() {
        winbooksExtraService.streamBookYears(winbooksFileConfiguration)
                .flatMap(year -> winbooksExtraService.streamBookYearDocuments(winbooksFileConfiguration, year))
                .peek(this::checkDocument)
                .forEach(this::printDocument);
    }

    @Test
    public void testStreamDocumentPageData() throws Exception {
        WbDocument testDocument = winbooksExtraService.streamBookYears(winbooksFileConfiguration)
                .flatMap(year -> winbooksExtraService.streamBookYearDocuments(winbooksFileConfiguration, year))
                .filter(doc -> doc.getPartCount() > 0)
                .findAny()
                .orElseThrow(AssertionError::new);

        this.printDocument(testDocument);
        byte[] documentData = winbooksExtraService.getDocumentData(winbooksFileConfiguration, testDocument)
                .orElseThrow(AssertionError::new);

        PdfReader pdfReader = new PdfReader(documentData);
        int pageCount = pdfReader.getNumberOfPages();
        int expectedMinPageCount = testDocument.getPartCount();
        Assert.assertTrue(pageCount >= expectedMinPageCount);
        Map<?, ?> infoMap = pdfReader.getInfo();
        infoMap.forEach((key, value) -> logger.info(key + " = " + value));
        int fileLength = pdfReader.getFileLength();
        Assert.assertFalse("File should not be empty", fileLength == 0);
        logger.info("File size: " + fileLength);
    }

    @Test
    public void testReadDBF() {
//        dumpDbf(winbooksFileConfiguration, "act");
        dumpDbf(winbooksFileConfiguration, "acf");
//        dumpDbf(winbooksFileConfiguration, "csf");
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
    public void testStreamEntries() {
        winbooksExtraService.streamAct(winbooksFileConfiguration)
                .map(WbEntry::toString)
                .forEach(logger::info);
    }

    @Test
    public void testFindDistinctDocOrder() {
        winbooksExtraService.streamAct(winbooksFileConfiguration)
                .map(WbEntry::getWbDocOrderType)
                .distinct()
                .map(WbDocOrderType::name)
                .forEach(logger::info);
    }

    @Test
    public void testFindDistinctDocStatus() {
        winbooksExtraService.streamAct(winbooksFileConfiguration)
                .map(WbEntry::getDocStatus)
                .distinct()
                .map(WbDocStatus::name)
                .forEach(logger::info);
    }

    @Test
    public void testAccountTotal() {
        Date startDate = new Date(119, Calendar.JANUARY, 01);
        Date endDate = new Date(999, Calendar.JANUARY, 01);
        TreeMap<String, Map<Integer, BigDecimal>> categoryMonthTotalMap = winbooksExtraService.streamAct(winbooksFileConfiguration)
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

    private void checkDocument(WbDocument wbDocument) {
        int pageCount = wbDocument.getPartCount();
        Assert.assertFalse(pageCount == 0);
    }

    private void printBookYear(WbBookYearFull wbBookYearFull) {
        logger.info(wbBookYearFull.toString());
        wbBookYearFull.getPeriodList()
                .stream()
                .map(WbPeriod::toString)
                .forEach(logger::info);
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
//
//            boolean van = valueMap.entrySet().stream()
//                    .anyMatch(e -> e.getKey().equalsIgnoreCase("accountrp") && e.getValue() != null && e.getValue().toString().equalsIgnoreCase("004"));
////                    .map(e->e.getValue())
////                    .anyMatch(v -> v instanceof String && ((String) v).equalsIgnoreCase("004"));
////            .anyMatch(v -> v instanceof BigDecimal && ((BigDecimal) v).compareTo(BigDecimal.valueOf(26323, 2)) == 0);
//            if (!van) {
//                return;
//            }

            logger.info("Record #" + recordNumber + ": " + valueMap);
        } catch (ParseException parseException) {
            throw new WinbooksException(WinbooksError.UNKNOWN_ERROR, parseException);
        }
    }

    private void printDocument(WbDocument wbDocument) {
        String name = wbDocument.getDocumentNumber();
        String dbCode = wbDocument.getDbkCode();
        int partCount = wbDocument.getPartCount();
        String fileNameTemplate = wbDocument.getFileNameTemplate();
        WbPeriod wbPeriod = wbDocument.getWbPeriod();
        String periodName = wbPeriod.getShortName();
        String message = MessageFormat.format("Document: {0}, dbk = {1}, period = {2}, part count = {3}, fileName = {4}",
                name, dbCode, periodName, partCount, fileNameTemplate);
        logger.info(message);
    }
}

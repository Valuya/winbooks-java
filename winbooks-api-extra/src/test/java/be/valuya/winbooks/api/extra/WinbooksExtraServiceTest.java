package be.valuya.winbooks.api.extra;

import be.valuya.jbooks.model.WbAccount;
import be.valuya.jbooks.model.WbBookYearFull;
import be.valuya.jbooks.model.WbDocOrderType;
import be.valuya.jbooks.model.WbDocStatus;
import be.valuya.jbooks.model.WbEntry;
import be.valuya.jbooks.model.WbPeriod;
import be.valuya.winbooks.domain.error.WinbooksError;
import be.valuya.winbooks.domain.error.WinbooksException;
import com.github.robtimus.filesystems.ftp.ConnectionMode;
import com.github.robtimus.filesystems.ftp.FTPEnvironment;
import net.iryndin.jdbf.core.DbfRecord;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Yannick
 */

public class WinbooksExtraServiceTest {

    private static String FTP_USER_NAME;
    private static String FTP_PASSWORD;
    private static String FTP_HOST_NAME;
    private static boolean FTP_SSL_ENABLED;
    private static String PROTOCOL;
    private static String FTP_PATH_NAME;
    private static String BASE_NAME;
    private static FileSystem FILESYSTEM;

    private WinbooksExtraService winbooksExtraService;
    private WinbooksFileConfiguration winbooksFileConfiguration;

    private Logger logger = Logger.getLogger(WinbooksExtraServiceTest.class.getName());

    @BeforeClass
    public static void initFileSystem() throws IOException {
        FTP_USER_NAME = System.getProperty("winbooks.test.ftp.user.name");
        FTP_PASSWORD = System.getProperty("winbooks.test.ftp.password");
        FTP_HOST_NAME = System.getProperty("winbooks.test.ftp.host");
        String sslEnabledStr = System.getProperty("winbooks.test.ftp.ssl");
        FTP_SSL_ENABLED = Boolean.parseBoolean(sslEnabledStr);
        PROTOCOL = FTP_SSL_ENABLED ? "ftps" : "ftp";
        FTP_PATH_NAME = System.getProperty("winbooks.test.ftp.path");
        BASE_NAME = System.getProperty("winbooks.test.base.name");

        char[] passwordChars = FTP_PASSWORD.toCharArray();
        FTPEnvironment ftpEnvironment = new FTPEnvironment()
                .withConnectionMode(ConnectionMode.PASSIVE)
                .withCredentials(FTP_USER_NAME, passwordChars);
        String uriStr = MessageFormat.format("{0}://{1}", PROTOCOL, FTP_HOST_NAME);
        URI uri = URI.create(uriStr);
        FILESYSTEM = FileSystems.newFileSystem(uri, ftpEnvironment);
    }

    @AfterClass
    public static void closeFileSystem() throws IOException {
        if (FILESYSTEM != null) {
            FILESYSTEM.close();
        }
    }

    @Before
    public void setup() {
        winbooksExtraService = new WinbooksExtraService();

        String uriStr = MessageFormat.format("{0}://{1}@{2}", PROTOCOL, FTP_USER_NAME, FTP_HOST_NAME);
        URI uri = URI.create(uriStr);
        Path ftpBasePath = Paths.get(uri)
                .resolve(FTP_PATH_NAME);
        winbooksFileConfiguration = winbooksExtraService.createWinbooksFileConfigurationOptional(ftpBasePath, BASE_NAME)
                .orElseThrow(AssertionError::new);
        winbooksFileConfiguration.setReadTablesToMemory(true);
    }

    @Test
    public void testGuessBaseName() {
        String baseName = winbooksFileConfiguration.getBaseName();
        Assert.assertTrue(baseName.equalsIgnoreCase(BASE_NAME));
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
        Date startDate = new Date(116, Calendar.JANUARY, 01);
        Date endDate = new Date(117, Calendar.JANUARY, 01);
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
            monthTotalMap.forEach((month, total) -> System.out.println("Account " + accountNumber + ", month " + month + ": " + total));
            System.out.println("Account " + accountNumber + ": " + accountTotal);
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

        System.out.println("Account: " + description);
    }

    private void printBookYear(WbBookYearFull wbBookYearFull) {
        System.out.println(wbBookYearFull);
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

            System.out.println("Record #" + recordNumber + ": " + valueMap);
        } catch (ParseException parseException) {
            throw new WinbooksException(WinbooksError.UNKNOWN_ERROR, parseException);
        }
    }
}

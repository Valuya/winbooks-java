package be.valuya.winbooks.api.extra;

import be.valuya.jbooks.model.WbAccount;
import be.valuya.jbooks.model.WbBookYearFull;
import be.valuya.jbooks.model.WbDocOrderType;
import be.valuya.jbooks.model.WbDocStatus;
import be.valuya.jbooks.model.WbEntry;
import be.valuya.jbooks.model.WbPeriod;
import be.valuya.winbooks.api.FtpWinbooksDossierCategory;
import be.valuya.winbooks.api.extra.config.WinbooksFileConfiguration;
import be.valuya.winbooks.domain.error.WinbooksConfigurationException;
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
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Yannick
 */
@RunWith(JUnit4.class)
@Category(FtpWinbooksDossierCategory.class)
public class WinbooksExtraServiceFtpTest {

    private static String FTP_USER_NAME;
    private static String FTP_HOST_NAME;
    private static String PROTOCOL;
    private static String FTP_PATH_NAME;
    private static String BASE_NAME;
    private static String ROOT_PATH_MAPPINGS;
    private static FileSystem FILESYSTEM;

    private WinbooksExtraService winbooksExtraService;
    private WinbooksFileConfiguration winbooksFileConfiguration;

    private Logger logger = Logger.getLogger(WinbooksExtraServiceFtpTest.class.getName());

    @BeforeClass
    public static void initFileSystem() throws IOException {
        FTP_USER_NAME = System.getProperty("winbooks.test.ftp.user.name");
        String FTP_PASSWORD = System.getProperty("winbooks.test.ftp.password");
        FTP_HOST_NAME = System.getProperty("winbooks.test.ftp.host");
        String sslEnabledStr = System.getProperty("winbooks.test.ftp.ssl");
        boolean FTP_SSL_ENABLED = Boolean.parseBoolean(sslEnabledStr);
        PROTOCOL = FTP_SSL_ENABLED ? "ftps" : "ftp";
        FTP_PATH_NAME = System.getProperty("winbooks.test.ftp.path");
        ROOT_PATH_MAPPINGS = System.getProperty("winbooks.test.ftp.path.mappings");
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
    public void setup() throws WinbooksConfigurationException {
        winbooksExtraService = new WinbooksExtraService();

        String uriStr = MessageFormat.format("{0}://{1}@{2}", PROTOCOL, FTP_USER_NAME, FTP_HOST_NAME);
        URI uri = URI.create(uriStr);
        Path ftpBasePath = Paths.get(uri)
                .resolve(FTP_PATH_NAME);
        winbooksFileConfiguration = winbooksExtraService.createWinbooksFileConfiguration(ftpBasePath, BASE_NAME);
        winbooksFileConfiguration.setReadTablesToMemory(true);
        winbooksFileConfiguration.setResolveArchivedBookYears(true);


        Map<String, Path> pathMappings = Arrays.stream(ROOT_PATH_MAPPINGS.split(","))
                .collect(Collectors.toMap(
                        Function.identity(),
                        mappingName -> ftpBasePath
                ));
        winbooksFileConfiguration.setPathMappings(pathMappings);
    }

    @Test
    public void testGuessBaseName() {
        String baseName = winbooksFileConfiguration.getWinbooksCompanyName();
        Assert.assertTrue(baseName.equalsIgnoreCase(BASE_NAME));
    }

    @Test
    public void testReadDBF() {
//        dumpDbf(winbooksFileConfiguration, "act");
//        dumpDbf(winbooksFileConfiguration, "acf");
        dumpDbf(winbooksFileConfiguration, "logbook");
//        dumpDbf(winbooksFileConfiguration, "csf");
    }

    @Test
    public void testStreamBookYears() {
        winbooksExtraService.streamBookYears(winbooksFileConfiguration)
                .forEach(this::printBookYear);
    }

    @Test
    public void testStreamEntries() {
        winbooksExtraService.streamAct(winbooksFileConfiguration)
//                .filter(e -> {
//                    if (e.getAmount() == null) return false;
//                    BigDecimal remaining = e.getAmount().subtract(BigDecimal.valueOf(109.3)).abs();
//                    return remaining.compareTo(BigDecimal.valueOf(0.01d)) < 0;
//                })
                .filter(e -> {
//                    WbBookYearFull wbBookYearFull = e.getWbBookYearFull();
                    boolean testAccount = e.getAccountRp() != null && e.getAccountRp().equalsIgnoreCase("MCOTELECOM");
                    return testAccount;
                })
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
        Date startDate = new Date(116, Calendar.JANUARY, 01);
        Date endDate = new Date(117, Calendar.JANUARY, 01);
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
//        wbBookYearFull.getPeriodList()
//                .stream()
//                .map(WbPeriod::toString)
//                .forEach(logger::info);
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

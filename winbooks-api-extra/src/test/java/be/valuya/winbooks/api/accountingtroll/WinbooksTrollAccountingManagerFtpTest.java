package be.valuya.winbooks.api.accountingtroll;

import be.valuya.accountingtroll.domain.ATAccountingEntry;
import be.valuya.winbooks.api.FtpWinbooksDossierCategory;
import be.valuya.winbooks.api.extra.WinbooksExtraService;
import be.valuya.winbooks.api.extra.config.DocumentMatchingMode;
import be.valuya.winbooks.api.extra.config.WinbooksFileConfiguration;
import be.valuya.winbooks.domain.error.WinbooksConfigurationException;
import com.github.robtimus.filesystems.ftp.ConnectionMode;
import com.github.robtimus.filesystems.ftp.FTPEnvironment;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@RunWith(JUnit4.class)
@Category(FtpWinbooksDossierCategory.class)
public class WinbooksTrollAccountingManagerFtpTest {


    private static String FTP_USER_NAME;
    private static String FTP_HOST_NAME;
    private static String PROTOCOL;
    private static String FTP_PATH_NAME;
    private static String BASE_NAME;
    private static FileSystem FILESYSTEM;


    private WinbooksTrollAccountingManager trollSrervice;

    @BeforeClass
    public static void initFileSystem() throws IOException {
        FTP_USER_NAME = System.getProperty("winbooks.test.ftp.user.name");
        String FTP_PASSWORD = System.getProperty("winbooks.test.ftp.password");
        FTP_HOST_NAME = System.getProperty("winbooks.test.ftp.host");
        String sslEnabledStr = System.getProperty("winbooks.test.ftp.ssl");
        boolean FTP_SSL_ENABLED = Boolean.parseBoolean(sslEnabledStr);
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
    public void setup() throws WinbooksConfigurationException {
        WinbooksExtraService winbooksExtraService = new WinbooksExtraService();

        String uriStr = MessageFormat.format("{0}://{1}@{2}", PROTOCOL, FTP_USER_NAME, FTP_HOST_NAME);
        URI uri = URI.create(uriStr);
        Path ftpBasePath = Paths.get(uri)
                .resolve(FTP_PATH_NAME);
        WinbooksFileConfiguration winbooksFileConfiguration = winbooksExtraService.createWinbooksFileConfigurationOptional(ftpBasePath, BASE_NAME);
        winbooksFileConfiguration.setDocumentMatchingMode(DocumentMatchingMode.EAGERLY_CACHE_ALL_DOCUMENTS);
        winbooksFileConfiguration.setResolveArchivedBookYears(true);
        winbooksFileConfiguration.setResolveDocumentTimes(false);

        trollSrervice = new WinbooksTrollAccountingManager(winbooksFileConfiguration);
    }


    @Test
    public void testStreamAccounts() {
        trollSrervice.streamAccounts()
                .forEach(this::debug);
    }

    @Test
    public void testStreamBookYears() {
        trollSrervice.streamBookYears()
                .forEach(this::debug);
    }

    @Test
    public void testStreamPeriods() {
        trollSrervice.streamPeriods()
                .forEach(this::debug);
    }

    @Test
    public void testStreamThirdParties() {
        trollSrervice.streamThirdParties()
                .forEach(this::debug);
    }

    @Test
    public void testStreamEntries() {
        trollSrervice.streamAccountingEntries()
                .forEach(this::debug);
    }

    @Test
    public void testStreamEntriesTime() {
        long startTime = System.currentTimeMillis();
        List<ATAccountingEntry> allEntries = trollSrervice.streamAccountingEntries()
                .filter(e -> e.getBookPeriod().getStartDate().getYear() == 2017)
                .collect(Collectors.toList());
        long withDocumentCount = allEntries.stream()
                .filter(e -> e.getDocumentOptional().isPresent())
                .count();
        long withoutDocumentCount = allEntries.stream()
                .filter(e -> !e.getDocumentOptional().isPresent())
                .count();
        long endTime = System.currentTimeMillis();

        Duration duration = Duration.of(endTime - startTime, ChronoUnit.MILLIS);

        System.out.println("Time: " + duration.toString() + " .With doc: " + withDocumentCount + ", without: " + withoutDocumentCount);

//        trollSrervice.streamAccountingEntries()
//                .forEach(this::debug);
    }


    @Test
    public void testStreamBalances() {
        trollSrervice.streamAccountingEntries()
                .forEach(this::debug);
    }

    private void debug(Object valueObject) {
        System.out.println(valueObject);
    }

}

package be.valuya.winbooks.api.accountingtroll;

import be.valuya.winbooks.api.extra.WinbooksExtraService;
import be.valuya.winbooks.api.extra.WinbooksFileConfiguration;
import be.valuya.winbooks.api.extra.WinbooksSession;
import org.junit.Before;
import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;

public class WinbooksTrollAccountingServiceTest {


    private WinbooksTrollAccountingService trollSrervice;
    private WinbooksSession session;

    @Before
    public void setup() {
        trollSrervice = new WinbooksTrollAccountingService();
        WinbooksExtraService extraService = new WinbooksExtraService();

        String baseFolderLocation = System.getProperty("winbooks.test.folder");
        String baseName = System.getProperty("winbooks.test.base.name");

        Path baseFolderPath = Paths.get(baseFolderLocation)
                .resolve(baseName);
        WinbooksFileConfiguration winbooksFileConfiguration = extraService.createWinbooksFileConfigurationOptional(baseFolderPath, baseName)
                .orElseThrow(AssertionError::new);
        winbooksFileConfiguration.setReadTablesToMemory(true);

        session = extraService.createSession(winbooksFileConfiguration);

//        eventHandler = winbooksEvent -> logger.info(winbooksEvent.getMessage());
    }


    @Test
    public void testStreamAccounts() {
        trollSrervice.streamAccounts(session)
                .forEach(this::debug);
    }

    @Test
    public void testStreamBookYears() {
        trollSrervice.streamBookYears(session)
                .forEach(this::debug);
    }

    @Test
    public void testStreamPeriods() {
        trollSrervice.streamPeriods(session)
                .forEach(this::debug);
    }

    @Test
    public void testStreamThirdParties() {
        trollSrervice.streamThirdParties(session)
                .forEach(this::debug);
    }

    @Test
    public void testStreamEntries() {
        trollSrervice.streamAccountingEntries(session)
                .forEach(this::debug);
    }

    private void debug(Object valueObject) {
        System.out.println(valueObject);
    }

}

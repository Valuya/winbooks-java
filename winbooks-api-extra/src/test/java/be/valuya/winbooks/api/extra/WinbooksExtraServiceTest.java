package be.valuya.winbooks.api.extra;

import be.valuya.jbooks.model.WbAccount;
import be.valuya.jbooks.model.WbEntry;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * @author Yannick
 */

public class WinbooksExtraServiceTest {

    private WinbooksExtraService winbooksExtraService;
    private WinbooksFileConfiguration winbooksFileConfiguration;

    @Before
    public void setup() {
        winbooksExtraService = new WinbooksExtraService();

//        Path baseFolderPath = Paths.get("C:\\temp\\wbdata\\valuya");
        Path baseFolderPath = Paths.get("C:\\temp\\wbdata\\pc");

        winbooksFileConfiguration = new WinbooksFileConfiguration();
        winbooksFileConfiguration.setBaseFolderPath(baseFolderPath);
        winbooksFileConfiguration.setBaseName("pc");
    }

    @Test
    public void testReadDBF() throws Exception {
        winbooksExtraService.dumpDbf(winbooksFileConfiguration, "act");
        winbooksExtraService.dumpDbf(winbooksFileConfiguration, "acf");
        winbooksExtraService.dumpDbf(winbooksFileConfiguration, "csf");
    }

    @Test
    public void testStreamBookYears() throws Exception {
        winbooksExtraService.streamBookYears(winbooksFileConfiguration)
                .forEach(System.out::println);
    }

    @Test
    public void testFindDistinctDocOrder() {
        winbooksExtraService.streamAct(winbooksFileConfiguration)
                .map(wbEntry -> wbEntry.getWbDocOrderType())
                .distinct()
                .forEach(docOrderType -> System.out.println(docOrderType));
    }

    @Test
    public void testFindDistinctDocStatus() {
        winbooksExtraService.streamAct(winbooksFileConfiguration)
                .map(wbEntry -> wbEntry.getDocStatus())
                .distinct()
                .forEach(docStatus -> System.out.println(docStatus));
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
                .peek(wbEntry -> System.out.println(wbEntry))
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

}

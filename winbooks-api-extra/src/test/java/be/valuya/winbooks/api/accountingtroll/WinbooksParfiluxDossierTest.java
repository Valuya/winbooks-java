package be.valuya.winbooks.api.accountingtroll;

import be.valuya.accountingtroll.AccountingEventListener;
import be.valuya.accountingtroll.domain.ATAccount;
import be.valuya.accountingtroll.domain.ATAccountBalance;
import be.valuya.accountingtroll.domain.ATAccountingEntry;
import be.valuya.accountingtroll.domain.ATBookPeriod;
import be.valuya.accountingtroll.domain.ATBookYear;
import be.valuya.accountingtroll.domain.ATPeriodType;
import be.valuya.winbooks.api.ParfiluxDossierCategory;
import be.valuya.winbooks.api.extra.WinbooksExtraService;
import be.valuya.winbooks.api.extra.config.WinbooksFileConfiguration;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@RunWith(JUnit4.class)
@Category(ParfiluxDossierCategory.class)
public class WinbooksParfiluxDossierTest {

    private static final String BOOK_YEAR_2017_NAME = "Ex. 2017";
    private static final String BOOK_YEAR_2016_NAME = "Ex. 2016";
    private static final BigDecimal ZERO = BigDecimal.valueOf(0).setScale(3, RoundingMode.UNNECESSARY);
    private static List<String> GENERAL_BALANCES_CSV_LINES;

    private WinbooksTrollAccountingManager trollSrervice;
    private AccountingEventListener eventListener;


    @BeforeClass
    public static void beforeClass() throws Exception {
        GENERAL_BALANCES_CSV_LINES = readGeneralBalanceCSV();
    }

    @Before
    public void before() {
        WinbooksExtraService extraService = new WinbooksExtraService();

        String baseFolderLocation = System.getProperty("winbooks.test.folder");
        String baseName = System.getProperty("winbooks.test.base.name");

        Path baseFolderPath = Paths.get(baseFolderLocation);
        WinbooksFileConfiguration winbooksFileConfiguration = extraService.createWinbooksFileConfigurationOptional(baseFolderPath, baseName)
                .orElseThrow(AssertionError::new);
        winbooksFileConfiguration.setReadTablesToMemory(true);

        trollSrervice = new WinbooksTrollAccountingManager(winbooksFileConfiguration);
        eventListener = new TestAccountingEventListener();
    }

    @Test
    public void testBookYears() {
        List<ATBookYear> bookYears = trollSrervice.streamBookYears()
                .collect(Collectors.toList());
        Assert.assertEquals(2, bookYears.size());

        ATBookYear firstBookYear = bookYears.get(0);
        ATBookYear secondBookyear = bookYears.get(1);

        Assert.assertEquals(BOOK_YEAR_2016_NAME, firstBookYear.getName());
        Assert.assertEquals(BOOK_YEAR_2017_NAME, secondBookyear.getName());

        LocalDate startOf2016 = LocalDate.of(2016, 1, 1);
        LocalDate startOf2017 = LocalDate.of(2017, 1, 1);
        LocalDate startOf2018 = LocalDate.of(2018, 1, 1);
        Assert.assertEquals(startOf2016, firstBookYear.getStartDate());
        Assert.assertEquals(startOf2017, firstBookYear.getEndDate());
        Assert.assertEquals(startOf2017, secondBookyear.getStartDate());
        Assert.assertEquals(startOf2018, secondBookyear.getEndDate());
    }


    @Test
    public void testBookYear2017Periods() {
        List<ATBookPeriod> periods2017 = trollSrervice.streamPeriods()
                .filter(p -> p.getBookYear().getName().equals(BOOK_YEAR_2017_NAME))
                .collect(Collectors.toList());
        // 12 months + opening + closing
        Assert.assertEquals(14, periods2017.size());

        ATBookPeriod openingPeriod = periods2017.get(0);
        ATBookPeriod closingPeriod = periods2017.get(13);
        Assert.assertEquals(ATPeriodType.OPENING, openingPeriod.getPeriodType());
        Assert.assertEquals(ATPeriodType.CLOSING, closingPeriod.getPeriodType());

        LocalDate startOf2017 = LocalDate.of(2017, 1, 1);
        LocalDate endOf2017 = LocalDate.of(2017, 12, 31);

        Assert.assertEquals(startOf2017, openingPeriod.getStartDate());
        Assert.assertEquals(endOf2017, closingPeriod.getStartDate());

        for (int monthIndex = 1; monthIndex <= 12; monthIndex++) {
            ATBookPeriod monthPeriod = periods2017.get(monthIndex);
            LocalDate periodStartDate = monthPeriod.getStartDate();
            LocalDate periodEndDate = monthPeriod.getEndDate();

            LocalDate monthStart = LocalDate.of(2017, monthIndex, 1);
            LocalDate montEnd = monthStart.plusMonths(1);
            Assert.assertEquals(monthStart, periodStartDate);
            Assert.assertEquals(montEnd, periodEndDate);
        }
    }

    @Test
    public void testAccounts() {
        Map<String, ATAccount> accountsMap = trollSrervice.streamAccounts()
                .filter(a -> !a.isTitle())
                .collect(Collectors.toMap(
                        ATAccount::getCode,
                        Function.identity()
                ));

        List<String> balanceCSVLines = new ArrayList<>(GENERAL_BALANCES_CSV_LINES).stream()
                .skip(6) // header
                .filter(s -> !s.trim().isEmpty())
                .sorted()
                .collect(Collectors.toList());
        int generalCsvLinesCount = balanceCSVLines.size();

        // Winbooks export is made per book year, which might not include all accounts from previous book years
        // so we would have to merge 2 export documents to ensure amount of account would match
        // here we only check that the accounts referenced in the 'Balance périodique générale' are correctly streamed.
        int accountIndex = 0;
        while (accountIndex < generalCsvLinesCount) {
            String csvLine = balanceCSVLines.get(accountIndex);
            String[] csvColumns = csvLine.split("\t");
            String csvAccountNumber = csvColumns[0];
            String csvAccountName = csvColumns[1].trim();

            ATAccount atAccount = accountsMap.get(csvAccountNumber);
            Assert.assertNotNull(atAccount);

            String name = atAccount.getName();
            if (!csvAccountName.equals(atAccount.getName())) {
                // Accounts may have different labels on different book years :/
                System.err.println("Account label mismatch. Expected '" + csvAccountName + "', but got '" + name + "' for account " + csvAccountNumber);
            }

            accountIndex += 1;
        }
    }


    @Test
    public void testAccountBalances() {
        Map<String, BigDecimal> balancesMap = trollSrervice.streamAccountBalances()
                .peek(this::debug)
                .collect(Collectors.toMap(
                        balance -> balance.getAccount().getCode(),
                        ATAccountBalance::getPeriodEndBalance,
                        (prev, next) -> next
                ));
        List<String> balanceCSVLines = new ArrayList<>(GENERAL_BALANCES_CSV_LINES).stream()
                .skip(6) // header
                .filter(s -> !s.trim().isEmpty())
                .sorted()
                .collect(Collectors.toList());
        int generalCsvLinesCount = balanceCSVLines.size();

        int accountIndex = 0;
        while (accountIndex < generalCsvLinesCount) {
            String csvLine = balanceCSVLines.get(accountIndex);
            String[] csvColumns = csvLine.split("\t");
            String csvAccountNumber = csvColumns[0];
            String csvAccountBalanceString = csvColumns[5].trim();
            double csvAccountBalanceDouble = Double.parseDouble(csvAccountBalanceString);
            BigDecimal csvAccountBalance = BigDecimal.valueOf(csvAccountBalanceDouble)
                    .negate() // We want negative number for debit, positive for credit
                    .setScale(3, RoundingMode.HALF_UP);

            BigDecimal accountBalance = balancesMap.getOrDefault(csvAccountNumber, ZERO);

            if (isTestableAccount(csvAccountNumber)) {
                boolean balanceMatch = accountBalance.equals(csvAccountBalance);
                if (!balanceMatch) {
                    System.err.println("Balance mismatch for " + csvAccountNumber + " : " + csvAccountBalance + " but got " + accountBalance);
                }
                Assert.assertEquals("Balance mismatch for " + csvAccountNumber, csvAccountBalance, accountBalance);
            }
            accountIndex += 1;
        }
    }

    private void debug(ATAccountBalance accountBalance) {
        if (accountBalance.getAccount().getCode().equals("200009")) {
            String periodName = accountBalance.getPeriod().getName();
            BigDecimal periodStartBalance = accountBalance.getPeriodStartBalance();
            BigDecimal periodEndBalance = accountBalance.getPeriodEndBalance();
            System.out.println(periodName + " : " + periodStartBalance + " -> " + periodEndBalance);
        }
    }

    private boolean isTestableAccount(String csvAccountNumber) {
        // Special account filled with previous year balance for accounts 6+7
        return !csvAccountNumber.equals("149999");
    }

    private static List<String> readGeneralBalanceCSV() throws IOException {
        try (InputStream csvStream = WinbooksParfiluxDossierTest.class.getClassLoader()
                .getResourceAsStream("PARFILUX Balance périodique générale.csv")) {
            InputStreamReader streamReader = new InputStreamReader(csvStream);
            BufferedReader bufferedReader = new BufferedReader(streamReader);
            List<String> allLines = bufferedReader.lines().collect(Collectors.toList());
            return allLines;
        }
    }

}
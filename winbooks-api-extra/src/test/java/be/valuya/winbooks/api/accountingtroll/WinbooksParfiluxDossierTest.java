package be.valuya.winbooks.api.accountingtroll;

import be.valuya.accountingtroll.AccountingEventListener;
import be.valuya.accountingtroll.domain.ATAccount;
import be.valuya.accountingtroll.domain.ATAccountBalance;
import be.valuya.accountingtroll.domain.ATAccountingEntry;
import be.valuya.accountingtroll.domain.ATBookPeriod;
import be.valuya.accountingtroll.domain.ATBookYear;
import be.valuya.accountingtroll.domain.ATPeriodType;
import be.valuya.accountingtroll.domain.ATThirdParty;
import be.valuya.accountingtroll.domain.ATThirdPartyBalance;
import be.valuya.accountingtroll.domain.ATThirdPartyType;
import be.valuya.jbooks.model.WbClientSupplierType;
import be.valuya.winbooks.api.ParfiluxDossierCategory;
import be.valuya.winbooks.api.accountingtroll.converter.ATThirdPartyIdFactory;
import be.valuya.winbooks.api.extra.WinbooksExtraService;
import be.valuya.winbooks.api.extra.config.WinbooksFileConfiguration;
import be.valuya.winbooks.domain.error.WinbooksConfigurationException;
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
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@RunWith(JUnit4.class)
@Category(ParfiluxDossierCategory.class)
public class WinbooksParfiluxDossierTest {

    private static final String BOOK_YEAR_2017_NAME = "Ex. 2017";
    private static final String BOOK_YEAR_2016_NAME = "Ex. 2016";
    private static final BigDecimal ZERO = BigDecimal.valueOf(0).setScale(3, RoundingMode.UNNECESSARY);
    private static List<String> GENERAL_BALANCES_CSV_LINES;
    private static List<String> CLIENTS_BALANCES_CSV_LINES;
    private static List<String> SUPPLIERS_BALANCES_CSV_LINES;

    private WinbooksTrollAccountingManager trollSrervice;
    private AccountingEventListener eventListener;


    @BeforeClass
    public static void beforeClass() throws Exception {
        GENERAL_BALANCES_CSV_LINES = readGeneralBalanceCSV();
        CLIENTS_BALANCES_CSV_LINES = readClientsBalanceCSV();
        SUPPLIERS_BALANCES_CSV_LINES = readSuppliersBalanceCSV();
    }

    @Before
    public void before() throws WinbooksConfigurationException {
        WinbooksExtraService extraService = new WinbooksExtraService();

        String baseFolderLocation = System.getProperty("winbooks.test.folder");
        String baseName = System.getProperty("winbooks.test.base.name");

        Path baseFolderPath = Paths.get(baseFolderLocation);
        WinbooksFileConfiguration winbooksFileConfiguration = extraService.createWinbooksFileConfiguration(baseFolderPath, baseName);
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
        LocalDate startOf2018 = LocalDate.of(2018, 1, 1);

        Assert.assertEquals(startOf2017, openingPeriod.getStartDate());
        Assert.assertEquals(startOf2018, closingPeriod.getStartDate());

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

    @Test
    public void testThirdPartyBalances() {
        Map<String, BigDecimal> openingBalances2017 = trollSrervice.streamThirdPartyBalances()
                .filter(b -> b.getPeriod().getBookYear().getName().equals("Ex. 2017")
                        && b.getPeriod().getPeriodType() == ATPeriodType.OPENING)
                .collect(Collectors.groupingBy(balance -> balance.getThirdParty().getId()))
                .entrySet()
                .stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> e.getValue().stream().max(Comparator.comparing(ATThirdPartyBalance::getPeriod))
                                .map(ATThirdPartyBalance::getPeriodEndBalance)
                                .orElse(BigDecimal.ZERO)
                ));
        Map<String, BigDecimal> closingBalance2017 = trollSrervice.streamThirdPartyBalances()
                .filter(b -> b.getPeriod().getBookYear().getName().equals("Ex. 2017"))
//                .peek(this::debug)
                .collect(Collectors.groupingBy(balance -> balance.getThirdParty().getId()))
                .entrySet()
                .stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> e.getValue().stream().max(Comparator.comparing(ATThirdPartyBalance::getPeriod))
                                .map(ATThirdPartyBalance::getPeriodEndBalance)
                                .orElse(BigDecimal.ZERO)
                ));


        List<String> clientsCSVLines = new ArrayList<>(CLIENTS_BALANCES_CSV_LINES).stream()
                .skip(1) // header
                .filter(s -> !s.trim().isEmpty())
                .sorted()
                .collect(Collectors.toList());
        int clientsLineCOunt = clientsCSVLines.size();

        int lineIndex = 0;
        while (lineIndex < clientsLineCOunt) {
            String csvLine = clientsCSVLines.get(lineIndex);
            lineIndex = testThirdPartyBalanceLine(openingBalances2017, closingBalance2017, lineIndex, csvLine, WbClientSupplierType.CLIENT);
        }

        List<String> suppliersCSVLines = new ArrayList<>(SUPPLIERS_BALANCES_CSV_LINES).stream()
                .skip(1) // header
                .filter(s -> !s.trim().isEmpty())
                .sorted()
                .collect(Collectors.toList());
        int suppliersLineCOunt = suppliersCSVLines.size();

        lineIndex = 0;
        while (lineIndex < suppliersLineCOunt) {
            String csvLine = suppliersCSVLines.get(lineIndex);
            lineIndex = testThirdPartyBalanceLine(openingBalances2017, closingBalance2017, lineIndex, csvLine, WbClientSupplierType.SUPPLIER);
        }

    }

    @Test
    public void testCustomersProvidersBalance() {
        BigDecimal totalForAccount400000 = trollSrervice.streamAccountingEntries()
                .filter(e -> e.getAccount().getCode().equals("400000"))
                .map(ATAccountingEntry::getAmount)
                .reduce(BigDecimal::add)
                .orElse(BigDecimal.ZERO)
                .setScale(2, RoundingMode.HALF_UP);

        BigDecimal expectedCustomerBalance = BigDecimal.valueOf(-35460.01)
                .setScale(2, RoundingMode.HALF_UP);


        Assert.assertEquals(expectedCustomerBalance, totalForAccount400000);

        BigDecimal totalForAccount440000 = trollSrervice.streamAccountingEntries()
                .filter(e -> e.getAccount().getCode().equals("440000"))
                .map(ATAccountingEntry::getAmount)
                .reduce(BigDecimal::add)
                .orElse(BigDecimal.ZERO)
                .setScale(2, RoundingMode.HALF_UP);

        BigDecimal expectedSupplierBalance = BigDecimal.valueOf(96344.28)
                .setScale(2, RoundingMode.HALF_UP);

        Assert.assertEquals(expectedSupplierBalance, totalForAccount440000);
    }


    @Test
    public void testGeneralBalance2017() {
        BigDecimal totalForAccounts6 = trollSrervice.streamAccountingEntries()
                .filter(e -> e.getAccount().getCode().startsWith("6"))
                .filter(e -> e.getBookPeriod().getBookYear().getName().equals(BOOK_YEAR_2017_NAME))
                .map(ATAccountingEntry::getAmount)
                .reduce(BigDecimal::add)
                .orElse(BigDecimal.ZERO)
                .setScale(2, RoundingMode.HALF_UP);


        BigDecimal totalForAccounts7 = trollSrervice.streamAccountingEntries()
                .filter(e -> e.getAccount().getCode().startsWith("7"))
                .filter(e -> e.getBookPeriod().getBookYear().getName().equals(BOOK_YEAR_2017_NAME))
                .map(ATAccountingEntry::getAmount)
                .reduce(BigDecimal::add)
                .orElse(BigDecimal.ZERO)
                .setScale(2, RoundingMode.HALF_UP);

        BigDecimal generalBalance = totalForAccounts6.add(totalForAccounts7)
                .setScale(2, RoundingMode.HALF_UP);


        BigDecimal expectedTotalForAccounts6 = BigDecimal.valueOf(-83727.16)
                .setScale(2, RoundingMode.HALF_UP);

        BigDecimal expectedTotalForAccounts7 = BigDecimal.valueOf(26380.45)
                .setScale(2, RoundingMode.HALF_UP);

        BigDecimal expectedGeneralBalance = BigDecimal.valueOf(-57346.71)
                .setScale(2, RoundingMode.HALF_UP);


        Assert.assertEquals(expectedTotalForAccounts6, totalForAccounts6);
        Assert.assertEquals(expectedTotalForAccounts7, totalForAccounts7);
        Assert.assertEquals(expectedGeneralBalance, generalBalance);

    }

    @Test
    public void testAccountingEntryThirdPrtiesTest() {
        // Check some arbitrary entries are correctly linked to the correct third party type.
        checkAccountingEntryThirdParty("Jan 2017", 2184,
                ATThirdPartyType.CLIENT, "DELPH");
        checkAccountingEntryThirdParty("Mar 2017", 7494,
                ATThirdPartyType.CLIENT, "COMPTOIR");
        checkAccountingEntryThirdParty("Fév 2017", -7720,
                ATThirdPartyType.CLIENT, "FOXTROT");

        checkAccountingEntryThirdParty("Mai 2017", -24793388,
                ATThirdPartyType.SUPPLIER, "DUROBRIK");
        checkAccountingEntryThirdParty("Jan 2017", -429048,
                ATThirdPartyType.SUPPLIER, "AUDU");
        checkAccountingEntryThirdParty("Mai 2017", -212231,
                ATThirdPartyType.SUPPLIER, "WARMITUP");

    }

    private void checkAccountingEntryThirdParty(String periodName, long unscaledAmount,
                                                ATThirdPartyType expectedThirdPartyType,
                                                String expectedThirdPartyCode) {
        List<ATAccountingEntry> accountingEntries = trollSrervice.streamAccountingEntries()
                .filter(e -> e.getBookPeriod().getName().equals(periodName))
                .filter(e -> e.getAmount().setScale(2, RoundingMode.UNNECESSARY)
                        .equals(BigDecimal.valueOf(unscaledAmount, 2)))
                .collect(Collectors.toList());
        Assert.assertEquals(1, accountingEntries.size());
        ATAccountingEntry accountingEntry = accountingEntries.get(0);
        System.out.println(accountingEntry);

        ATThirdParty thirdParty = accountingEntry.getThirdPartyOptional()
                .orElseThrow(AssertionError::new);
        System.out.println(thirdParty);

        thirdParty.getTypeOptional()
                .filter(type -> type == expectedThirdPartyType)
                .orElseThrow(AssertionError::new);
        String thirdPartyId = thirdParty.getId();
        String code = ATThirdPartyIdFactory.getThirdPartyCodeFromId(thirdPartyId);
        Assert.assertEquals(expectedThirdPartyCode, code);
    }

    private void debug(ATAccountBalance accountBalance) {
        String code = accountBalance.getAccount().getCode();
        if (code.equals("200009")) {
            String periodName = accountBalance.getPeriod().getName();
            BigDecimal periodStartBalance = accountBalance.getPeriodStartBalance();
            BigDecimal periodEndBalance = accountBalance.getPeriodEndBalance();
            System.out.println(code + " " + periodName + " : " + periodStartBalance + " -> " + periodEndBalance);
        }
    }

    private void debug(ATThirdPartyBalance thirdPartyBalance) {
        String thirdPartyId = thirdPartyBalance.getThirdParty().getId();
//        if (thirdPartyId.equals("1ALPHA")) {
        String periodName = thirdPartyBalance.getPeriod().getName();
        BigDecimal periodStartBalance = thirdPartyBalance.getPeriodStartBalance();
        BigDecimal periodEndBalance = thirdPartyBalance.getPeriodEndBalance();
        System.out.println(thirdPartyId + " " + periodName + " : " + periodStartBalance + " -> " + periodEndBalance);
//        }
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

    private static List<String> readClientsBalanceCSV() throws IOException {
        try (InputStream csvStream = WinbooksParfiluxDossierTest.class.getClassLoader()
                .getResourceAsStream("PARFILUX Balance courante clients.csv")) {
            InputStreamReader streamReader = new InputStreamReader(csvStream);
            BufferedReader bufferedReader = new BufferedReader(streamReader);
            List<String> allLines = bufferedReader.lines().collect(Collectors.toList());
            return allLines;
        }
    }

    private static List<String> readSuppliersBalanceCSV() throws IOException {
        try (InputStream csvStream = WinbooksParfiluxDossierTest.class.getClassLoader()
                .getResourceAsStream("PARFILUX Balance courante fournisseurs.csv")) {
            InputStreamReader streamReader = new InputStreamReader(csvStream);
            BufferedReader bufferedReader = new BufferedReader(streamReader);
            List<String> allLines = bufferedReader.lines().collect(Collectors.toList());
            return allLines;
        }
    }

    private int testThirdPartyBalanceLine(Map<String, BigDecimal> openingBalances2017, Map<String, BigDecimal> closingBalance2017,
                                          int lineIndex, String csvLine, WbClientSupplierType supplierType) {
        String[] csvColumns = csvLine.split("\t");
        String thirdPartyRef = csvColumns[0].trim();
        String thirdPartyName = csvColumns[1];
        String reportedAmountString = csvColumns[2];
        String negativeAmountString = csvColumns[3];
        String positiveAmountString = csvColumns[4];
        String endBalanceAmountString = csvColumns[5];

        double reportedAmountDouble = Double.parseDouble(reportedAmountString.replaceAll(",", ""));
        BigDecimal reportedAmount = BigDecimal.valueOf(reportedAmountDouble)
                .negate()
                .setScale(3, RoundingMode.HALF_UP);

        double negativeAmountDouble = Double.parseDouble(negativeAmountString.replaceAll(",", ""));
        BigDecimal negativeAmount = BigDecimal.valueOf(negativeAmountDouble)
                .negate()
                .setScale(3, RoundingMode.HALF_UP);

        double positiveAmountDouble = Double.parseDouble(positiveAmountString.replaceAll(",", ""));
        BigDecimal positiveAmount = BigDecimal.valueOf(positiveAmountDouble)
                .setScale(3, RoundingMode.HALF_UP);

        double endBalanceAmountDouble = Double.parseDouble(endBalanceAmountString.replaceAll(",", ""));
        BigDecimal endBalanceAmount = BigDecimal.valueOf(endBalanceAmountDouble)
                .negate()
                .setScale(3, RoundingMode.HALF_UP);

        Optional<BigDecimal> actualOpeningBalanceOptional =
                Optional.ofNullable(openingBalances2017.get(supplierType.getValue() + thirdPartyRef))
                        .map(b -> b.setScale(3, RoundingMode.HALF_UP));
        BigDecimal actualClosingBalance =
                Optional.ofNullable(closingBalance2017.get(supplierType.getValue() + thirdPartyRef))
                        .orElse(BigDecimal.ZERO)
                        .setScale(3, RoundingMode.HALF_UP);


        int finalIndex = lineIndex;
        actualOpeningBalanceOptional.ifPresent(openingBalance -> {
            Assert.assertEquals("Opening balance mismatch at index " + finalIndex,
                    reportedAmount, openingBalance);
        });

        Assert.assertEquals("Closing balance mismatch at index " + lineIndex,
                endBalanceAmount, actualClosingBalance);

        lineIndex += 1;
        return lineIndex;
    }
}

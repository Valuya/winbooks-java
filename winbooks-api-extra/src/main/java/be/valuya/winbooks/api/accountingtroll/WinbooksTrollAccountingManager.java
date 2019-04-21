package be.valuya.winbooks.api.accountingtroll;

import be.valuya.accountingtroll.AccountingEventListener;
import be.valuya.accountingtroll.AccountingManager;
import be.valuya.accountingtroll.domain.ATAccount;
import be.valuya.accountingtroll.domain.ATAccountingEntry;
import be.valuya.accountingtroll.domain.ATBookPeriod;
import be.valuya.accountingtroll.domain.ATBookYear;
import be.valuya.accountingtroll.domain.ATDocument;
import be.valuya.accountingtroll.domain.ATPeriodType;
import be.valuya.accountingtroll.domain.ATThirdParty;
import be.valuya.accountingtroll.domain.AccountingEntryDocumentNumberType;
import be.valuya.accountingtroll.event.BalanceChangeEvent;
import be.valuya.winbooks.api.extra.WinbooksExtraService;
import be.valuya.winbooks.api.extra.WinbooksFileConfiguration;

import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.stream.Stream;

public class WinbooksTrollAccountingManager implements AccountingManager {

    private static final BigDecimal ZERO_EURO = BigDecimal.ZERO.setScale(2, BigDecimal.ROUND_UNNECESSARY);
    private static final Comparator<AccountWithThirdParty> ACCOUNT_WITH_THIRD_PARTY_COMPARATOR = createAccountWithThirdPartyComparator();

    private WinbooksExtraService extraService;
    private WinbooksFileConfiguration fileConfiguration;

    private final AccountingManagerCache accountingManagerCache;

    public WinbooksTrollAccountingManager(WinbooksFileConfiguration fileConfiguration) {
        this.fileConfiguration = fileConfiguration;
        extraService = new WinbooksExtraService();
        accountingManagerCache = new AccountingManagerCache(fileConfiguration);
    }

    @Override
    public Optional<LocalDateTime> getLastAccountModificationTime() {
        LocalDateTime modificationDateTime = extraService.getActModificationDateTime(fileConfiguration);
        return Optional.of(modificationDateTime);
    }

    @Override
    public Stream<ATAccount> streamAccounts() {
        return accountingManagerCache.streamAccounts();
    }

    @Override
    public Stream<ATBookYear> streamBookYears() {
        // TODO: check use of book year index 0 that was added in gestemps
        return accountingManagerCache.streamBookYears();
    }

    @Override
    public Stream<ATBookPeriod> streamPeriods() {
        return accountingManagerCache.streamPeriods();
    }

    @Override
    public Stream<ATThirdParty> streamThirdParties() {
        return accountingManagerCache.streamThirdParties();
    }

    @Override
    public Stream<ATAccountingEntry> streamAccountingEntries(AccountingEventListener accountingEventListener) {
        Map<AccountWithThirdParty, AccountBalance> accountBalanceMap = new ConcurrentSkipListMap<>(ACCOUNT_WITH_THIRD_PARTY_COMPARATOR);

        return accountingManagerCache.streamAccountingEntries()
                .sorted()
                .flatMap(entry -> convertWithBalanceCheck(entry, accountBalanceMap, accountingEventListener));
    }

    @Override
    public Stream<ATDocument> streamDocuments() {
        return null;
    }

    @Override
    public InputStream streamDocumentContent(ATDocument atDocument) throws Exception {
        return null;
    }

    private Stream<ATAccountingEntry> convertWithBalanceCheck(ATAccountingEntry atAccountingEntry,
                                                              Map<AccountWithThirdParty, AccountBalance> accountBalanceMap,
                                                              AccountingEventListener accountingEventListener) {
        AccountingEntryDocumentNumberType documentNumberType = atAccountingEntry.getDocNumberTypeOptional()
                .orElse(AccountingEntryDocumentNumberType.DEFAULT);
        ATAccount atAccount = atAccountingEntry.getAccount();
        Optional<ATThirdParty> thirdPartyOptional = atAccountingEntry.getThirdPartyOptional();
        AccountWithThirdParty accountWithThirdParty = new AccountWithThirdParty(atAccount, thirdPartyOptional);

        if (documentNumberType == AccountingEntryDocumentNumberType.BALANCE) {
            resetAccountBalance(accountWithThirdParty, accountBalanceMap, atAccountingEntry);
            return Stream.empty();
        }

//        // TODO: check
        ATPeriodType periodType = atAccountingEntry.getBookPeriod().getPeriodType();
        AccountBalance accountBalance;
        if (periodType == ATPeriodType.OPENING) {
            resetAccountBalance(accountWithThirdParty, accountBalanceMap, atAccountingEntry);
            accountBalance = accountBalanceMap.get(accountWithThirdParty);
        } else {
            accountBalance = updateAccountBalanceAfterAccountingEntry(accountWithThirdParty, accountBalanceMap, atAccountingEntry);
        }

        BigDecimal newBalance = accountBalance.getBalance();
        LocalDate balanceDate = accountBalance.getDate();
        Optional<ATAccountingEntry> accountingEntryOptional = Optional.of(atAccountingEntry);

        BalanceChangeEvent balanceChangeEvent = new BalanceChangeEvent();
        balanceChangeEvent.setAccount(atAccount);
        balanceChangeEvent.setNewBalance(newBalance);
        balanceChangeEvent.setDate(balanceDate);
        balanceChangeEvent.setAccountingEntryOptional(accountingEntryOptional);
        accountingEventListener.handleBalanceChangeEvent(balanceChangeEvent);
        return Stream.of(atAccountingEntry);
    }

    private void resetAccountBalance(AccountWithThirdParty accountWithThirdParty,
                                     Map<AccountWithThirdParty, AccountBalance> accountBalanceMap,
                                     ATAccountingEntry accountingEntry) {
        LocalDate date = accountingEntry.getDate();
        BigDecimal amount = accountingEntry.getAmount();
        ATBookPeriod bookPeriod = accountingEntry.getBookPeriod();

        AccountBalance accountBalanceNullable = accountBalanceMap.get(accountWithThirdParty);
        BigDecimal newBalanceAmount = Optional.ofNullable(accountBalanceNullable)
                // if there already is a balance for this date / account combination, add it (we probably get an entry per accountGl)
                .filter(oldBalance -> oldBalance.getDate().equals(date))
                .map(AccountBalance::getBalance)
                .map(amount::add)
                .orElse(amount);

        setAccountBalance(accountWithThirdParty, date, bookPeriod, newBalanceAmount, accountBalanceMap);
    }

    private AccountBalance updateAccountBalanceAfterAccountingEntry(AccountWithThirdParty accountWithThirdParty,
                                                                    Map<AccountWithThirdParty, AccountBalance> accountBalanceMap,
                                                                    ATAccountingEntry accountingEntry) {
        BigDecimal entryAmount = accountingEntry.getAmount();
        BigDecimal newBalance = getYearlyAdjustedAccountBalanceOptional(accountWithThirdParty, accountBalanceMap, accountingEntry)
                .map(AccountBalance::getBalance)
                .map(entryAmount::add)
                .orElse(entryAmount);
        LocalDate date = accountingEntry.getDate();
        ATBookPeriod bookPeriod = accountingEntry.getBookPeriod();
        return setAccountBalance(accountWithThirdParty, date, bookPeriod, newBalance, accountBalanceMap);
    }

    private Optional<AccountBalance> getYearlyAdjustedAccountBalanceOptional(AccountWithThirdParty accountWithThirdParty,
                                                                             Map<AccountWithThirdParty, AccountBalance> accountBalanceMap,
                                                                             ATAccountingEntry accountingEntry) {
        AccountBalance accountBalanceNullable = accountBalanceMap.get(accountWithThirdParty);
        return Optional.ofNullable(accountBalanceNullable)
                .map(accountBalance -> getAdjustedBalance(accountWithThirdParty, accountBalanceMap, accountBalance, accountingEntry));
    }

    private AccountBalance getAdjustedBalance(AccountWithThirdParty accountWithThirdParty,
                                              Map<AccountWithThirdParty, AccountBalance> accountBalanceMap,
                                              AccountBalance accountBalance,
                                              ATAccountingEntry accountingEntry) {
        ATAccount account = accountWithThirdParty.getAccount();
        if (!account.isYearlyBalanceReset()) {
            return accountBalance;
        }
        if (isSamePeriod(accountBalance, accountingEntry)) {
            return accountBalance;
        }
        LocalDate date = accountingEntry.getDate();
        ATBookPeriod bookPeriod = accountingEntry.getBookPeriod();
        if (!isSamePeriod(accountBalance, accountingEntry)) {
            return setAccountBalance(accountWithThirdParty, date, bookPeriod, ZERO_EURO, accountBalanceMap);
        }
        return accountBalance;
    }


    private boolean isSamePeriod(AccountBalance accountBalance, ATAccountingEntry accountingEntry) {
        ATBookPeriod balancePeriod = accountBalance.getPeriod();
        ATBookPeriod entryPeriod = accountingEntry.getBookPeriod();
        return balancePeriod.equals(entryPeriod);
    }

    private AccountBalance setAccountBalance(AccountWithThirdParty accountWithThirdParty,
                                             LocalDate date,
                                             ATBookPeriod bookPeriod,
                                             BigDecimal newBalanceAmount,
                                             Map<AccountWithThirdParty, AccountBalance> accountBalanceMap
    ) {
        ATAccount account = accountWithThirdParty.getAccount();
        AccountBalance newBalance = new AccountBalance();
        newBalance.setAccount(account);
        newBalance.setBalance(newBalanceAmount);
        newBalance.setPeriod(bookPeriod);
        newBalance.setDate(date);

        accountBalanceMap.put(accountWithThirdParty, newBalance);
        return newBalance;
    }

    private static Comparator<AccountWithThirdParty> createAccountWithThirdPartyComparator() {
        return Comparator.nullsFirst(Comparator
                .comparing((AccountWithThirdParty a) -> a.getAccount().getCode())
                .thenComparing((AccountWithThirdParty a) -> a.getThirdPartyOptional().map(ATThirdParty::getId).orElse("")));
    }

// // TODO:check usage
//    /**
//     * Get the book year that is before index 0.
//     */
//    private BookYear getStartBookYear(AccountingCache accountingCache, Customer customer) {
//        BookYear firstBookYear = accountingCache.streamBookYears()
//                .findFirst()
//                .orElseThrow(() -> new IllegalArgumentException("No book year with index 1!"));
//
//        int firstYear = firstBookYear.getYear();
//        int startYear = firstYear - 1;
//        LocalDate startDate = firstBookYear.getStartDate().minusYears(1);
//        LocalDate endDate = firstBookYear.getEndDate().minusYears(1);
//
//        BookYear startBookYear = new BookYear();
//        startBookYear.setWbIndex(0);
//        startBookYear.setCustomer(customer);
//        startBookYear.setYear(startYear);
//        startBookYear.setStartDate(startDate);
//        startBookYear.setEndDate(endDate);
//
//        return entityManager.merge(startBookYear);
//    }

}

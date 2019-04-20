package be.valuya.winbooks.api.accountingtroll;

import be.valuya.accountingtroll.AccountingEventListener;
import be.valuya.accountingtroll.AccountingManager;
import be.valuya.accountingtroll.domain.ATAccount;
import be.valuya.accountingtroll.domain.ATAccountingEntry;
import be.valuya.accountingtroll.domain.ATBookPeriod;
import be.valuya.accountingtroll.domain.ATBookYear;
import be.valuya.accountingtroll.domain.ATDocument;
import be.valuya.accountingtroll.domain.ATPeriodType;
import be.valuya.accountingtroll.domain.ATTax;
import be.valuya.accountingtroll.domain.ATThirdParty;
import be.valuya.accountingtroll.domain.ATThirdPartyType;
import be.valuya.accountingtroll.event.BalanceChangeEvent;
import be.valuya.jbooks.model.WbAccount;
import be.valuya.jbooks.model.WbBookYearFull;
import be.valuya.jbooks.model.WbClientSupplier;
import be.valuya.jbooks.model.WbClientSupplierType;
import be.valuya.jbooks.model.WbDocOrderType;
import be.valuya.jbooks.model.WbEntry;
import be.valuya.jbooks.model.WbPeriod;
import be.valuya.winbooks.api.extra.WinbooksExtraService;
import be.valuya.winbooks.api.extra.WinbooksFileConfiguration;
import be.valuya.winbooks.domain.error.WinbooksError;
import be.valuya.winbooks.domain.error.WinbooksException;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class WinbooksTrollAccountingManager implements AccountingManager {

    private static final BigDecimal ZERO_EURO = BigDecimal.ZERO.setScale(2, BigDecimal.ROUND_UNNECESSARY);
    private static final Comparator<AccountWithThirdParty> ACCOUNT_WITH_THIRD_PARTY_COMPARATOR = createAccountWithThirdPartyComparator();

    private WinbooksExtraService extraService;
    private WinbooksFileConfiguration fileConfiguration;

    // Cache
    private Map<String, ATBookYear> bookYearsByShortName;
    private Map<String, ATAccount> accountsByCode;
    private Map<String, ATThirdParty> thirdPartiesById;
    // Balance cache
    private Map<AccountWithThirdParty, AccountBalance> accountBalanceMap;

    public WinbooksTrollAccountingManager(WinbooksFileConfiguration fileConfiguration) {
        this.fileConfiguration = fileConfiguration;
        extraService = new WinbooksExtraService();
    }

    @Override
    public Optional<LocalDateTime> getLastAccountModificationTime() {
        LocalDateTime modificationDateTime = extraService.getActModificationDateTime(fileConfiguration);
        return Optional.of(modificationDateTime);
    }

    @Override
    public Stream<ATAccount> streamAccounts() {
        return extraService.streamAcf(fileConfiguration)
                .filter(this::isValidAccount)
                .map(this::convertToTrollAcccount);
    }

    @Override
    public Stream<ATBookYear> streamBookYears() {
        // TODO: check use of book year index 0 that was added in gestemps
        return extraService.streamBookYears(fileConfiguration)
                .map(this::convertToTrollBookYear);
    }

    @Override
    public Stream<ATBookPeriod> streamPeriods() {
        return extraService.streamBookYears(fileConfiguration)
                .flatMap(this::streamPeriods);
    }

    @Override
    public Stream<ATThirdParty> streamThirdParties() {
        return extraService.streamCsf(fileConfiguration)
                .filter(this::isValidClientSupplier)
                .map(this::convertToTrollThirdParty);
    }


    @Override
    public Stream<ATAccountingEntry> streamAccountingEntries(AccountingEventListener accountingEventListener) {
        accountBalanceMap = new ConcurrentSkipListMap<>(ACCOUNT_WITH_THIRD_PARTY_COMPARATOR);

        Comparator<WbEntry> wbEntryComparator = Comparator.comparing(WbEntry::getBookYear)
                .thenComparing(WbEntry::getWbPeriod, Comparator.nullsFirst(
                        Comparator.comparing(WbPeriod::getIndex)
                ))
                .thenComparing(WbEntry::getDate)
                .thenComparing(WbEntry::getDocOrder, Comparator.nullsFirst(Comparator.naturalOrder())); // balance first

        return extraService.streamAct(fileConfiguration, accountingEventListener)
                .sorted(wbEntryComparator)
                .filter(this::isValidAccountingEntry)
                .flatMap(entry -> convertWithBalanceCheck(entry, accountingEventListener));
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


    private Stream<ATAccountingEntry> convertWithBalanceCheck(WbEntry entry, AccountingEventListener accountingEventListener) {
        WbDocOrderType wbDocOrderType = entry.getWbDocOrderType();
        String accountGl = entry.getAccountGl();
        String accountRp = entry.getAccountRp();

        ATAccount atAccount = getCachedAccountByCodeOptional(accountGl)
                .orElseThrow(() -> new WinbooksException(WinbooksError.INVALID_PARAMETER, "No cached account for code " + accountGl));
        Optional<ATThirdParty> thirdPartyOptional = getCachedThirdPartyOptional(accountRp);
        AccountWithThirdParty accountWithThirdParty = new AccountWithThirdParty(atAccount, thirdPartyOptional);
        ATAccountingEntry atAccountingEntry = convertToTrollAccountingEntry(entry);

        if (wbDocOrderType == WbDocOrderType.BALANCE) {
            resetAccountBalance(accountWithThirdParty, atAccountingEntry);
            return Stream.empty();
        }

//        // TODO: check
        ATPeriodType periodType = atAccountingEntry.getBookPeriod().getPeriodType();
        AccountBalance accountBalance;
        if (periodType == ATPeriodType.OPENING) {
            resetAccountBalance(accountWithThirdParty, atAccountingEntry);
            accountBalance = accountBalanceMap.get(accountWithThirdParty);
        } else {
            accountBalance = updateAccountBalanceAfterAccountingEntry(accountWithThirdParty, atAccountingEntry);
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

    private Stream<ATBookPeriod> streamPeriods(WbBookYearFull wbYear) {
        return wbYear.getPeriodList().stream()
                .map(wbPeriod -> this.convertToTrollPeriod(wbYear, wbPeriod));
    }

    private boolean isValidClientSupplier(WbClientSupplier wbClientSupplier) {
        String nameNullable = wbClientSupplier.getName1();
        return nameNullable != null;
    }

    private boolean isValidAccount(WbAccount wbAccount) {
        return wbAccount.getAccountNumber() != null;
    }


    private boolean isValidAccountingEntry(WbEntry wbEntry) {
        return wbEntry.getAccountGl() != null
                && wbEntry.getWbPeriod() != null;
    }

    private ATAccount convertToTrollAcccount(WbAccount wbAccount) {
        String accountNumber = wbAccount.getAccountNumber();
        String currency = wbAccount.getCurrency();
        String name = wbAccount.getName11();
        boolean analyt = wbAccount.isAnalyt();
        boolean yearlyBalanceReset = this.isYearResetAccount(accountNumber);

        Optional<String> currencyOptional = Optional.ofNullable(currency);

        ATAccount account = new ATAccount();
        account.setCode(accountNumber);
        account.setName(name);
        account.setAnalytics(analyt);
        account.setYearlyBalanceReset(yearlyBalanceReset);
        account.setCurrencyOptional(currencyOptional);
        return account;
    }


    private ATBookYear convertToTrollBookYear(WbBookYearFull wbYear) {
        String shortName = wbYear.getShortName();
        LocalDate startDate = wbYear.getStartDate();
        LocalDate endDate = wbYear.getEndDate();

        ATBookYear bookYear = new ATBookYear();
        bookYear.setName(shortName);
        bookYear.setStartDate(startDate);
        bookYear.setEndDate(endDate);
        return bookYear;
    }

    private ATBookPeriod convertToTrollPeriod(WbBookYearFull wbYear, WbPeriod wbPeriod) {
        String yearShortName = wbYear.getShortName();
        ATBookYear bookYear = getCachedBookYearOrThrow(yearShortName);
        String periodShortName = wbPeriod.getShortName();
        LocalDate periodStartDate = wbPeriod.getStartDate();
        LocalDate periodEndDate = wbPeriod.getEndDate();
        ATPeriodType accountingPeriodType = getAccountingPeriodType(wbPeriod);

        ATBookPeriod bookPeriod = new ATBookPeriod();
        bookPeriod.setBookYear(bookYear);
        bookPeriod.setName(periodShortName);
        bookPeriod.setStartDate(periodStartDate);
        bookPeriod.setEndDate(periodEndDate);
        bookPeriod.setPeriodType(accountingPeriodType);
        return bookPeriod;
    }

    private ATThirdParty convertToTrollThirdParty(WbClientSupplier wbSupplier) {
        String number = wbSupplier.getNumber();
        WbClientSupplierType wbClientSupplierType = wbSupplier.getWbClientSupplierType();
        ATThirdPartyType thirdPartyType = wbClientSupplierType == WbClientSupplierType.SUPPLIER ?
                ATThirdPartyType.SUPPLIER : ATThirdPartyType.CLIENT;
        String fullName = Stream.of(wbSupplier.getCivName1(), wbSupplier.getName1())
                .filter(Objects::nonNull)
                .collect(Collectors.joining(" "));
        String address = Stream.of(wbSupplier.getAddress1(), wbSupplier.getAddress2())
                .filter(Objects::nonNull)
                .collect(Collectors.joining(" "));
        String zipCode = wbSupplier.getZipCode();
        String city = wbSupplier.getCity();
        String countryCode = wbSupplier.getCountryCode();
        String vatNumber = wbSupplier.getVatNumber();

        String phoneNumber = wbSupplier.getTelNumber();
        String bankAccount = wbSupplier.getBankAccount();
        String lang = wbSupplier.getLang();

        ATThirdParty thirdParty = new ATThirdParty();
        thirdParty.setId(number);
        thirdParty.setType(thirdPartyType);
        thirdParty.setFullName(fullName);
        thirdParty.setAddress(address);
        thirdParty.setZipCode(zipCode);
        thirdParty.setCity(city);
        thirdParty.setCountryCode(countryCode);
        thirdParty.setVatNumber(vatNumber);
        thirdParty.setPhoneNumber(phoneNumber);
        thirdParty.setBankAccountNumber(bankAccount);
        thirdParty.setLanguage(lang);
        return thirdParty;
    }

    private ATAccountingEntry convertToTrollAccountingEntry(WbEntry wbEntry) {
        WbBookYearFull wbBookYearFull = wbEntry.getWbBookYearFull();
        WbPeriod wbPeriod = wbEntry.getWbPeriod();
        ATBookPeriod bookPeriod = convertToTrollPeriod(wbBookYearFull, wbPeriod);

        BigDecimal amount = wbEntry.getAmountEur();
        String dbkCode = wbEntry.getDbkCode();

        String accountFromNumber = wbEntry.getAccountGl();
        Optional<ATAccount> accountOptional = getCachedAccountByCodeOptional(accountFromNumber);

        String accountToId = wbEntry.getAccountRp();
        Optional<ATThirdParty> thirdPartyOptional = getCachedThirdPartyOptional(accountToId);

        Optional<ATTax> taxOptional = Optional.empty(); // TODO
        Optional<ATDocument> documentOptional = Optional.empty(); //TODO
        Optional<ATDocument> matchedDocumentOptional = Optional.empty(); //TODO

        Optional<String> matchNo = Optional.ofNullable(wbEntry.getMatchNo()).map(String::trim);
        Date entryDate = wbEntry.getDate();
        Date documentDate = wbEntry.getDateDoc();
        Date dueDate = wbEntry.getDueDate();
        LocalDate entryLocalDate = this.convertToLocalDate(entryDate)
                .orElseThrow(() -> new WinbooksException(WinbooksError.FATAL_ERRORS, "Could not parse local date " + entryDate));
        Optional<LocalDate> documentLocalDateOptional = this.convertToLocalDate(documentDate);
        Optional<LocalDate> dueDateOptional = this.convertToLocalDate(dueDate);
        String comment = wbEntry.getComment();

        ATAccountingEntry accountingEntry = new ATAccountingEntry();
        accountingEntry.setBookPeriod(bookPeriod);
        accountingEntry.setDate(entryLocalDate);
        accountingEntry.setAmount(amount);
        accountingEntry.setDbkCode(dbkCode);
        accountingEntry.setMatched(matchNo.isPresent());

        accountingEntry.setTaxOptional(taxOptional);
        accountingEntry.setAccountOptional(accountOptional);
        accountingEntry.setThirdPartyOptional(thirdPartyOptional);
        accountingEntry.setDocumentDateOptional(documentLocalDateOptional);
        accountingEntry.setDueDateOptional(dueDateOptional);
        accountingEntry.setCommentOptional(Optional.ofNullable(comment));
        accountingEntry.setDocumentOptional(documentOptional);
        accountingEntry.setMatchedDocumentOptional(matchedDocumentOptional);

        return accountingEntry;
    }

    private Optional<LocalDate> convertToLocalDate(Date date) {
        if (date == null) {
            return Optional.empty();
        }
        Instant instant = date.toInstant();
        LocalDate localDate = instant.atZone(ZoneId.systemDefault()).toLocalDate();
        return Optional.of(localDate);
    }

    private ATBookYear getCachedBookYearOrThrow(String bookYearShortName) {
        if (bookYearsByShortName == null) {
            bookYearsByShortName = streamBookYears().collect(
                    Collectors.toMap(ATBookYear::getName, Function.identity(),
                            (t1, t2) -> t1)// Override in case of dupplicates
            );
        }
        ATBookYear bookYearNullable = bookYearsByShortName.get(bookYearShortName);
        return Optional.ofNullable(bookYearNullable)
                .orElseThrow(() -> new WinbooksException(WinbooksError.FATAL_ERRORS, "No book year matching short name " + bookYearShortName));
    }


    private Optional<ATAccount> getCachedAccountByCodeOptional(String accountCode) {
        if (accountsByCode == null) {
            accountsByCode = streamAccounts().collect(
                    Collectors.toMap(ATAccount::getCode, Function.identity(),
                            (t1, t2) -> t1)// Override in case of dupplicates
            );
        }
        ATAccount accountNullable = accountsByCode.get(accountCode);
        return Optional.ofNullable(accountNullable);
    }

    private Optional<ATThirdParty> getCachedThirdPartyOptional(String id) {
        if (thirdPartiesById == null) {
            thirdPartiesById = streamThirdParties()
                    .collect(
                            Collectors.toMap(ATThirdParty::getId, Function.identity(),
                                    (t1, t2) -> t1)// Override in case of dupplicates
                    );
        }
        ATThirdParty thirdPartyNullable = thirdPartiesById.get(id);
        return Optional.ofNullable(thirdPartyNullable);
    }


    private void resetAccountBalance(AccountWithThirdParty accountWithThirdParty, ATAccountingEntry accountingEntry) {
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

        setAccountBalance(accountWithThirdParty, date, bookPeriod, newBalanceAmount);
    }

    private AccountBalance updateAccountBalanceAfterAccountingEntry(AccountWithThirdParty accountWithThirdParty, ATAccountingEntry accountingEntry) {
        BigDecimal entryAmount = accountingEntry.getAmount();
        BigDecimal newBalance = getYearlyAdjustedAccountBalanceOptional(accountWithThirdParty, accountingEntry)
                .map(AccountBalance::getBalance)
                .map(entryAmount::add)
                .orElse(entryAmount);
        LocalDate date = accountingEntry.getDate();
        ATBookPeriod bookPeriod = accountingEntry.getBookPeriod();
        return setAccountBalance(accountWithThirdParty, date, bookPeriod, newBalance);
    }

    private Optional<AccountBalance> getYearlyAdjustedAccountBalanceOptional(AccountWithThirdParty accountWithThirdParty, ATAccountingEntry accountingEntry) {
        AccountBalance accountBalanceNullable = accountBalanceMap.get(accountWithThirdParty);
        return Optional.ofNullable(accountBalanceNullable)
                .map(accountBalance -> getAdjustedBalance(accountWithThirdParty, accountBalance, accountingEntry));
    }

    private AccountBalance getAdjustedBalance(AccountWithThirdParty accountWithThirdParty, AccountBalance accountBalance, ATAccountingEntry accountingEntry) {
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
            return setAccountBalance(accountWithThirdParty, date, bookPeriod, ZERO_EURO);
        }
        return accountBalance;
    }


    private boolean isSamePeriod(AccountBalance accountBalance, ATAccountingEntry accountingEntry) {
        ATBookPeriod balancePeriod = accountBalance.getPeriod();
        ATBookPeriod entryPeriod = accountingEntry.getBookPeriod();
        return balancePeriod.equals(entryPeriod);
    }

    private AccountBalance setAccountBalance(AccountWithThirdParty accountWithThirdParty, LocalDate date, ATBookPeriod bookPeriod, BigDecimal newBalanceAmount) {
        ATAccount account = accountWithThirdParty.getAccount();
        AccountBalance newBalance = new AccountBalance();
        newBalance.setAccount(account);
        newBalance.setBalance(newBalanceAmount);
        newBalance.setPeriod(bookPeriod);
        newBalance.setDate(date);

        accountBalanceMap.put(accountWithThirdParty, newBalance);
        return newBalance;
    }

    private boolean isYearResetAccount(String accountCode) {
        return accountCode.startsWith("6") || accountCode.startsWith("7");
    }

    private static Comparator<AccountWithThirdParty> createAccountWithThirdPartyComparator() {
        return Comparator.nullsFirst(Comparator
                .comparing((AccountWithThirdParty a) -> a.getAccount().getCode())
                .thenComparing((AccountWithThirdParty a) -> a.getThirdPartyOptional().map(ATThirdParty::getId).orElse("")));
    }

    public ATPeriodType getAccountingPeriodType(WbPeriod wbPeriod) {
        int numericIndex = wbPeriod.getIndex();
        String index = MessageFormat.format("{0,number,00}", numericIndex);

        switch (index) {
            case "00": {
                return ATPeriodType.OPENING;
            }
            case "99": {
                return ATPeriodType.CLOSING;
            }
            default: {
                return ATPeriodType.GENERAL;
            }
        }
    }

}

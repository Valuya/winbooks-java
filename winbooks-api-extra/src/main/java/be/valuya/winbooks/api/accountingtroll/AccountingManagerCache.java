package be.valuya.winbooks.api.accountingtroll;

import be.valuya.accountingtroll.AccountingEventListener;
import be.valuya.accountingtroll.domain.ATAccount;
import be.valuya.accountingtroll.domain.ATAccountingEntry;
import be.valuya.accountingtroll.domain.ATBookPeriod;
import be.valuya.accountingtroll.domain.ATBookYear;
import be.valuya.accountingtroll.domain.ATThirdParty;
import be.valuya.accountingtroll.event.AccountingEventHandler;
import be.valuya.jbooks.model.WbAccount;
import be.valuya.jbooks.model.WbBookYearFull;
import be.valuya.jbooks.model.WbClientSupplier;
import be.valuya.jbooks.model.WbEntry;
import be.valuya.jbooks.model.WbPeriod;
import be.valuya.winbooks.api.accountingtroll.converter.ATAccountConverter;
import be.valuya.winbooks.api.accountingtroll.converter.ATAccountingEntryConverter;
import be.valuya.winbooks.api.accountingtroll.converter.ATBookPeriodConverter;
import be.valuya.winbooks.api.accountingtroll.converter.ATBookYearConverter;
import be.valuya.winbooks.api.accountingtroll.converter.ATThirdPartyConverter;
import be.valuya.winbooks.api.extra.WinbooksExtraService;
import be.valuya.winbooks.api.extra.WinbooksFileConfiguration;
import be.valuya.winbooks.domain.error.WinbooksError;
import be.valuya.winbooks.domain.error.WinbooksException;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AccountingManagerCache {

    private Map<String, ATBookYear> bookYearsByShortName;
    private Map<String, List<ATBookPeriod>> bookPeriodsByBookYearShortName;
    private Map<String, ATAccount> accountsByCode;
    private Map<String, ATThirdParty> thirdPartiesById;
    private List<ATAccountingEntry> accountingEntries;

    private final WinbooksExtraService extraService;
    private final WinbooksFileConfiguration fileConfiguration;

    private final ATAccountConverter atAccountConverter;
    private final ATAccountingEntryConverter atAccountingEntryConverter;
    private final ATBookYearConverter atBookYearConverter;
    private final ATBookPeriodConverter atBookPeriodConverter;
    private final ATThirdPartyConverter atThirdPartyConverter;

    public AccountingManagerCache(WinbooksFileConfiguration fileConfiguration) {
        this.fileConfiguration = fileConfiguration;
        this.extraService = new WinbooksExtraService();

        atAccountConverter = new ATAccountConverter();
        atAccountingEntryConverter = new ATAccountingEntryConverter(this);
        atBookYearConverter = new ATBookYearConverter();
        atBookPeriodConverter = new ATBookPeriodConverter(this);
        atThirdPartyConverter = new ATThirdPartyConverter();
    }

    public Stream<ATAccount> streamAccounts() {
        this.cacheAccounts();
        return accountsByCode.values().stream();
    }

    public Stream<ATBookYear> streamBookYears() {
        this.cacheBookYears();
        return bookYearsByShortName.values().stream();
    }

    public Stream<ATBookPeriod> streamPeriods() {
        this.cacheBookPeriods();
        return bookPeriodsByBookYearShortName.values()
                .stream()
                .flatMap(List::stream);
    }

    public Stream<ATThirdParty> streamThirdParties() {
        this.cacheThirdParties();
        return thirdPartiesById.values().stream();
    }

    public Stream<ATAccountingEntry> streamAccountingEntries() {
        this.cacheAccountingEntries();
        return accountingEntries.stream();
    }

    public ATBookYear getCachedBookYearOrThrow(String bookYearShortName) {
        cacheBookYears();
        ATBookYear bookYearNullable = bookYearsByShortName.get(bookYearShortName);
        return Optional.ofNullable(bookYearNullable)
                .orElseThrow(() -> new WinbooksException(WinbooksError.FATAL_ERRORS, "No book year matching short name " + bookYearShortName));
    }

    public ATBookPeriod getCachedBookPeriodOrThrow(WbBookYearFull bookYearFull, WbPeriod period) {
        cacheBookPeriods();
        String shortName = bookYearFull.getShortName();
        List<ATBookPeriod> periodListNullable = bookPeriodsByBookYearShortName.get(shortName);
        return Optional.ofNullable(periodListNullable)
                .map(List::stream)
                .orElse(Stream.empty())
                .filter(atPeriod -> this.isSamePeriod(atPeriod, period))
                .findAny()
                .orElseThrow(() -> new WinbooksException(WinbooksError.FATAL_ERRORS, "Could not find period for year " + bookYearFull + " and period " + period));
    }

    public Optional<ATAccount> getCachedAccountByCodeOptional(String accountCode) {
        cacheAccounts();
        ATAccount accountNullable = accountsByCode.get(accountCode);
        return Optional.ofNullable(accountNullable);
    }


    public Optional<ATThirdParty> getCachedThirdPartyOptional(String accountId) {
        cacheThirdParties();
        ATThirdParty thirdPartyNullable = thirdPartiesById.get(accountId);
        return Optional.ofNullable(thirdPartyNullable);
    }

    private void cacheAccounts() {
        if (accountsByCode != null) {
            return;
        }
        accountsByCode = extraService.streamAcf(fileConfiguration)
                .filter(this::isValidAccount)
                .map(atAccountConverter::convertToTrollAcccount)
                .collect(Collectors.toMap(
                        ATAccount::getCode,
                        Function.identity(),
                        (t1, t2) -> t1)// Override in case of dupplicates
                );
    }


    private void cacheBookYears() {
        if (bookYearsByShortName != null) {
            return;
        }
        bookYearsByShortName = extraService.streamBookYears(fileConfiguration)
                .map(atBookYearConverter::convertToTrollBookYear)
                .collect(Collectors.toMap(
                        ATBookYear::getName,
                        Function.identity() // throw in case of duplicates
                ));
    }


    private void cacheBookPeriods() {
        if (bookPeriodsByBookYearShortName != null) {
            return;
        }
        bookPeriodsByBookYearShortName = extraService.streamBookYears(fileConfiguration)
                .flatMap(this::streamYearPeriods)
                .collect(Collectors.groupingBy(
                        p -> p.getBookYear().getName()
                ));
    }

    private void cacheThirdParties() {
        if (thirdPartiesById != null) {
            return;
        }
        thirdPartiesById = extraService.streamCsf(fileConfiguration)
                .filter(this::isValidClientSupplier)
                .map(atThirdPartyConverter::convertToTrollThirdParty)
                .collect(Collectors.toMap(
                        ATThirdParty::getId,
                        Function.identity(),
                        (a, b) -> b // OVerwrite in case of duplicates
                ));
    }


    private void cacheAccountingEntries() {
        if (accountingEntries != null) {
            return;
        }
        AccountingEventListener accountingEventListener = new AccountingEventHandler();
        accountingEntries = extraService.streamAct(fileConfiguration, accountingEventListener)
                .filter(this::isValidAccountingEntry)
                .map(atAccountingEntryConverter::convertToTrollAccountingEntry)
                .collect(Collectors.toList());
    }


    private Stream<ATBookPeriod> streamYearPeriods(WbBookYearFull bookYearFull) {
        return bookYearFull.getPeriodList().stream()
                .map(period -> atBookPeriodConverter.convertToTrollPeriod(bookYearFull, period));
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

    private boolean isSamePeriod(ATBookPeriod atPeriod, WbPeriod period) {
        String periodName = atPeriod.getName();
        return periodName.equals(period.getShortName());
    }


}

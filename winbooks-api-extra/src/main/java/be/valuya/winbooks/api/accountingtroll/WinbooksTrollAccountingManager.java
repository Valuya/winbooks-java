package be.valuya.winbooks.api.accountingtroll;

import be.valuya.accountingtroll.AccountingEventListener;
import be.valuya.accountingtroll.AccountingManager;
import be.valuya.accountingtroll.domain.Account;
import be.valuya.accountingtroll.domain.AccountingEntry;
import be.valuya.accountingtroll.domain.BookPeriod;
import be.valuya.accountingtroll.domain.BookYear;
import be.valuya.accountingtroll.domain.ThirdParty;
import be.valuya.accountingtroll.domain.ThirdPartyType;
import be.valuya.jbooks.model.WbAccount;
import be.valuya.jbooks.model.WbBookYearFull;
import be.valuya.jbooks.model.WbClientSupplier;
import be.valuya.jbooks.model.WbClientSupplierType;
import be.valuya.jbooks.model.WbEntry;
import be.valuya.jbooks.model.WbPeriod;
import be.valuya.winbooks.api.extra.WinbooksExtraService;
import be.valuya.winbooks.api.extra.WinbooksFileConfiguration;
import be.valuya.winbooks.domain.error.WinbooksError;
import be.valuya.winbooks.domain.error.WinbooksException;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class WinbooksTrollAccountingManager implements AccountingManager {

    private WinbooksExtraService extraService;
    private WinbooksFileConfiguration fileConfiguration;

    private Map<String, BookYear> bookYearsByShortName;
    private Map<String, Account> accountsByCode;
    private Map<String, ThirdParty> thirdPartiesById;


    public WinbooksTrollAccountingManager(WinbooksFileConfiguration fileConfiguration) {
        this.fileConfiguration = fileConfiguration;
        extraService = new WinbooksExtraService();
    }

    @Override
    public Stream<Account> streamAccounts() {
        return extraService.streamAcf(fileConfiguration)
                .map(this::convertToTrollAcccount);
    }


    @Override
    public Stream<BookYear> streamBookYears() {
        return extraService.streamBookYears(fileConfiguration)
                .map(this::convertToTrollBookYear);
    }

    @Override
    public Stream<BookPeriod> streamPeriods() {
        return extraService.streamBookYears(fileConfiguration)
                .flatMap(wbYear -> this.streamPeriods(wbYear));
    }

    @Override
    public Stream<ThirdParty> streamThirdParties() {
        return extraService.streamCsf(fileConfiguration)
                .filter(this::isValidClientSupplier)
                .map(this::convertToTrollThirdParty);
    }


    @Override
    public Stream<AccountingEntry> streamAccountingEntries(AccountingEventListener accountingEventListener) {
        return extraService.streamAct(fileConfiguration, accountingEventListener)
                .map(this::convertToTrollAccountingEntry);
    }

    private Stream<BookPeriod> streamPeriods(WbBookYearFull wbYear) {
        return wbYear.getPeriodList().stream()
                .map(wbPeriod -> this.convertToTrollPeriod(wbYear, wbPeriod));
    }

    private boolean isValidClientSupplier(WbClientSupplier wbClientSupplier) {
        String nameNullable = wbClientSupplier.getName1();
        return Optional.ofNullable(nameNullable).isPresent();
    }

    private Account convertToTrollAcccount(WbAccount wbAccount) {
        String accountNumber = wbAccount.getAccountNumber();
        String currency = wbAccount.getCurrency();
        String name = wbAccount.getName11();

        Account account = new Account();
        account.setCode(accountNumber);
        account.setCurrency(currency);
        account.setName(name);
        return account;
    }


    private BookYear convertToTrollBookYear(WbBookYearFull wbYear) {
        String shortName = wbYear.getShortName();
        LocalDate startDate = wbYear.getStartDate();
        LocalDate endDate = wbYear.getEndDate();

        BookYear bookYear = new BookYear();
        bookYear.setName(shortName);
        bookYear.setStartDate(startDate);
        bookYear.setEndDate(endDate);
        return bookYear;
    }

    private BookPeriod convertToTrollPeriod(WbBookYearFull wbYear, WbPeriod wbPeriod) {
        String yearShortName = wbYear.getShortName();
        BookYear bookYear = getCachedBookYearOrThrow(yearShortName);
        String periodShortName = wbPeriod.getShortName();
        LocalDate periodStartDate = wbPeriod.getStartDate();
        LocalDate periodEndDate = wbPeriod.getEndDate();

        BookPeriod bookPeriod = new BookPeriod();
        bookPeriod.setBookYear(bookYear);
        bookPeriod.setName(periodShortName);
        bookPeriod.setStartDate(periodStartDate);
        bookPeriod.setEndDate(periodEndDate);
        return bookPeriod;
    }

    private ThirdParty convertToTrollThirdParty(WbClientSupplier wbSupplier) {
        String number = wbSupplier.getNumber();
        WbClientSupplierType wbClientSupplierType = wbSupplier.getWbClientSupplierType();
        ThirdPartyType thirdPartyType = wbClientSupplierType == WbClientSupplierType.SUPPLIER ?
                ThirdPartyType.SUPPLIER : ThirdPartyType.CLIENT;
        String fullName = Stream.of(wbSupplier.getCivName1(), wbSupplier.getName1())
                .filter(Objects::nonNull)
                .collect(Collectors.joining(" "));
        String address = Stream.of(wbSupplier.getAddress1(), wbSupplier.getAddress2())
                .filter(Objects::nonNull)
                .collect(Collectors.joining(" "));
        String zipCode = wbSupplier.getZipCode();
        String city = wbSupplier.getCity();
        String countryCode = wbSupplier.getCountryCode();
        String vatCode = wbSupplier.getVatCode();
        String vatNumber = wbSupplier.getVatNumber();

        String phoneNumber = wbSupplier.getTelNumber();
        String bankAccount = wbSupplier.getBankAccount();
        String lang = wbSupplier.getLang();

        ThirdParty thirdParty = new ThirdParty();
        thirdParty.setId(number);
        thirdParty.setType(thirdPartyType);
        thirdParty.setFullName(fullName);
        thirdParty.setAddress(address);
        thirdParty.setZipCode(zipCode);
        thirdParty.setCity(city);
        thirdParty.setCountryCode(countryCode);
        thirdParty.setVatCode(vatCode);
        thirdParty.setVatNumber(vatNumber);
        thirdParty.setPhoneNumber(phoneNumber);
        thirdParty.setBankAccountNumber(bankAccount);
        thirdParty.setLanguage(lang);
        return thirdParty;
    }

    private AccountingEntry convertToTrollAccountingEntry(WbEntry wbEntry) {
        String bookYearName = wbEntry.getWbBookYearFull().getShortName();
        BookYear bookYear = getCachedBookYearOrThrow(bookYearName);

        WbBookYearFull wbBookYearFull = wbEntry.getWbBookYearFull();
        WbPeriod wbPeriod = wbEntry.getWbPeriod();
        BookPeriod bookPeriod = convertToTrollPeriod(wbBookYearFull, wbPeriod);

        BigDecimal amount = wbEntry.getAmountEur();
        BigDecimal vatBase = wbEntry.getVatTax();
        BigDecimal currAmount = wbEntry.getCurrAmount(); // FIXME balance has to be computed?

        String accountFromNumber = wbEntry.getAccountGl();
        Optional<Account> accountOptional = getCachedAccountByCodeOptional(accountFromNumber);

        String accountToId = wbEntry.getAccountRp();
        Optional<ThirdParty> thirdPartyOptional = getCachedThirdPartyOptional(accountToId);

        Date entryDate = wbEntry.getDate();
        Date documentDate = wbEntry.getDateDoc();
        Date dueDate = wbEntry.getDueDate();
        LocalDate entryLocalDate = this.convertToLocalDate(entryDate)
                .orElseThrow(() -> new WinbooksException(WinbooksError.FATAL_ERRORS, "Could not parse local date " + entryDate));
        Optional<LocalDate> documentLocalDateOptional = this.convertToLocalDate(documentDate);
        Optional<LocalDate> dueDateOptional = this.convertToLocalDate(dueDate);
        String comment = wbEntry.getComment();

        AccountingEntry accountingEntry = new AccountingEntry();
        accountingEntry.setBookPeriod(bookPeriod);
        accountingEntry.setDate(entryLocalDate);
        accountingEntry.setAmount(amount);
        accountingEntry.setVatRate(vatBase);
        accountingEntry.setBalance(currAmount);

        accountingEntry.setAccountOptional(accountOptional);
        accountingEntry.setThirdPartyOptional(thirdPartyOptional);
        accountingEntry.setDocumentDateOptional(documentLocalDateOptional);
        accountingEntry.setDueDateOptional(dueDateOptional);
        accountingEntry.setCommentOptional(Optional.ofNullable(comment));

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

    private BookYear getCachedBookYearOrThrow(String bookYearShortName) {
        if (bookYearsByShortName == null) {
            bookYearsByShortName = streamBookYears().collect(
                    Collectors.toMap(BookYear::getName, Function.identity())
            );
        }
        BookYear bookYearNullable = bookYearsByShortName.get(bookYearShortName);
        return Optional.ofNullable(bookYearNullable)
                .orElseThrow(() -> new WinbooksException(WinbooksError.FATAL_ERRORS, "No book year matching short name " + bookYearShortName));
    }


    private Optional<Account> getCachedAccountByCodeOptional(String accountCode) {
        if (accountsByCode == null) {
            accountsByCode = streamAccounts().collect(
                    Collectors.toMap(Account::getCode, Function.identity())
            );
        }
        Account accountNullable = accountsByCode.get(accountCode);
        return Optional.ofNullable(accountNullable);
    }

    private Optional<ThirdParty> getCachedThirdPartyOptional(String id) {
        if (thirdPartiesById == null) {
            thirdPartiesById = streamThirdParties().collect(
                    Collectors.toMap(ThirdParty::getId, Function.identity())
            );
        }
        ThirdParty thirdPartyNullable = thirdPartiesById.get(id);
        return Optional.ofNullable(thirdPartyNullable);
    }
}

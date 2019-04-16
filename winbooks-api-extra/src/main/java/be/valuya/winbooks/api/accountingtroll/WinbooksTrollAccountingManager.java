package be.valuya.winbooks.api.accountingtroll;

import be.valuya.accountingtroll.AccountingEventListener;
import be.valuya.accountingtroll.AccountingManager;
import be.valuya.accountingtroll.domain.ATAccount;
import be.valuya.accountingtroll.domain.ATAccountingEntry;
import be.valuya.accountingtroll.domain.ATBookPeriod;
import be.valuya.accountingtroll.domain.ATBookYear;
import be.valuya.accountingtroll.domain.ATThirdParty;
import be.valuya.accountingtroll.domain.ATThirdPartyType;
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
import java.time.LocalDateTime;
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

    private Map<String, ATBookYear> bookYearsByShortName;
    private Map<String, ATAccount> accountsByCode;
    private Map<String, ATThirdParty> thirdPartiesById;


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
                .map(this::convertToTrollAcccount);
    }


    @Override
    public Stream<ATBookYear> streamBookYears() {
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
        return extraService.streamAct(fileConfiguration, accountingEventListener)
                .map(this::convertToTrollAccountingEntry);
    }

    private Stream<ATBookPeriod> streamPeriods(WbBookYearFull wbYear) {
        return wbYear.getPeriodList().stream()
                .map(wbPeriod -> this.convertToTrollPeriod(wbYear, wbPeriod));
    }

    private boolean isValidClientSupplier(WbClientSupplier wbClientSupplier) {
        String nameNullable = wbClientSupplier.getName1();
        return Optional.ofNullable(nameNullable).isPresent();
    }

    private ATAccount convertToTrollAcccount(WbAccount wbAccount) {
        String accountNumber = wbAccount.getAccountNumber();
        String currency = wbAccount.getCurrency();
        String name = wbAccount.getName11();

        ATAccount account = new ATAccount();
        account.setCode(accountNumber);
        account.setCurrency(currency);
        account.setName(name);
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

        ATBookPeriod bookPeriod = new ATBookPeriod();
        bookPeriod.setBookYear(bookYear);
        bookPeriod.setName(periodShortName);
        bookPeriod.setStartDate(periodStartDate);
        bookPeriod.setEndDate(periodEndDate);
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
        String vatCode = wbSupplier.getVatCode();
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
        thirdParty.setVatCode(vatCode);
        thirdParty.setVatNumber(vatNumber);
        thirdParty.setPhoneNumber(phoneNumber);
        thirdParty.setBankAccountNumber(bankAccount);
        thirdParty.setLanguage(lang);
        return thirdParty;
    }

    private ATAccountingEntry convertToTrollAccountingEntry(WbEntry wbEntry) {
        String bookYearName = wbEntry.getWbBookYearFull().getShortName();
        ATBookYear bookYear = getCachedBookYearOrThrow(bookYearName);

        WbBookYearFull wbBookYearFull = wbEntry.getWbBookYearFull();
        WbPeriod wbPeriod = wbEntry.getWbPeriod();
        ATBookPeriod bookPeriod = convertToTrollPeriod(wbBookYearFull, wbPeriod);

        BigDecimal amount = wbEntry.getAmountEur();
        BigDecimal vatBase = wbEntry.getVatTax();
        BigDecimal currAmount = wbEntry.getCurrAmount(); // FIXME balance has to be computed?

        String accountFromNumber = wbEntry.getAccountGl();
        Optional<ATAccount> accountOptional = getCachedAccountByCodeOptional(accountFromNumber);

        String accountToId = wbEntry.getAccountRp();
        Optional<ATThirdParty> thirdPartyOptional = getCachedThirdPartyOptional(accountToId);

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

    private ATBookYear getCachedBookYearOrThrow(String bookYearShortName) {
        if (bookYearsByShortName == null) {
            bookYearsByShortName = streamBookYears().collect(
                    Collectors.toMap(ATBookYear::getName, Function.identity())
            );
        }
        ATBookYear bookYearNullable = bookYearsByShortName.get(bookYearShortName);
        return Optional.ofNullable(bookYearNullable)
                .orElseThrow(() -> new WinbooksException(WinbooksError.FATAL_ERRORS, "No book year matching short name " + bookYearShortName));
    }


    private Optional<ATAccount> getCachedAccountByCodeOptional(String accountCode) {
        if (accountsByCode == null) {
            accountsByCode = streamAccounts().collect(
                    Collectors.toMap(ATAccount::getCode, Function.identity())
            );
        }
        ATAccount accountNullable = accountsByCode.get(accountCode);
        return Optional.ofNullable(accountNullable);
    }

    private Optional<ATThirdParty> getCachedThirdPartyOptional(String id) {
        if (thirdPartiesById == null) {
            thirdPartiesById = streamThirdParties().collect(
                    Collectors.toMap(ATThirdParty::getId, Function.identity())
            );
        }
        ATThirdParty thirdPartyNullable = thirdPartiesById.get(id);
        return Optional.ofNullable(thirdPartyNullable);
    }
}

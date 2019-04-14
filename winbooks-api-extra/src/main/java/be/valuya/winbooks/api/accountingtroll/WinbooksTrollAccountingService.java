package be.valuya.winbooks.api.accountingtroll;

import be.valuya.accountingtroll.AccountingService;
import be.valuya.accountingtroll.Session;
import be.valuya.accountingtroll.cache.AccountingCache;
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
import be.valuya.winbooks.api.extra.WinbooksSession;
import be.valuya.winbooks.domain.error.WinbooksError;
import be.valuya.winbooks.domain.error.WinbooksException;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class WinbooksTrollAccountingService implements AccountingService {

    private WinbooksExtraService extraService;

    public WinbooksTrollAccountingService() {
        extraService = new WinbooksExtraService();
    }

    @Override
    public Stream<Account> streamAccounts(Session session) {
        WinbooksSession winbooksSession = checkSession(session);
        return extraService.streamAcf(winbooksSession)
                .map(wbAccount -> this.convertToTrollAcccount(wbAccount, session));
    }


    @Override
    public Stream<BookYear> streamBookYears(Session session) {
        WinbooksSession winbooksSession = checkSession(session);
        return extraService.streamBookYears(winbooksSession)
                .map(wbYear -> this.convertToTrollBookYear(wbYear, session));
    }


    @Override
    public Stream<BookPeriod> streamPeriods(Session session) {
        WinbooksSession winbooksSession = checkSession(session);
        return extraService.streamBookYears(winbooksSession)
                .flatMap(wbYear -> this.streamPeriods(wbYear, session));
    }


    @Override
    public Stream<ThirdParty> streamThirdParties(Session session) {
        WinbooksSession winbooksSession = checkSession(session);
        return extraService.streamCsf(winbooksSession)
                .filter(this::isValidClientSupplier)
                .map(supplier -> this.convertToTrollThirdParty(supplier, session));
    }


    @Override
    public Stream<AccountingEntry> streamAccountingEntries(Session session) {
        WinbooksSession winbooksSession = checkSession(session);
        return extraService.streamAct(winbooksSession)
                .map(wbEntry -> this.convertToTrollAccountingEntry(wbEntry, session));
    }

    private WinbooksSession checkSession(Session trollSession) {
        if (trollSession.getSessionType().equals(WinbooksSession.SESSION_TYPE)) {
            return (WinbooksSession) trollSession;
        } else {
            throw new WinbooksException(WinbooksError.INVALID_PARAMETER, "Session type mismatch");
        }
    }


    private Stream<BookPeriod> streamPeriods(WbBookYearFull wbYear, Session session) {
        return wbYear.getPeriodList().stream()
                .map(wbPeriod -> this.convertToTrollPeriod(wbYear, wbPeriod, session));
    }

    private boolean isValidClientSupplier(WbClientSupplier wbClientSupplier) {
        String nameNullable = wbClientSupplier.getName1();
        return Optional.ofNullable(nameNullable).isPresent();
    }

    private Account convertToTrollAcccount(WbAccount wbAccount, Session session) {
        String accountNumber = wbAccount.getAccountNumber();
        String currency = wbAccount.getCurrency();
        String name = wbAccount.getName11();

        Account account = new Account();
        account.setCode(accountNumber);
        account.setCurrency(currency);
        account.setName(name);
        return account;
    }


    private BookYear convertToTrollBookYear(WbBookYearFull wbYear, Session session) {
        String shortName = wbYear.getShortName();
        LocalDate startDate = wbYear.getStartDate();
        LocalDate endDate = wbYear.getEndDate();

        BookYear bookYear = new BookYear();
        bookYear.setName(shortName);
        bookYear.setStartDate(startDate);
        bookYear.setEndDate(endDate);
        return bookYear;
    }

    private BookPeriod convertToTrollPeriod(WbBookYearFull wbYear, WbPeriod wbPeriod, Session session) {
        AccountingCache sessionCache = session.getCache();
        String yearShortName = wbYear.getShortName();
        BookYear bookYear = sessionCache.findBookYearByName(this, yearShortName)
                .orElseThrow(() -> new WinbooksException(WinbooksError.FATAL_ERRORS, "No book year matching " + yearShortName));
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


    private ThirdParty convertToTrollThirdParty(WbClientSupplier wbSupplier, Session session) {
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

    private AccountingEntry convertToTrollAccountingEntry(WbEntry wbEntry, Session session) {
        AccountingCache sessionCache = session.getCache();

        String bookYearName = wbEntry.getWbBookYearFull().getShortName();
        BookYear bookYear = sessionCache.findBookYearByName(this, bookYearName)
                .orElseThrow(() -> new WinbooksException(WinbooksError.FATAL_ERRORS, "No book year matching " + bookYearName));
        String periodName = wbEntry.getWbPeriod().getShortName();
        BookPeriod bookPeriod = sessionCache.findPeriodByName(this, bookYear, periodName)
                .orElseThrow(() -> new WinbooksException(WinbooksError.FATAL_ERRORS, "No period matching " + periodName + " found for book year " + bookYear));

        BigDecimal amount = wbEntry.getAmountEur();
        BigDecimal vatBase = wbEntry.getVatTax();
        BigDecimal currAmount = wbEntry.getCurrAmount(); // FIXME balance has to be computed?

        String accountFromNumber = wbEntry.getAccountGl();
        Optional<Account> accountOptional = Optional.ofNullable(accountFromNumber)
                .flatMap(number -> sessionCache.findAccountByCode(this, number));

        String accountToNumber = wbEntry.getAccountRp();
        Optional<ThirdParty> thirdPartyOptional = Optional.ofNullable(accountToNumber)
                .flatMap(thirdPartyNumber -> sessionCache.findThirdPartyById(this, thirdPartyNumber));

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

        accountOptional.ifPresent(accountingEntry::setAccount);
        thirdPartyOptional.ifPresent(accountingEntry::setThirdParty);
        documentLocalDateOptional.ifPresent(accountingEntry::setDocumentDate);
        dueDateOptional.ifPresent(accountingEntry::setDueDate);
        accountingEntry.setComment(comment);
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

}

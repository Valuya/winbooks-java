package be.valuya.winbooks.api.accountingtroll.cache;

import be.valuya.accountingtroll.domain.ATAccount;
import be.valuya.accountingtroll.domain.ATAccountingEntry;
import be.valuya.accountingtroll.domain.ATBookPeriod;
import be.valuya.accountingtroll.domain.ATBookYear;
import be.valuya.accountingtroll.domain.ATDocument;
import be.valuya.accountingtroll.domain.ATThirdParty;
import be.valuya.jbooks.model.WbAccount;
import be.valuya.jbooks.model.WbBookYearFull;
import be.valuya.jbooks.model.WbClientSupplier;
import be.valuya.jbooks.model.WbEntry;
import be.valuya.jbooks.model.WbPeriod;
import be.valuya.winbooks.api.accountingtroll.converter.ATAccountConverter;
import be.valuya.winbooks.api.accountingtroll.converter.ATAccountingEntryConverter;
import be.valuya.winbooks.api.accountingtroll.converter.ATBookPeriodConverter;
import be.valuya.winbooks.api.accountingtroll.converter.ATBookYearConverter;
import be.valuya.winbooks.api.accountingtroll.converter.ATDocumentConverter;
import be.valuya.winbooks.api.accountingtroll.converter.ATThirdPartyConverter;
import be.valuya.winbooks.api.extra.WinbooksExtraService;
import be.valuya.winbooks.api.extra.config.DocumentMatchingMode;
import be.valuya.winbooks.api.extra.config.WinbooksFileConfiguration;
import be.valuya.winbooks.domain.error.WinbooksError;
import be.valuya.winbooks.domain.error.WinbooksException;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AccountingManagerCache {

    private List<WbBookYearFull> wbBookYearFulls;
    private Map<String, ATBookYear> bookYearsByShortName;
    private Map<String, List<ATBookPeriod>> bookPeriodsByBookYearShortName;
    private Map<String, ATAccount> accountsByCode;
    private Map<String, ATThirdParty> thirdPartiesById;
    private Map<ATDocumentCacheKey, ATDocument> documentsByCacheKey;
    private List<ATAccountingEntry> accountingEntries;

    private final WinbooksExtraService extraService;
    private final WinbooksFileConfiguration fileConfiguration;

    private final ATAccountConverter atAccountConverter;
    private final ATAccountingEntryConverter atAccountingEntryConverter;
    private final ATBookYearConverter atBookYearConverter;
    private final ATBookPeriodConverter atBookPeriodConverter;
    private final ATThirdPartyConverter atThirdPartyConverter;
    private final ATDocumentConverter atDocumentConverter;

    public AccountingManagerCache(WinbooksFileConfiguration fileConfiguration) {
        this.fileConfiguration = fileConfiguration;
        this.extraService = new WinbooksExtraService();

        atAccountConverter = new ATAccountConverter();
        atAccountingEntryConverter = new ATAccountingEntryConverter(this);
        atBookYearConverter = new ATBookYearConverter();
        atBookPeriodConverter = new ATBookPeriodConverter(this);
        atThirdPartyConverter = new ATThirdPartyConverter();
        atDocumentConverter = new ATDocumentConverter(this);
    }

    public Stream<WbBookYearFull> streamWbBookYearFulls() {
        this.cachWbBookYearFull();
        return wbBookYearFulls.stream();
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

    public Stream<ATDocument> streamDocuments() {
        this.cacheDocuments();
        return documentsByCacheKey.values().stream();
    }

    public ATBookYear getCachedBookYearOrThrow(String bookYearShortName) {
        cacheBookYears();
        ATBookYear bookYearNullable = bookYearsByShortName.get(bookYearShortName);
        return Optional.ofNullable(bookYearNullable)
                .orElseThrow(() -> new WinbooksException(WinbooksError.FATAL_ERRORS, "No book year matching short name " + bookYearShortName));
    }

    public ATBookPeriod getCachedBookPeriodOrThrow(WbPeriod wbPeriod) {
        cacheBookPeriods();
        WbBookYearFull wbBookYearFull = wbPeriod.getWbBookYearFull();
        String shortName = wbBookYearFull.getShortName();
        List<ATBookPeriod> periodListNullable = bookPeriodsByBookYearShortName.get(shortName);
        return Optional.ofNullable(periodListNullable)
                .map(List::stream)
                .orElse(Stream.empty())
                .filter(atPeriod -> this.isSamePeriod(atPeriod, wbPeriod))
                .findAny()
                .orElseThrow(() -> new WinbooksException(WinbooksError.FATAL_ERRORS, "Could not find period for wbperiod " + wbPeriod));
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

        int accountNumberLength = extraService.getAccountNumberLengthFromParamsTable(fileConfiguration);
        accountsByCode = extraService.streamAcf(fileConfiguration)
                .filter(this::isValidAccount)
                .map(wbAccount -> atAccountConverter.convertToTrollAcccount(wbAccount, accountNumberLength))
                .collect(Collectors.toMap(
                        ATAccount::getCode,
                        Function.identity(),
                        (t1, t2) -> t1)// Override in case of dupplicates
                );
    }


    private void cachWbBookYearFull() {
        if (wbBookYearFulls != null) {
            return;
        }
        wbBookYearFulls = extraService.streamBookYears(fileConfiguration)
                .collect(Collectors.toList());
    }

    private void cacheBookYears() {
        if (bookYearsByShortName != null) {
            return;
        }
        cachWbBookYearFull();
        bookYearsByShortName = wbBookYearFulls.stream()
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
        cacheBookYears();
        cacheBookPeriods();

        DocumentMatchingMode documentMatchingMode = fileConfiguration.getDocumentMatchingMode();
        if (documentMatchingMode == DocumentMatchingMode.EAGERLY_CACHE_ALL_DOCUMENTS) {
            cacheDocuments();
        }

        accountingEntries = extraService.streamAct(fileConfiguration)
                .filter(this::isValidAccountingEntry)
                .map(atAccountingEntryConverter::convertToTrollAccountingEntry)
                .map(e -> this.linkEntryDocument(e, documentMatchingMode))
                .collect(Collectors.toList());
    }

    private ATAccountingEntry linkEntryDocument(ATAccountingEntry accountingEntry, DocumentMatchingMode documentMatchingMode) {
        if (documentMatchingMode == DocumentMatchingMode.EAGERLY_CACHE_ALL_DOCUMENTS) {
            Optional<ATDocument> documentOptional = this.findAccountingEntryDocument(accountingEntry);
            accountingEntry.setDocumentOptional(documentOptional);
            return accountingEntry;
        } else {
            return accountingEntry;
        }
    }

    private Optional<ATDocument> findAccountingEntryDocument(ATAccountingEntry accountingEntry) {
        String dbkCode = accountingEntry.getDbkCode();
        ATBookPeriod bookPeriod = accountingEntry.getBookPeriod();
        String docNumber = accountingEntry.getDocNumber();
        ATDocumentCacheKey cacheKey = new ATDocumentCacheKey(docNumber, dbkCode, bookPeriod);
        ATDocument documentNullable = documentsByCacheKey.get(cacheKey);

        return Optional.ofNullable(documentNullable);
    }

    private void cacheDocuments() {
        if (documentsByCacheKey != null) {
            return;
        }
        documentsByCacheKey = streamWbBookYearFulls()
                .flatMap(bookYear -> extraService.streamBookYearDocuments(fileConfiguration, bookYear))
                .map(atDocumentConverter::convertDocument)
                .collect(Collectors.toMap(
                        ATDocumentCacheKey::new,
                        Function.identity()
                ));
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
        String dbkCode = wbEntry.getDbkCode();
        boolean isSimulationLedger = dbkCode.equals("ODSIMU"); // TODO: read from journal table, set flag on accounting entry?
        return wbEntry.getAccountGl() != null // TODO: find out how to handle these
                && wbEntry.getWbPeriod() != null // TODO: throw or warn?
                && !isSimulationLedger;
//        return !isSimulationLedger && wbEntry.getAccountGl() != null;
    }

    private boolean isSamePeriod(ATBookPeriod atPeriod, WbPeriod period) {
        String periodName = atPeriod.getName();
        return periodName.equals(period.getShortName());
    }


}

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
import be.valuya.jbooks.model.WbClientSupplierType;
import be.valuya.jbooks.model.WbDocType;
import be.valuya.jbooks.model.WbDocument;
import be.valuya.jbooks.model.WbEntry;
import be.valuya.jbooks.model.WbPeriod;
import be.valuya.winbooks.api.accountingtroll.converter.ATAccountConverter;
import be.valuya.winbooks.api.accountingtroll.converter.ATAccountingEntryConverter;
import be.valuya.winbooks.api.accountingtroll.converter.ATBookPeriodConverter;
import be.valuya.winbooks.api.accountingtroll.converter.ATBookYearConverter;
import be.valuya.winbooks.api.accountingtroll.converter.ATDocumentConverter;
import be.valuya.winbooks.api.accountingtroll.converter.ATThirdPartyConverter;
import be.valuya.winbooks.api.accountingtroll.converter.ATThirdPartyIdFactory;
import be.valuya.winbooks.api.extra.WinbooksExtraService;
import be.valuya.winbooks.api.extra.config.DocumentMatchingMode;
import be.valuya.winbooks.api.extra.config.WinbooksFileConfiguration;
import be.valuya.winbooks.domain.error.WinbooksError;
import be.valuya.winbooks.domain.error.WinbooksException;

import java.util.List;
import java.util.Map;
import java.util.Objects;
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
        return bookYearsByShortName.values().stream()
                .sorted();
    }

    public Stream<ATBookPeriod> streamPeriods() {
        this.cacheBookPeriods();
        return bookPeriodsByBookYearShortName.values()
                .stream()
                .flatMap(List::stream)
                .sorted();
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


    public Optional<ATThirdParty> getCachedThirdPartyOptional(String accountId, WbDocType wbDocType) {
        cacheThirdParties();
        switch (wbDocType) {
            case IMPUT_CLIENT:
                return getCachedCustomerThirdPartyOptional(accountId);
            case IMPUT_SUPPLIER:
                return getCachedSupplierThirdPartyOptional(accountId);
            default:
                return getCachedCustomerThirdPartyOptional(accountId)
                        .or(() -> getCachedSupplierThirdPartyOptional(accountId));
        }
    }

    public Optional<ATThirdParty> getCachedCustomerThirdPartyOptional(String accountId) {
        String id = ATThirdPartyIdFactory.getId(WbClientSupplierType.CLIENT, accountId);
        ATThirdParty thirdParty = thirdPartiesById.get(id);
        return Optional.ofNullable(thirdParty);
    }

    public Optional<ATThirdParty> getCachedSupplierThirdPartyOptional(String accountId) {
        String id = ATThirdPartyIdFactory.getId(WbClientSupplierType.SUPPLIER, accountId);
        ATThirdParty thirdParty = thirdPartiesById.get(id);
        return Optional.ofNullable(thirdParty);
    }

    private void cacheAccounts() {
        if (accountsByCode != null) {
            return;
        }

        int accountNumberLength = extraService.getAccountNumberLengthFromParamsTable(fileConfiguration);
        accountsByCode = extraService.streamAcf(fileConfiguration)
                .filter(this::isValidAccount)
                .map(wbAccount -> this.safeConvertToTrollAccount(wbAccount, accountNumberLength))
                .peek(e -> this.checkThrowOnConversion(e, WinbooksError.CANNOT_OPEN_DOSSIER))
                .flatMap(this::streamOptional)
                .collect(Collectors.toMap(
                        ATAccount::getCode,
                        Function.identity(),
                        (t1, t2) -> t1)// Override in case of dupplicates
                );
    }

    private <T> void checkThrowOnConversion(Optional<T> converterEntity, WinbooksError error) {
        boolean ignoreConversionErrors = fileConfiguration.isIgnoreConversionErrors();
        if (!ignoreConversionErrors && !converterEntity.isPresent()) {
            throw new WinbooksException(error, "Conversion errored");
        }
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
                .map(this::safeConvertToTrollBookYear)
                .peek(e -> this.checkThrowOnConversion(e, WinbooksError.NO_BOOKYEAR))
                .flatMap(this::streamOptional)
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
                .map(this::safeConvertToTrollThirdParty)
                .peek(e -> this.checkThrowOnConversion(e, WinbooksError.CANNOT_OPEN_DOSSIER))
                .flatMap(this::streamOptional)
                .collect(Collectors.toMap(
                        ATThirdParty::getId,
                        Function.identity(),
                        (a, b) -> {
                            System.out.println("Multiple third party with same id " + a.getId() + " : " + a + " and " + b);
                            return b; // OVerwrite in case of duplicates
                        }
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
                .map(this::safeConvertToTrollAccountingEntry)
                .peek(e -> this.checkThrowOnConversion(e, WinbooksError.USER_FILE_ERROR))
                .flatMap(this::streamOptional)
                .map(e -> this.linkEntryDocument(e, documentMatchingMode))
                .collect(Collectors.toList());
    }

    private Optional<ATAccountingEntry> safeConvertToTrollAccountingEntry(WbEntry wbEntry) {
        try {
            ATAccountingEntry atAccountingEntry = atAccountingEntryConverter.convertToTrollAccountingEntry(wbEntry);
            return Optional.of(atAccountingEntry);
        } catch (WinbooksException e) {
            System.err.println(e);
            return Optional.empty();
        }
    }

    private Optional<ATThirdParty> safeConvertToTrollThirdParty(WbClientSupplier wbSupplier) {
        try {
            ATThirdParty aThirdParty = atThirdPartyConverter.convertToTrollThirdParty(wbSupplier);
            return Optional.of(aThirdParty);
        } catch (WinbooksException e) {
            return Optional.empty();
        }
    }

    private Optional<ATBookYear> safeConvertToTrollBookYear(WbBookYearFull wbBookYearFull) {
        try {
            ATBookYear atBookYear = atBookYearConverter.convertToTrollBookYear(wbBookYearFull);
            return Optional.of(atBookYear);
        } catch (WinbooksException e) {
            return Optional.empty();
        }
    }

    private Optional<ATAccount> safeConvertToTrollAccount(WbAccount wbAccount, int accountNumberLengths) {
        try {
            ATAccount atAccount = atAccountConverter.convertToTrollAcccount(wbAccount, accountNumberLengths);
            return Optional.of(atAccount);
        } catch (WinbooksException e) {
            return Optional.empty();
        }
    }

    private Optional<ATBookPeriod> safeConvertToTrollPeriod(WbPeriod wbPeriod, WbBookYearFull bookYearFull) {
        try {
            ATBookPeriod atBookPeriod = atBookPeriodConverter.convertToTrollPeriod(bookYearFull, wbPeriod);
            return Optional.of(atBookPeriod);
        } catch (WinbooksException e) {
            return Optional.empty();
        }
    }

    private Optional<ATDocument> safeConvertToDocument(WbDocument wbDocument) {
        try {
            ATDocument atDocument = atDocumentConverter.convertDocument(wbDocument);
            return Optional.of(atDocument);
        } catch (WinbooksException e) {
            return Optional.empty();
        }
    }

    private ATAccountingEntry linkEntryDocument(ATAccountingEntry accountingEntry, DocumentMatchingMode documentMatchingMode) {
        if (documentMatchingMode == DocumentMatchingMode.EAGERLY_CACHE_ALL_DOCUMENTS) {
            Optional<ATDocument> documentOptional = this.findAccountingEntryDocument(accountingEntry);
            documentOptional.ifPresent(accountingEntry::setDocument);
        }
        return accountingEntry;
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
                .map(this::safeConvertToDocument)
                .peek(e -> this.checkThrowOnConversion(e, WinbooksError.USER_FILE_ERROR))
                .flatMap(this::streamOptional)
                .collect(Collectors.toMap(
                        ATDocumentCacheKey::new,
                        Function.identity()
                ));
    }


    private Stream<ATBookPeriod> streamYearPeriods(WbBookYearFull bookYearFull) {
        return bookYearFull.getPeriodList().stream()
                .map(period -> this.safeConvertToTrollPeriod(period, bookYearFull))
                .peek(e -> this.checkThrowOnConversion(e, WinbooksError.BOOKYEAR_NOT_FOUND))
                .flatMap(this::streamOptional);
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

    private <T> Stream<T> streamOptional(Optional<T> optionalValue) {
        return Stream.of(optionalValue.orElse(null))
                .filter(Objects::nonNull);
    }

}

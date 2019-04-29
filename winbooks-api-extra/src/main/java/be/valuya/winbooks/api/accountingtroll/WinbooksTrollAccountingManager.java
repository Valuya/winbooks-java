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
import be.valuya.accountingtroll.event.AccountingEventHandler;
import be.valuya.accountingtroll.event.BalanceChangeEvent;
import be.valuya.jbooks.model.WbDocument;
import be.valuya.winbooks.api.accountingtroll.cache.AccountBalance;
import be.valuya.winbooks.api.accountingtroll.cache.AccountBalanceCache;
import be.valuya.winbooks.api.accountingtroll.cache.AccountingManagerCache;
import be.valuya.winbooks.api.accountingtroll.converter.ATDocumentConverter;
import be.valuya.winbooks.api.extra.WinbooksExtraService;
import be.valuya.winbooks.api.extra.config.WinbooksFileConfiguration;
import be.valuya.winbooks.domain.error.WinbooksError;
import be.valuya.winbooks.domain.error.WinbooksException;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class WinbooksTrollAccountingManager implements AccountingManager {

    private static final BigDecimal ZERO = BigDecimal.ZERO.setScale(3, BigDecimal.ROUND_UNNECESSARY);
    private static final String DOCUMENTS_PATH_NAME = "Document";
    private static final String DOCUMENT_UPLOAD_PATH_NAME = "Scans";

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
        AccountBalanceCache accountBalanceCache = new AccountBalanceCache(accountingManagerCache);

        List<ATAccountingEntry> atAccountingEntries = accountingManagerCache.streamAccountingEntries()
                .sorted()
                .flatMap(entry -> convertWithBalanceCheck(entry, accountBalanceCache, accountingEventListener))
                .collect(Collectors.toList()); // has to consume the stream to trigger side effects

        // Reemit resetted balances for last book year
        accountBalanceCache.getLastBookYear()
                .ifPresent(bookYear -> this.emitResettedBalancesEvents(bookYear, accountBalanceCache, accountingEventListener));

        return atAccountingEntries.stream();
    }


    @Override
    public Stream<ATDocument> streamDocuments() {
        return accountingManagerCache.streamDocuments();
    }

    @Override
    public InputStream streamDocumentContent(ATDocument atDocument) throws Exception {
        AccountingEventListener eventListener = new AccountingEventHandler(); //TODO
        ATDocumentConverter documentConverter = new ATDocumentConverter(accountingManagerCache);
        WbDocument wbDocument = documentConverter.convertWbDocument(atDocument);
        byte[] documentdata = extraService.getDocumentData(fileConfiguration, wbDocument, eventListener)
                .orElseThrow(() -> new WinbooksException(WinbooksError.FATAL_ERRORS, "Could not find document date"));

        return new ByteArrayInputStream(documentdata);
    }

    @Override
    public void uploadDocument(Path documentPath, InputStream inputStream) throws Exception {
        Path baseFolderPath = fileConfiguration.getBaseFolderPath();
        Path documentFullPath = baseFolderPath.resolve(DOCUMENTS_PATH_NAME)
                .resolve(DOCUMENT_UPLOAD_PATH_NAME)
                .resolve(documentPath);
        Path documentDirectoryPath = documentFullPath.getParent();

        // TODO: resolve case-insensitive parent paths if they exists
        Files.createDirectories(documentDirectoryPath);
        Files.copy(inputStream, documentFullPath, StandardCopyOption.REPLACE_EXISTING);
    }

    private Stream<ATAccountingEntry> convertWithBalanceCheck(ATAccountingEntry atAccountingEntry,
                                                              AccountBalanceCache accountBalanceCache,
                                                              AccountingEventListener accountingEventListener) {
        AccountingEntryDocumentNumberType documentNumberType = atAccountingEntry.getDocNumberTypeOptional()
                .orElse(AccountingEntryDocumentNumberType.DEFAULT);
        ATAccount atAccount = atAccountingEntry.getAccount();

        if (documentNumberType == AccountingEntryDocumentNumberType.BALANCE) {
            accountBalanceCache.resetAccountBalance(atAccountingEntry);
            return Stream.empty();
        }

        ATBookPeriod bookPeriod = atAccountingEntry.getBookPeriod();
        ATBookYear bookYear = bookPeriod.getBookYear();
        ATPeriodType periodType = bookPeriod.getPeriodType();
        AccountBalance accountBalance;

        if (periodType == ATPeriodType.OPENING) {
            accountBalanceCache.resetAccountBalance(atAccountingEntry);
            accountBalance = accountBalanceCache.getAccountBalance(atAccount, bookYear);
        } else if (periodType == ATPeriodType.CLOSING) {
            accountBalanceCache.appendToAccountBalance(atAccountingEntry);
            accountBalance = accountBalanceCache.getAccountBalance(atAccount, bookYear);
        } else if (periodType == ATPeriodType.GENERAL) {
            accountBalanceCache.appendToAccountBalance(atAccountingEntry);
            accountBalance = accountBalanceCache.getAccountBalance(atAccount, bookYear);
        } else {
            return Stream.empty();
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


    private void emitResettedBalancesEvents(ATBookYear bookYear, AccountBalanceCache accountBalanceCache, AccountingEventListener accountingEventListener) {
        List<ATAccount> accountWithoutBalanceInLastBookYear = accountBalanceCache.findAccountWithoutBalanceInBookYear(bookYear);
        accountWithoutBalanceInLastBookYear.stream()
                .filter(ATAccount::isYearlyBalanceReset)
                .forEach(account -> this.emitBalanceResetEvent(bookYear, account, accountingEventListener));
    }

    private void emitBalanceResetEvent(ATBookYear bookYear, ATAccount account, AccountingEventListener accountingEventListener) {
        ATBookPeriod openingPeriod = this.findBookyearOpeningPeriod(bookYear);
        LocalDate startDate = openingPeriod.getStartDate();

        BalanceChangeEvent balanceChangeEvent = new BalanceChangeEvent();
        balanceChangeEvent.setDate(startDate);
        balanceChangeEvent.setNewBalance(ZERO);
        balanceChangeEvent.setAccount(account);

        accountingEventListener.handleBalanceChangeEvent(balanceChangeEvent);
    }

    private ATBookPeriod findBookyearOpeningPeriod(ATBookYear bookYear) {
        return accountingManagerCache.streamPeriods()
                .filter(period -> this.isBookyearOpeningPeriod(period, bookYear))
                .findAny()
                .orElseThrow(() -> new WinbooksException(WinbooksError.UNKNOWN_ERROR, "No opening period for book year " + bookYear));
    }

    private boolean isBookyearOpeningPeriod(ATBookPeriod bookPeriod, ATBookYear bookYear) {
        ATBookYear periodBookYear = bookPeriod.getBookYear();
        ATPeriodType periodType = bookPeriod.getPeriodType();
        return periodType == ATPeriodType.OPENING && periodBookYear.equals(bookYear);
    }


}

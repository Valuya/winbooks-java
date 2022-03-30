package be.valuya.winbooks.api.accountingtroll.converter;

import be.valuya.accountingtroll.domain.ATAccount;
import be.valuya.accountingtroll.domain.ATAccountingEntry;
import be.valuya.accountingtroll.domain.ATBookPeriod;
import be.valuya.accountingtroll.domain.ATCurrencyAmount;
import be.valuya.accountingtroll.domain.ATDocument;
import be.valuya.accountingtroll.domain.ATTax;
import be.valuya.accountingtroll.domain.ATThirdParty;
import be.valuya.accountingtroll.domain.AccountingEntryDocumentNumberType;
import be.valuya.accountingtroll.domain.AccountingEntryDocumentType;
import be.valuya.accountingtroll.domain.AccountingEntryType;
import be.valuya.jbooks.model.WbDbkType;
import be.valuya.jbooks.model.WbDocOrderType;
import be.valuya.jbooks.model.WbDocType;
import be.valuya.jbooks.model.WbEntry;
import be.valuya.jbooks.model.WbPeriod;
import be.valuya.winbooks.api.accountingtroll.cache.AccountingManagerCache;
import be.valuya.winbooks.domain.error.WinbooksError;
import be.valuya.winbooks.domain.error.WinbooksException;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;

public class ATAccountingEntryConverter {

    private final AccountingManagerCache accountingManagerCache;

    public ATAccountingEntryConverter(AccountingManagerCache accountingManagerCache) {
        this.accountingManagerCache = accountingManagerCache;
    }

    public ATAccountingEntry convertToTrollAccountingEntry(WbEntry wbEntry) {
        WbPeriod wbPeriod = wbEntry.getWbPeriod();
        ATBookPeriod bookPeriod = accountingManagerCache.getCachedBookPeriodOrThrow(wbPeriod);
        WbDocType wbDocType = wbEntry.getWbDocType();

        BigDecimal amount = wbEntry.getAmountEur().negate();
        String dbkCode = wbEntry.getDbkCode();
        String docNumber = wbEntry.getDocNumber();
        int docOrder = Optional.ofNullable(wbEntry.getDocOrder())
                .orElse(0);

        String accountFromNumber = wbEntry.getAccountGl();
        ATAccount account = accountingManagerCache.getCachedAccountByCodeOptional(accountFromNumber)
                .orElseThrow(() -> new WinbooksException(WinbooksError.FATAL_ERRORS, "No account found for number " + accountFromNumber));

        String accountToId = wbEntry.getAccountRp();
        Optional<ATThirdParty> thirdPartyOptional = accountingManagerCache.getCachedThirdPartyOptional(accountToId, wbDocType);

        Optional<ATTax> taxOptional = convertTaxOptional(wbEntry);
        Optional<ATCurrencyAmount> currencyAmountOptional = convertCurrencyAmountOtional(wbEntry);

        // Matching is done post-conversion
        Optional<ATDocument> documentOptional = Optional.empty();

        Date entryDate = wbEntry.getDate();
        Date documentDate = wbEntry.getDateDoc();
        Date dueDate = wbEntry.getDueDate();
        boolean matched = wbEntry.isMatched();
        String matchNo = wbEntry.getMatchNo();

        WbDocOrderType wbDocOrderType = wbEntry.getWbDocOrderType();
        AccountingEntryDocumentNumberType documentNumberType = this.getDocNumberType(wbDocOrderType);

        AccountingEntryDocumentType documentType = this.getDocType(wbDocType);

        WbDbkType wbDbkType = wbEntry.getWbDbkType();
        AccountingEntryType accountingEntryType = this.getEntryType(wbDbkType);

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
        accountingEntry.setAccount(account);
        accountingEntry.setMatched(matched);
        accountingEntry.setMatchNumber(matchNo);
        accountingEntry.setDocNumber(docNumber);
        accountingEntry.setDocNumberType(documentNumberType);
        accountingEntry.setOrderingNumber(docOrder);
        accountingEntry.setAccountingEntryDocumentType(documentType);
        accountingEntry.setAccountingEntryType(accountingEntryType);

        taxOptional.ifPresent(accountingEntry::setTax);
        currencyAmountOptional.ifPresent(accountingEntry::setCurrencyAmount);

        thirdPartyOptional.ifPresent(accountingEntry::setThirdParty);
        documentLocalDateOptional.ifPresent(accountingEntry::setDocumentDate);
        dueDateOptional.ifPresent(accountingEntry::setDueDate);
        accountingEntry.setComment(comment);
        documentOptional.ifPresent(accountingEntry::setDocument);

        return accountingEntry;
    }

    private Optional<ATCurrencyAmount> convertCurrencyAmountOtional(WbEntry wbEntry) {
        BigDecimal currAmount = wbEntry.getCurrAmount();
        String currCode = wbEntry.getCurrCode();
        BigDecimal curRate = wbEntry.getCurRate();
        BigDecimal curEurBase = wbEntry.getCurEurBase();
        if (currAmount == null || currCode == null || currCode.isBlank()) {
            return Optional.empty();
        }

        ATCurrencyAmount currencyAmount = new ATCurrencyAmount();

        currencyAmount.setAmount(currAmount);

        currencyAmount.setCurrencyCode(currCode);

        currencyAmount.setRate(curRate);

        currencyAmount.setEuroBase(curEurBase);

        return Optional.of(currencyAmount);
    }

    private Optional<ATTax> convertTaxOptional(WbEntry wbEntry) {

        ATTax atTax = new ATTax();

        String wbVatCode = wbEntry.getVatCode();
        Optional.ofNullable(wbVatCode)
                .flatMap(accountingManagerCache::getCachedVatCodeById)
                .ifPresent(atTax::setVatCode);

        atTax.setVatBase(wbEntry.getVatBase());
        atTax.setVatAmount(wbEntry.getVatTax());
        atTax.setVatImputation(wbEntry.getVatImput());

        return Optional.of(atTax);
    }

    private AccountingEntryType getEntryType(WbDbkType wbDbkType) {
        switch (wbDbkType) {
            case PURCHASE:
                return AccountingEntryType.PURCHASE;
            case CREDIT_NOTE_PURCHASE:
                return AccountingEntryType.PURCHASE_CREDIT_NOTE;
            case SALE:
                return AccountingEntryType.SALE;
            case CREDIT_NOTE_SALE:
                return AccountingEntryType.SALE_CREDIT_NOTE;
            case FINANCE:
                return AccountingEntryType.FINANCIAL;
            default:
                return AccountingEntryType.MISC;
        }
    }

    private AccountingEntryDocumentType getDocType(WbDocType wbDocType) {
        switch (wbDocType) {
            case IMPUT_CLIENT:
                return AccountingEntryDocumentType.CUSTOMER_ACCOUNT_ALLOCATION;
            case IMPUT_SUPPLIER:
                return AccountingEntryDocumentType.SUPPLIER_ACCOUNT_ALLOCATION;
            case IMPUT_GENERAL:
                return AccountingEntryDocumentType.GENERAL_ACCOUNT_ALLOCATION;
            case VAT_ZERO:
                return AccountingEntryDocumentType.ZERO_VAT;
            default:
                throw new IllegalArgumentException(wbDocType.name());
        }
    }

    private AccountingEntryDocumentNumberType getDocNumberType(WbDocOrderType docOrderType) {
        switch (docOrderType) {
            case NUMBER:
                return AccountingEntryDocumentNumberType.DEFAULT;
            case VAT:
                return AccountingEntryDocumentNumberType.VAT;
            case DEBIT_CENTRAL:
                return AccountingEntryDocumentNumberType.DEBIT_CENTRAL;
            case CREDIT_CENTRAL:
                return AccountingEntryDocumentNumberType.CREDIT_CENTRAL;
            case A:
                return AccountingEntryDocumentNumberType.A;
            case BALANCE:
                return AccountingEntryDocumentNumberType.BALANCE;
            default:
                return AccountingEntryDocumentNumberType.DEFAULT;
        }
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

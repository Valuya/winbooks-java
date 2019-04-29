package be.valuya.winbooks.api.accountingtroll.converter;

import be.valuya.accountingtroll.domain.ATAccount;
import be.valuya.accountingtroll.domain.ATAccountingEntry;
import be.valuya.accountingtroll.domain.ATBookPeriod;
import be.valuya.accountingtroll.domain.ATDocument;
import be.valuya.accountingtroll.domain.ATTax;
import be.valuya.accountingtroll.domain.ATThirdParty;
import be.valuya.accountingtroll.domain.AccountingEntryDocumentNumberType;
import be.valuya.jbooks.model.WbBookYearFull;
import be.valuya.jbooks.model.WbDocOrderType;
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
        WbBookYearFull wbBookYearFull = wbEntry.getWbBookYearFull();
        WbPeriod wbPeriod = wbEntry.getWbPeriod();
        ATBookPeriod bookPeriod = accountingManagerCache.getCachedBookPeriodOrThrow(wbPeriod);

        BigDecimal amount = wbEntry.getAmountEur();
        String dbkCode = wbEntry.getDbkCode();
        String docNumber = wbEntry.getDocNumber();
        int docOrder = Optional.ofNullable(wbEntry.getDocOrder())
                .orElse(0);

        String accountFromNumber = wbEntry.getAccountGl();
        ATAccount account = accountingManagerCache.getCachedAccountByCodeOptional(accountFromNumber)
                .orElseThrow(() -> new WinbooksException(WinbooksError.FATAL_ERRORS, "No account found for number " + accountFromNumber));

        String accountToId = wbEntry.getAccountRp();
        Optional<ATThirdParty> thirdPartyOptional = accountingManagerCache.getCachedThirdPartyOptional(accountToId);

        Optional<ATTax> taxOptional = Optional.empty(); // TODO
        // Matching is done post-conversion
        Optional<ATDocument> documentOptional = Optional.empty();

        Optional<String> matchNo = Optional.ofNullable(wbEntry.getMatchNo()).map(String::trim);
        Date entryDate = wbEntry.getDate();
        Date documentDate = wbEntry.getDateDoc();
        Date dueDate = wbEntry.getDueDate();

        WbDocOrderType wbDocOrderType = wbEntry.getWbDocOrderType();
        AccountingEntryDocumentNumberType documentNumberType = this.getDocNumberType(wbDocOrderType);

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
        accountingEntry.setMatched(matchNo.isPresent());
        accountingEntry.setDocNumber(docNumber);
        accountingEntry.setDocNumberTypeOptional(Optional.of(documentNumberType));
        accountingEntry.setOrderingNumber(docOrder);

        accountingEntry.setTaxOptional(taxOptional);
        accountingEntry.setThirdPartyOptional(thirdPartyOptional);
        accountingEntry.setDocumentDateOptional(documentLocalDateOptional);
        accountingEntry.setDueDateOptional(dueDateOptional);
        accountingEntry.setCommentOptional(Optional.ofNullable(comment));
        accountingEntry.setDocumentOptional(documentOptional);

        return accountingEntry;
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

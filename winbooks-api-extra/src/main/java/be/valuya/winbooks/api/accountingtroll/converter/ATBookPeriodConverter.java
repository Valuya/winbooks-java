package be.valuya.winbooks.api.accountingtroll.converter;

import be.valuya.accountingtroll.domain.ATBookPeriod;
import be.valuya.accountingtroll.domain.ATBookYear;
import be.valuya.accountingtroll.domain.ATPeriodType;
import be.valuya.jbooks.model.WbBookYearFull;
import be.valuya.jbooks.model.WbPeriod;
import be.valuya.winbooks.api.accountingtroll.cache.AccountingManagerCache;

import java.text.MessageFormat;
import java.time.LocalDate;

public class ATBookPeriodConverter {

    private final AccountingManagerCache accountingManagerCache;

    public ATBookPeriodConverter(AccountingManagerCache accountingManagerCache) {
        this.accountingManagerCache = accountingManagerCache;
    }

    public ATBookPeriod convertToTrollPeriod(WbBookYearFull wbYear, WbPeriod wbPeriod) {
        String yearShortName = wbYear.getShortName();
        ATBookYear bookYear = accountingManagerCache.getCachedBookYearOrThrow(yearShortName);
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


    private ATPeriodType getAccountingPeriodType(WbPeriod wbPeriod) {
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

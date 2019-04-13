package be.valuya.winbooks.api.accountingtroll;

import be.valuya.accountingtroll.TrollBookYear;
import be.valuya.accountingtroll.TrollPeriod;
import be.valuya.jbooks.model.WbPeriod;

import java.time.LocalDate;

public class WinbooksTrollPeriod implements TrollPeriod {

    private WbPeriod wbPeriod;
    private TrollBookYear bookYear;

    public WinbooksTrollPeriod(WbPeriod wbPeriod, TrollBookYear bookYear) {
        this.wbPeriod = wbPeriod;
        this.bookYear = bookYear;
    }

    @Override
    public String getName() {
        return wbPeriod.getShortName();
    }

    @Override
    public LocalDate getStartDate() {
        return wbPeriod.getStartDate();
    }

    @Override
    public LocalDate getEndDate() {
        return wbPeriod.getEndDate();
    }

    @Override
    public TrollBookYear getBookYear() {
        return bookYear;
    }
}

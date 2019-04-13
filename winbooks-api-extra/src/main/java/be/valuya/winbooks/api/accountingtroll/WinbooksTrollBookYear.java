package be.valuya.winbooks.api.accountingtroll;

import be.valuya.accountingtroll.TrollBookYear;
import be.valuya.jbooks.model.WbBookYearFull;

import java.time.LocalDate;
import java.util.stream.Stream;

public class WinbooksTrollBookYear implements TrollBookYear {

    private WbBookYearFull wbBookYearFull;

    public WinbooksTrollBookYear(WbBookYearFull wbBookYearFull) {
        this.wbBookYearFull = wbBookYearFull;
    }

    @Override
    public String getName() {
        return wbBookYearFull.getShortName();
    }

    @Override
    public LocalDate getStartDate() {
        return wbBookYearFull.getStartDate();
    }

    @Override
    public LocalDate getEndDate() {
        return wbBookYearFull.getEndDate();
    }

    public Stream<WinbooksTrollPeriod> streamPeriods() {
        return wbBookYearFull.getPeriodList().stream()
                .map(p -> new WinbooksTrollPeriod(p, this));
    }
}

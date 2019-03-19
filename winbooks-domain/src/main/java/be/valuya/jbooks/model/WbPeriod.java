package be.valuya.jbooks.model;

import java.time.LocalDate;
import java.util.Objects;

public class WbPeriod {

    private WbBookYearFull wbBookYearFull;
    private int index;
    private String shortName;
    private LocalDate startDate;
    private LocalDate endDate;

    public WbBookYearFull getWbBookYearFull() {
        return wbBookYearFull;
    }

    public void setWbBookYearFull(WbBookYearFull wbBookYearFull) {
        this.wbBookYearFull = wbBookYearFull;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        WbPeriod period = (WbPeriod) o;
        return index == period.index &&
                Objects.equals(wbBookYearFull, period.wbBookYearFull);
    }

    @Override
    public int hashCode() {
        return Objects.hash(wbBookYearFull, index);
    }

    @Override
    public String toString() {
        return "WbPeriod{" +
                "wbBookYearFull=" + wbBookYearFull +
                ", index=" + index +
                ", shortName='" + shortName + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                '}';
    }
}

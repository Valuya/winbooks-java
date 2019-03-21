package be.valuya.jbooks.model;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class WbBookYearFull {

    private int index;
    private int yearBeginInt;
    private int yearEndInt;
    private String shortName;
    private String longName;
    private LocalDate startDate;
    private LocalDate endDate;
    private int periods;
    private WbBookYearStatus wbBookYearStatus;
    private Optional<String> archivePathNameOptional;
    private List<WbPeriod> periodList;

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getYearBeginInt() {
        return yearBeginInt;
    }

    public void setYearBeginInt(int yearBeginInt) {
        this.yearBeginInt = yearBeginInt;
    }

    public int getYearEndInt() {
        return yearEndInt;
    }

    public void setYearEndInt(int yearEndInt) {
        this.yearEndInt = yearEndInt;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getLongName() {
        return longName;
    }

    public void setLongName(String longName) {
        this.longName = longName;
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

    public int getPeriods() {
        return periods;
    }

    public void setPeriods(int periods) {
        this.periods = periods;
    }

    public Optional<String> getArchivePathNameOptional() {
        return archivePathNameOptional;
    }

    public void setArchivePathNameOptional(Optional<String> archivePathNameOptional) {
        this.archivePathNameOptional = archivePathNameOptional;
    }

    public void setArchivePathName(String archivePathName) {
        this.archivePathNameOptional = Optional.of(archivePathName);
    }

    public List<WbPeriod> getPeriodList() {
        return periodList;
    }

    public void setPeriodList(List<WbPeriod> periodList) {
        this.periodList = periodList;
    }

    public WbBookYearStatus getWbBookYearStatus() {
        return wbBookYearStatus;
    }

    public void setWbBookYearStatus(WbBookYearStatus wbBookYearStatus) {
        this.wbBookYearStatus = wbBookYearStatus;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        WbBookYearFull that = (WbBookYearFull) o;
        return index == that.index;
    }

    @Override
    public int hashCode() {
        return Objects.hash(index);
    }

    @Override
    public String toString() {
        return "WbBookYearFull{" + "index=" + index + ", yearBeginInt=" + yearBeginInt + ", yearEndInt=" + yearEndInt + ", shortName=" + shortName + ", longName=" + longName + ", startDate=" + startDate + ", endDate=" + endDate + ", periods=" + periods + '}';
    }
}

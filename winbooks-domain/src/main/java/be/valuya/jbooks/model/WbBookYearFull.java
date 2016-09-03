package be.valuya.jbooks.model;

import java.time.LocalDate;

/**
 *
 * @author Yannick Majoros <yannick@valuya.be>
 */
public class WbBookYearFull {

    private int index;
    private int yearBeginInt;
    private int yearEndInt;
    private String shortName;
    private String longName;
    private LocalDate startDate;
    private LocalDate endDate;
    private int periods;

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
}

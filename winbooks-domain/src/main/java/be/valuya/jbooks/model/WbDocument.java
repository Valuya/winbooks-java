package be.valuya.jbooks.model;

import java.time.LocalDateTime;
import java.util.Objects;

public class WbDocument {

    private String name;
    private String dbCode;
    private WbPeriod wbPeriod;
    private int pageCount;
    private LocalDateTime creationTime;
    private LocalDateTime updatedTime;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDbCode() {
        return dbCode;
    }

    public void setDbCode(String dbCode) {
        this.dbCode = dbCode;
    }

    public WbPeriod getWbPeriod() {
        return wbPeriod;
    }

    public void setWbPeriod(WbPeriod wbPeriod) {
        this.wbPeriod = wbPeriod;
    }

    public int getPageCount() {
        return pageCount;
    }

    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }

    public LocalDateTime getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(LocalDateTime creationTime) {
        this.creationTime = creationTime;
    }

    public LocalDateTime getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(LocalDateTime updatedTime) {
        this.updatedTime = updatedTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        WbDocument that = (WbDocument) o;
        return name.equals(that.name) &&
                dbCode.equals(that.dbCode) &&
                wbPeriod.equals(that.wbPeriod);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, dbCode, wbPeriod);
    }
}

package be.valuya.jbooks.model;

import java.util.List;

/**
 *
 * @author Yannick Majoros <yannick@valuya.be>
 */
public class WbImport {

    private List<WbClientSupplier> wbClientSupplierList;
    private List<WbEntry> wbEntries;

    public List<WbClientSupplier> getWbClientSupplierList() {
        return wbClientSupplierList;
    }

    public void setWbClientSupplierList(List<WbClientSupplier> wbClientSupplierList) {
        this.wbClientSupplierList = wbClientSupplierList;
    }

    public List<WbEntry> getWbEntries() {
        return wbEntries;
    }

    public void setWbEntries(List<WbEntry> wbEntries) {
        this.wbEntries = wbEntries;
    }

}

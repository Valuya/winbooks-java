package be.valuya.jbooks.model;

import java.util.List;

/**
 *
 * @author Yannick Majoros <yannick@valuya.be>
 */
public class WbImport {

    private List<WbClientSupplier> wbClientSupplierList;
    private List<WbEntry> wbEntries;
    private List<WbGenericWarningResolution> genericWarningResolutions;
    private List<WbSpecificWarningResolution> specificWarningResolutions;

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

    public List<WbGenericWarningResolution> getGenericWarningResolutions() {
        return genericWarningResolutions;
    }

    public void setGenericWarningResolutions(List<WbGenericWarningResolution> genericWarningResolutions) {
        this.genericWarningResolutions = genericWarningResolutions;
    }

    public List<WbSpecificWarningResolution> getSpecificWarningResolutions() {
        return specificWarningResolutions;
    }

    public void setSpecificWarningResolutions(List<WbSpecificWarningResolution> specificWarningResolutions) {
        this.specificWarningResolutions = specificWarningResolutions;
    }
}

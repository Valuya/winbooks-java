package be.valuya.jbooks.model;

import java.util.List;

/**
 *
 * @author Yannick Majoros <yannick@valuya.be>
 */
public class WbImport {

    private List<WbClientSupplier> wbClientSupplierList;
    private List<WbInternalOperation> wbInternalOperationList;
    private List<WbGenericWarningResolution> genericWarningResolutions;
    private List<WbSpecificWarningResolution> specificWarningResolutions;

    public List<WbClientSupplier> getWbClientSupplierList() {
        return wbClientSupplierList;
    }

    public void setWbClientSupplierList(List<WbClientSupplier> wbClientSupplierList) {
        this.wbClientSupplierList = wbClientSupplierList;
    }

    public List<WbInternalOperation> getWbInternalOperationList() {
        return wbInternalOperationList;
    }

    public void setWbInternalOperationList(List<WbInternalOperation> wbInternalOperationList) {
        this.wbInternalOperationList = wbInternalOperationList;
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

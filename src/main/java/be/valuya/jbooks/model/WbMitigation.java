package be.valuya.jbooks.model;

import java.io.Serializable;
import java.util.List;

/**
 *
 * @author Yannick Majoros <yannick@valuya.be>
 */
public class WbMitigation implements Serializable {

    public List<WbSpecificMitigation> wbSpecificMitigations;
    public List<WbGenericMitigation> wbGenericMitigations;

    public WbMitigation() {
    }

    public WbMitigation(List<WbSpecificMitigation> wbSpecificMitigations, List<WbGenericMitigation> wbGenericMitigations) {
        this.wbSpecificMitigations = wbSpecificMitigations;
        this.wbGenericMitigations = wbGenericMitigations;
    }

    public List<WbSpecificMitigation> getWbSpecificMitigations() {
        return wbSpecificMitigations;
    }

    public void setWbSpecificMitigations(List<WbSpecificMitigation> wbSpecificMitigations) {
        this.wbSpecificMitigations = wbSpecificMitigations;
    }

    public List<WbGenericMitigation> getWbGenericMitigations() {
        return wbGenericMitigations;
    }

    public void setWbGenericMitigations(List<WbGenericMitigation> wbGenericMitigations) {
        this.wbGenericMitigations = wbGenericMitigations;
    }

}

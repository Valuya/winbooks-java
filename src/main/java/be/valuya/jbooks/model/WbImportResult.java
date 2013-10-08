package be.valuya.jbooks.model;

import be.valuya.jbooks.util.WbFatalError;
import java.util.List;

/**
 *
 * @author Yannick Majoros <yannick@valuya.be>
 */
public class WbImportResult {

    private List<WbWarning> wbWarnings;
    private List<WbFatalError> wbFatalErrors;

    public List<WbWarning> getWbWarnings() {
        return wbWarnings;
    }

    public void setWbWarnings(List<WbWarning> wbWarnings) {
        this.wbWarnings = wbWarnings;
    }

    public List<WbFatalError> getWbFatalErrors() {
        return wbFatalErrors;
    }

    public void setWbFatalErrors(List<WbFatalError> wbFatalErrors) {
        this.wbFatalErrors = wbFatalErrors;
    }
}

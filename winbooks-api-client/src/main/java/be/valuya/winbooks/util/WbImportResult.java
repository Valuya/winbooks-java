package be.valuya.winbooks.util;

import be.valuya.winbooks.util.WbWarning;
import be.valuya.winbooks.domain.error.WbFatalError;
import be.valuya.winbooks.domain.error.WinbooksError;
import java.util.List;

/**
 *
 * @author Yannick Majoros <yannick@valuya.be>
 */
public class WbImportResult {

    private WinbooksError winbooksError;
    private String errorMessage;
    private List<WbWarning> wbWarnings;
    private List<WbFatalError> wbFatalErrors;

    public WinbooksError getWinbooksError() {
        return winbooksError;
    }

    public void setWinbooksError(WinbooksError winbooksError) {
        this.winbooksError = winbooksError;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

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

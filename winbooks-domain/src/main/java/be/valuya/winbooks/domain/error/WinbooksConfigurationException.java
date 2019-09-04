package be.valuya.winbooks.domain.error;

/**
 * @author Yannick Majoros <yannick@valuya.be>
 */
public class WinbooksConfigurationException extends Exception {

    private WinbooksError winbooksError;

    public WinbooksConfigurationException(WinbooksError winbooksError) {
        this.winbooksError = winbooksError;
    }

    public WinbooksConfigurationException(WinbooksError winbooksError, String message) {
        super(message);
        this.winbooksError = winbooksError;
    }

    public WinbooksConfigurationException(WinbooksError winbooksError, String message, Throwable cause) {
        super(message, cause);
        this.winbooksError = winbooksError;
    }

    public WinbooksConfigurationException(WinbooksError winbooksError, Throwable cause) {
        super(cause);
        this.winbooksError = winbooksError;
    }

    public WinbooksError getWinbooksError() {
        return winbooksError;
    }
}

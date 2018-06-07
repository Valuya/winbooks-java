package be.valuya.winbooks.domain.error;

import be.valuya.winbooks.domain.error.WinbooksError;

/**
 *
 * @author Yannick Majoros <yannick@valuya.be>
 */
public class WinbooksException extends RuntimeException {

    public WinbooksError winbooksError;

    public WinbooksException(WinbooksError winbooksError) {
        this.winbooksError = winbooksError;
    }

    public WinbooksException(WinbooksError winbooksError, String message) {
        super(message);
        this.winbooksError = winbooksError;
    }

    public WinbooksException(WinbooksError winbooksError, String message, Throwable cause) {
        super(message, cause);
        this.winbooksError = winbooksError;
    }

    public WinbooksException(WinbooksError winbooksError, Throwable cause) {
        super(cause);
        this.winbooksError = winbooksError;
    }

    public WinbooksError getWinbooksError() {
        return winbooksError;
    }
}

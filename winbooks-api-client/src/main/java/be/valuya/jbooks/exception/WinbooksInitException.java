package be.valuya.jbooks.exception;

import be.valuya.winbooks.domain.error.WinbooksException;
import be.valuya.winbooks.domain.error.WinbooksError;


/**
 *
 * @author Yannick Majoros <yannick@valuya.be>
 */
public class WinbooksInitException extends WinbooksException {

    public WinbooksInitException(WinbooksError winbooksError) {
        super(winbooksError);
    }

    public WinbooksInitException(WinbooksError winbooksError, String message) {
        super(winbooksError, message);
    }

    public WinbooksInitException(WinbooksError winbooksError, String message, Throwable cause) {
        super(winbooksError, message, cause);
    }

    public WinbooksInitException(WinbooksError winbooksError, Throwable cause) {
        super(winbooksError, cause);
    }
}

package be.valuya.jbooks.exception;

/**
 *
 * @author Yannick Majoros <yannick@valuya.be>
 */
public class WinbooksExportException extends WinbooksException {

    public WinbooksExportException(WinbooksError winbooksError) {
        super(winbooksError);
    }

    public WinbooksExportException(WinbooksError winbooksError, String message) {
        super(winbooksError, message);
    }

    public WinbooksExportException(WinbooksError winbooksError, String message, Throwable cause) {
        super(winbooksError, message, cause);
    }

    public WinbooksExportException(WinbooksError winbooksError, Throwable cause) {
        super(winbooksError, cause);
    }
}

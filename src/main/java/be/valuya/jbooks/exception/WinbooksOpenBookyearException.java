package be.valuya.jbooks.exception;

/**
 *
 * @author Yannick Majoros <yannick@valuya.be>
 */
public class WinbooksOpenBookyearException extends WinbooksException {

    public WinbooksOpenBookyearException(WinbooksError winbooksError) {
        super(winbooksError);
    }

    public WinbooksOpenBookyearException(WinbooksError winbooksError, String message) {
        super(winbooksError, message);
    }

    public WinbooksOpenBookyearException(WinbooksError winbooksError, String message, Throwable cause) {
        super(winbooksError, message, cause);
    }

    public WinbooksOpenBookyearException(WinbooksError winbooksError, Throwable cause) {
        super(winbooksError, cause);
    }
}

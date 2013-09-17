package be.valuya.jbooks.exception;

/**
 *
 * @author Yannick Majoros <yannick@valuya.be>
 */
public enum WinbooksError {

    UNKNOWN_ERROR,
    USER_FILE_ERROR,
    NOT_INITIALIZED,
    NOT_LOGGED_IN,
    NO_DOSSIER,
    BAD_PASSWORD,
    DOSSIER_NOT_FOUND,
    DOSSIER_LOCKED,
    API_UNLICENSED,
    DEMO,
    NO_BOOKYEAR,
    BOOKYEAR_NOT_FOUND,
    CANNOT_OPEN_DOSSIER,
    UNTESTED,
    UNRESOLVED_WARNINGS,
    FATAL_ERRORS,
    RESOLUTION_UNSYCHRONIZED;
}

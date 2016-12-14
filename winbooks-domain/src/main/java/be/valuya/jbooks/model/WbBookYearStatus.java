package be.valuya.jbooks.model;

import java.util.Optional;
import java.util.stream.Stream;

public enum WbBookYearStatus {

    OPEN("Open"),
    CLOSED("Closed");

    private final String statusCode;

    WbBookYearStatus(String statusCode) {
        this.statusCode = statusCode;
    }

    public static Optional<WbBookYearStatus> fromValueStr(String statusCode) {
        WbBookYearStatus[] values = WbBookYearStatus.values();
        return Stream.of(values)
                .filter(wbBookYearStatus -> hasMatchingStatusCode(wbBookYearStatus, statusCode))
                .findAny();
    }

    private static boolean hasMatchingStatusCode(WbBookYearStatus wbBookYearStatus, String statusCode) {
        return wbBookYearStatus.statusCode.equals(statusCode);
    }
}

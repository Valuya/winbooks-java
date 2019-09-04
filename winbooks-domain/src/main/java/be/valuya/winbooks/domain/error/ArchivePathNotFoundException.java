package be.valuya.winbooks.domain.error;

import be.valuya.jbooks.model.WbBookYearFull;

import java.nio.file.Path;

public class ArchivePathNotFoundException extends Exception {

    private WbBookYearFull wbBookYearFull;

    public ArchivePathNotFoundException(WbBookYearFull wbBookYearFull) {
        super("No archive directory found for book year " + wbBookYearFull.toString() + " : "
                + wbBookYearFull.getArchivePathNameOptional().orElse("No archive path"));
        this.wbBookYearFull = wbBookYearFull;
    }

    public WbBookYearFull getWbBookYearFull() {
        return wbBookYearFull;
    }
}

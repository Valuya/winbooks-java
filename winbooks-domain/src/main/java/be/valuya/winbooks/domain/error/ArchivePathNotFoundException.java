package be.valuya.winbooks.domain.error;

import be.valuya.jbooks.model.WbBookYearFull;

import java.nio.file.Path;

public class ArchivePathNotFoundException extends Exception {

    private Path baseFolderPath;
    private WbBookYearFull wbBookYearFull;

    public ArchivePathNotFoundException(Path baseFolderPath, WbBookYearFull wbBookYearFull) {
        super("No archive directory found for base path " + baseFolderPath.toString() + " and book year " + wbBookYearFull.toString());
        this.baseFolderPath = baseFolderPath;
        this.wbBookYearFull = wbBookYearFull;
    }

    public Path getBaseFolderPath() {
        return baseFolderPath;
    }

    public WbBookYearFull getWbBookYearFull() {
        return wbBookYearFull;
    }
}

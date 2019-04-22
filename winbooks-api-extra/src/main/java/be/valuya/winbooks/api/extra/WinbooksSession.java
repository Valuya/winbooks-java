package be.valuya.winbooks.api.extra;

import be.valuya.jbooks.model.WbBookYearFull;
import be.valuya.winbooks.api.extra.config.WinbooksFileConfiguration;

import java.util.List;

public class WinbooksSession {

    private final WinbooksFileConfiguration winbooksFileConfiguration;
    private final List<WbBookYearFull> bookYears;

    public WinbooksSession(WinbooksFileConfiguration winbooksFileConfiguration, List<WbBookYearFull> bookYears) {
        this.winbooksFileConfiguration = winbooksFileConfiguration;
        this.bookYears = bookYears;
    }

    public WinbooksFileConfiguration getWinbooksFileConfiguration() {
        return winbooksFileConfiguration;
    }

    public List<WbBookYearFull> getBookYears() {
        return bookYears;
    }
}

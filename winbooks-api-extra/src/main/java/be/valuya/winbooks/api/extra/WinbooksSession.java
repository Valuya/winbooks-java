package be.valuya.winbooks.api.extra;

import be.valuya.jbooks.model.WbBookYearFull;
import be.valuya.accountingtroll.TrollSession;

import java.util.List;

public class WinbooksSession implements TrollSession {

    public   final static String SESSION_TYPE = "be.valuya.winbooks";

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

    @Override
    public String getSessionType() {
        return SESSION_TYPE;
    }
}

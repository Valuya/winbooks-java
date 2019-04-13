package be.valuya.winbooks.api.extra;

import be.valuya.accountingtroll.Session;
import be.valuya.accountingtroll.cache.AccountingCache;
import be.valuya.accountingtroll.cache.MemorySessionCache;
import be.valuya.jbooks.model.WbBookYearFull;

import java.util.List;

public class WinbooksSession implements Session {

    public final static String SESSION_TYPE = "be.valuya.winbooks";

    private final WinbooksFileConfiguration winbooksFileConfiguration;
    private final List<WbBookYearFull> bookYears;

    private AccountingCache accountingCache;

    public WinbooksSession(WinbooksFileConfiguration winbooksFileConfiguration, List<WbBookYearFull> bookYears) {
        this.winbooksFileConfiguration = winbooksFileConfiguration;
        this.bookYears = bookYears;

        this.accountingCache = new MemorySessionCache(this);
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

    @Override
    public AccountingCache getCache() {
        return this.accountingCache;
    }
}

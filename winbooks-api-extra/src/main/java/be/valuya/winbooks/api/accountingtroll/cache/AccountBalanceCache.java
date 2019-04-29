package be.valuya.winbooks.api.accountingtroll.cache;

import be.valuya.accountingtroll.domain.ATAccount;
import be.valuya.accountingtroll.domain.ATAccountingEntry;
import be.valuya.accountingtroll.domain.ATBookPeriod;
import be.valuya.accountingtroll.domain.ATBookYear;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.stream.Collectors;

public class AccountBalanceCache {

    private Map<ATBookYear, BookYearAccountBalanceCache> bookYearBalances = new ConcurrentSkipListMap<>();
    private final AccountingManagerCache accountingManagerCache;

    public AccountBalanceCache(AccountingManagerCache accountingManagerCache) {
        this.accountingManagerCache = accountingManagerCache;
    }

    public void resetAccountBalance(ATAccountingEntry accountingEntry) {
        ATAccount account = accountingEntry.getAccount();
        ATBookPeriod bookPeriod = accountingEntry.getBookPeriod();
        ATBookYear bookYear = bookPeriod.getBookYear();
        BookYearAccountBalanceCache bookYearBalanceCache = getBookYearBalanceCache(bookYear);
        bookYearBalanceCache.resetAccountBalance(account, accountingEntry);
    }

    public void appendToAccountBalance(ATAccountingEntry accountingEntry) {
        ATAccount account = accountingEntry.getAccount();
        ATBookPeriod bookPeriod = accountingEntry.getBookPeriod();
        ATBookYear bookYear = bookPeriod.getBookYear();
        BookYearAccountBalanceCache bookYearBalanceCache = getBookYearBalanceCache(bookYear);
        bookYearBalanceCache.appendAccountBalance(account, accountingEntry);
    }

    public List<ATAccount> findAccountWithoutBalanceInBookYear(ATBookYear bookYear) {
        BookYearAccountBalanceCache bookYearAccountBalanceCache = bookYearBalances.get(bookYear);
        return this.findAccountsWithoutBalanceInCache(bookYearAccountBalanceCache);
    }

    public Optional<ATBookYear> getLastBookYear() {
        return findLastBookYearCache()
                .map(BookYearAccountBalanceCache::getBookYear);
    }

    public AccountBalance getAccountBalance(ATAccount account, ATBookYear bookYear) {
        BookYearAccountBalanceCache bookYearBalanceCache = getBookYearBalanceCache(bookYear);
        return bookYearBalanceCache.getBalance(account);
    }

    private BookYearAccountBalanceCache getBookYearBalanceCache(ATBookYear bookYear) {
        BookYearAccountBalanceCache bookYearAccountBalanceCache = bookYearBalances.computeIfAbsent(bookYear, this::createBookYearBalance);
        return bookYearAccountBalanceCache;
    }

    private BookYearAccountBalanceCache createBookYearBalance(ATBookYear bookYear) {
        Optional<BookYearAccountBalanceCache> lastCacheOptional = findLastBookYearCache(bookYear);

        BookYearAccountBalanceCache newCache = new BookYearAccountBalanceCache(bookYear, accountingManagerCache, lastCacheOptional);
        bookYearBalances.put(bookYear, newCache);
        return newCache;
    }

    private Optional<BookYearAccountBalanceCache> findLastBookYearCache() {
        return bookYearBalances.keySet()
                .stream()
                .sorted(Comparator.reverseOrder())
                .map(bookYearBalances::get)
                .findFirst();
    }


    private Optional<BookYearAccountBalanceCache> findLastBookYearCache(ATBookYear maxBookyearEclusive) {
        return bookYearBalances.keySet()
                .stream()
                .sorted(Comparator.reverseOrder())
                .filter(year -> year.compareTo(maxBookyearEclusive) < 0)
                .map(bookYearBalances::get)
                .findFirst();
    }

    private List<ATAccount> findAccountsWithoutBalanceInCache(BookYearAccountBalanceCache bookYearcache) {
        ATBookYear lastBookyear = bookYearcache.getBookYear();
        List<ATAccount> accountsWithBalanceForLastBookyear = bookYearcache.getAccountsWithBalance();

        return bookYearBalances.keySet().stream()
                .filter(year -> !year.equals(lastBookyear))
                .map(bookYearBalances::get)
                .map(BookYearAccountBalanceCache::getAccountsWithBalance)
                .map(accounts -> this.listDifference(accounts, accountsWithBalanceForLastBookyear))
                .flatMap(List::stream)
                .distinct()
                .collect(Collectors.toList());
    }

    private List<ATAccount> listDifference(List<ATAccount> accounts, List<ATAccount> accountsWithBalanceForLastBookyear) {
        return accounts.stream()
                .filter(account -> !accountsWithBalanceForLastBookyear.contains(account))
                .collect(Collectors.toList());
    }

}

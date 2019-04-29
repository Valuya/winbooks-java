package be.valuya.winbooks.api.accountingtroll.cache;

import be.valuya.accountingtroll.domain.ATAccount;
import be.valuya.accountingtroll.domain.ATAccountingEntry;
import be.valuya.accountingtroll.domain.ATBookPeriod;
import be.valuya.accountingtroll.domain.ATBookYear;
import be.valuya.accountingtroll.domain.ATPeriodType;
import be.valuya.winbooks.domain.error.WinbooksError;
import be.valuya.winbooks.domain.error.WinbooksException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class BookYearAccountBalanceCache {

    private final static BigDecimal ZERO = BigDecimal.ZERO.setScale(3, RoundingMode.UNNECESSARY);

    private ATBookYear bookYear;
    private ATBookPeriod openingPeriod;
    private Map<String, AccountBalance> accountBalanceCache = new HashMap<>();
    private final AccountingManagerCache accountingManagerCache;

    public BookYearAccountBalanceCache(ATBookYear bookYear, AccountingManagerCache accountingManagerCache, Optional<BookYearAccountBalanceCache> lastCache) {
        this.bookYear = bookYear;
        this.accountingManagerCache = accountingManagerCache;
        this.openingPeriod = this.findOpeningPeriod();

        lastCache.ifPresent(this::copyBalances);
    }


    void resetAccountBalance(ATAccount account, ATAccountingEntry accountingEntry) {
        String accountCode = account.getCode();
        BigDecimal entryAmount = accountingEntry.getAmount();
        ATBookPeriod bookPeriod = accountingEntry.getBookPeriod();
        AccountBalance curBalance = accountBalanceCache.computeIfAbsent(accountCode, code -> this.createEmptyBalance(account, bookPeriod));
        if (isSamePeriod(curBalance, accountingEntry)) {
            this.appendBalanceAmount(curBalance, entryAmount);
        } else {
            this.setBalanceAmount(curBalance, entryAmount);
        }
    }


    void appendAccountBalance(ATAccount account, ATAccountingEntry accountingEntry) {
        String accountCode = account.getCode();
        BigDecimal entryAmount = accountingEntry.getAmount();
        LocalDate date = accountingEntry.getDate();
        ATBookPeriod bookPeriod = accountingEntry.getBookPeriod();
        AccountBalance curBalance = accountBalanceCache.computeIfAbsent(accountCode, code -> this.createEmptyBalance(account, bookPeriod));

        curBalance.setDate(date);
        curBalance.setPeriod(bookPeriod);
        appendBalanceAmount(curBalance, entryAmount);
    }

    AccountBalance getBalance(ATAccount account) {
        String accountCode = account.getCode();

        AccountBalance balance = accountBalanceCache.computeIfAbsent(accountCode, code -> this.createEmptyBalance(account, openingPeriod));
        return balance;
    }

    List<ATAccount> getAccountsWithBalance() {
        return accountBalanceCache.values()
                .stream()
                .map(AccountBalance::getAccount)
                .collect(Collectors.toList());
    }

    ATBookYear getBookYear() {
        return this.bookYear;
    }

    private void setBalanceAmount(AccountBalance balance, BigDecimal entryAmount) {
        balance.setBalance(entryAmount);
    }

    private void appendBalanceAmount(AccountBalance balance, BigDecimal amountToAdd) {
        BigDecimal newAmount = balance.getBalance().add(amountToAdd);
        balance.setBalance(newAmount);
    }

    private boolean isSamePeriod(AccountBalance curBalance, ATAccountingEntry accountingEntry) {
        ATBookPeriod periodA = curBalance.getPeriod();
        ATBookPeriod periodB = accountingEntry.getBookPeriod();
        return periodA.equals(periodB);
    }

    private AccountBalance createEmptyBalance(ATAccount account, ATBookPeriod bookPeriod) {
        AccountBalance accountBalance = new AccountBalance();
        accountBalance.setDate(bookPeriod.getStartDate());
        accountBalance.setPeriod(bookPeriod);
        accountBalance.setAccount(account);
        accountBalance.setBalance(ZERO);
        return accountBalance;
    }

    private Map<String, AccountBalance> getAccountBalanceCache() {
        return accountBalanceCache;
    }

    private void copyBalances(BookYearAccountBalanceCache previousCache) {
        previousCache.getAccountBalanceCache().values()
                .stream()
                .filter(this::isBalanceCopyRequired)
                .forEach(lastBalance -> this.copyPreviousBalance(lastBalance, openingPeriod));
    }

    private void copyPreviousBalance(AccountBalance previousBalance, ATBookPeriod newPeriod) {
        ATAccount account = previousBalance.getAccount();
        BigDecimal balance = previousBalance.getBalance();
        String code = account.getCode();
        LocalDate startDate = newPeriod.getStartDate();

        AccountBalance newBalance = new AccountBalance();
        newBalance.setBalance(balance);
        newBalance.setAccount(account);
        newBalance.setDate(startDate);
        newBalance.setPeriod(newPeriod);
        accountBalanceCache.put(code, newBalance);
    }

    private boolean isBalanceCopyRequired(AccountBalance accountBalance) {
        ATAccount account = accountBalance.getAccount();
        boolean yearlyBalanceReset = account.isYearlyBalanceReset();
        return !yearlyBalanceReset;
    }


    private ATBookPeriod findOpeningPeriod() {
        return accountingManagerCache.streamPeriods()
                .filter(this::isOpeningPeriod)
                .findAny()
                .orElseThrow(() -> new WinbooksException(WinbooksError.UNKNOWN_ERROR, "Could not find opening period for book year " + bookYear));
    }

    private boolean isOpeningPeriod(ATBookPeriod period) {
        ATBookYear periodBookYear = period.getBookYear();
        ATPeriodType periodType = period.getPeriodType();
        return periodType == ATPeriodType.OPENING && periodBookYear.equals(this.bookYear);
    }

}

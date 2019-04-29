package be.valuya.winbooks.api.accountingtroll.cache;

import be.valuya.accountingtroll.domain.ATAccount;
import be.valuya.accountingtroll.domain.ATBookPeriod;

import java.math.BigDecimal;
import java.time.LocalDate;

public class AccountBalance {

    private ATAccount account;
    private ATBookPeriod period;
    private LocalDate date;
    private BigDecimal balance;

    public ATAccount getAccount() {
        return account;
    }

    public void setAccount(ATAccount account) {
        this.account = account;
    }

    public ATBookPeriod getPeriod() {
        return period;
    }

    public void setPeriod(ATBookPeriod period) {
        this.period = period;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    @Override
    public String toString() {
        return "AccountBalance{" +
                "account=" + account +
                ", period=" + period +
                ", date=" + date +
                ", balance=" + balance +
                '}';
    }
}

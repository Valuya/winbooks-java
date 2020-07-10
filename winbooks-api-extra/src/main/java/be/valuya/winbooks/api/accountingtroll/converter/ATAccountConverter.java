package be.valuya.winbooks.api.accountingtroll.converter;

import be.valuya.accountingtroll.domain.ATAccount;
import be.valuya.jbooks.model.WbAccount;

import java.util.Optional;

public class ATAccountConverter {


    public ATAccount convertToTrollAcccount(WbAccount wbAccount, int acountNumberLength) {
        String accountNumber = wbAccount.getAccountNumber();
        String currency = wbAccount.getCurrency();
        String name = wbAccount.getName11();
        boolean analyt = wbAccount.isAnalyt();
        boolean yearlyBalanceReset = this.isYearResetAccount(accountNumber);
        boolean titleAccount = accountNumber.length() < acountNumberLength;

        ATAccount account = new ATAccount();
        account.setCode(accountNumber);
        account.setName(name);
        account.setAnalytics(analyt);
        account.setYearlyBalanceReset(yearlyBalanceReset);
        account.setCurrency(currency);
        account.setTitle(titleAccount);
        return account;
    }


    private boolean isYearResetAccount(String accountCode) {
        return accountCode.startsWith("6") || accountCode.startsWith("7");
    }

}

package be.valuya.winbooks.api.accountingtroll;

import be.valuya.accountingtroll.TrollAccount;
import be.valuya.jbooks.model.WbAccount;

public class WinbooksTrollAccount implements TrollAccount {

    private WbAccount wbAccount;

    public WinbooksTrollAccount(WbAccount wbAccount) {
        this.wbAccount = wbAccount;
    }

    @Override
    public String getName() {
        return wbAccount.getName11();
    }

    @Override
    public String getCode() {
        return wbAccount.getAccountNumber();
    }

    @Override
    public String getCurrency() {
        return wbAccount.getCurrency();
    }

}

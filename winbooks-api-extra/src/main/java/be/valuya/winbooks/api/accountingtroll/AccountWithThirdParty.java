package be.valuya.winbooks.api.accountingtroll;

import be.valuya.accountingtroll.domain.ATAccount;
import be.valuya.accountingtroll.domain.ATThirdParty;

import java.util.Objects;
import java.util.Optional;

public class AccountWithThirdParty  {
    private ATAccount account;
    private Optional<ATThirdParty> thirdPartyOptional = Optional.empty();

    public AccountWithThirdParty(ATAccount atAccount, Optional<ATThirdParty> thirdPartyOptional) {
        this.account = atAccount;
        this.thirdPartyOptional = thirdPartyOptional;
    }

    public AccountWithThirdParty() {
    }

    public ATAccount getAccount() {
        return account;
    }

    public void setAccount(ATAccount account) {
        this.account = account;
    }

    public Optional<ATThirdParty> getThirdPartyOptional() {
        return thirdPartyOptional;
    }

    public void setThirdPartyOptional(Optional<ATThirdParty> thirdPartyOptional) {
        this.thirdPartyOptional = thirdPartyOptional;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AccountWithThirdParty that = (AccountWithThirdParty) o;
        return Objects.equals(account, that.account) &&
                Objects.equals(thirdPartyOptional, that.thirdPartyOptional);
    }

    @Override
    public int hashCode() {
        return Objects.hash(account, thirdPartyOptional);
    }

}

package be.valuya.winbooks.api.accountingtroll;

import be.valuya.accountingtroll.ThirdPartyType;
import be.valuya.accountingtroll.TrollThirdParty;
import be.valuya.jbooks.model.WbClientSupplier;
import be.valuya.jbooks.model.WbClientSupplierType;

import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class WinbooksTrollThirdParty implements TrollThirdParty {
    WbClientSupplier wbClientSupplier;

    public WinbooksTrollThirdParty(WbClientSupplier wbClientSupplier) {
        this.wbClientSupplier = wbClientSupplier;
    }

    @Override
    public String getNumber() {
        return wbClientSupplier.getNumber();
    }

    @Override
    public ThirdPartyType getType() {
        return wbClientSupplier.getWbClientSupplierType() == WbClientSupplierType.CLIENT ?
                ThirdPartyType.CLIENT : ThirdPartyType.SUPPLIER;
    }

    @Override
    public String getFullName() {
        return Stream.of(wbClientSupplier.getCivName1(), wbClientSupplier.getName1())
                .filter(Objects::nonNull)
                .collect(Collectors.joining(" "));
    }

    @Override
    public String getAddress() {
        return Stream.of(wbClientSupplier.getAddress1(), wbClientSupplier.getAddress2())
                .filter(Objects::nonNull)
                .collect(Collectors.joining(" "));
    }

    @Override
    public String getZip() {
        return wbClientSupplier.getZipCode();
    }

    @Override
    public String getCity() {
        return wbClientSupplier.getCity();
    }

    @Override
    public String getVatCode() {
        return wbClientSupplier.getVatCode();
    }

    @Override
    public String getCountryCode() {
        return wbClientSupplier.getCountryCode();
    }

    @Override
    public String getVatNumber() {
        return wbClientSupplier.getVatNumber();
    }

    @Override
    public Optional<String> getPhoneNumber() {
        String telNumber = wbClientSupplier.getTelNumber();
        return Optional.ofNullable(telNumber);
    }

    @Override
    public Optional<String> getBankAccountNumber() {
        String bankAccount = wbClientSupplier.getBankAccount();
        return Optional.ofNullable(bankAccount);
    }

    @Override
    public Optional<String> getLanguage() {
        String lang = wbClientSupplier.getLang();
        return Optional.ofNullable(lang);
    }
}

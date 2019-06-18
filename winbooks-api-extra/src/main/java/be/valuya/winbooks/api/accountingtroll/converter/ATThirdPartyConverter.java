package be.valuya.winbooks.api.accountingtroll.converter;

import be.valuya.accountingtroll.domain.ATThirdParty;
import be.valuya.accountingtroll.domain.ATThirdPartyType;
import be.valuya.jbooks.model.WbClientSupplier;
import be.valuya.jbooks.model.WbClientSupplierType;

import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ATThirdPartyConverter {


    public ATThirdParty convertToTrollThirdParty(WbClientSupplier wbSupplier) {
        String number = wbSupplier.getNumber();
        WbClientSupplierType wbClientSupplierType = wbSupplier.getWbClientSupplierType();
        ATThirdPartyType thirdPartyType = wbClientSupplierType == WbClientSupplierType.SUPPLIER ?
                ATThirdPartyType.SUPPLIER : ATThirdPartyType.CLIENT;
        String id = ATThirdPartyIdFactory.getId(wbClientSupplierType, number);

        String fullName = Stream.of(wbSupplier.getCivName1(), wbSupplier.getName1())
                .filter(Objects::nonNull)
                .collect(Collectors.joining(" "));
        String address = Stream.of(wbSupplier.getAddress1(), wbSupplier.getAddress2())
                .filter(Objects::nonNull)
                .collect(Collectors.joining(" "));
        String zipCode = wbSupplier.getZipCode();
        String city = wbSupplier.getCity();
        String countryCode = wbSupplier.getCountryCode();
        String vatNumber = wbSupplier.getVatNumber();

        String phoneNumber = wbSupplier.getTelNumber();
        String bankAccount = wbSupplier.getBankAccount();
        String lang = wbSupplier.getLang();

        ATThirdParty thirdParty = new ATThirdParty();
        thirdParty.setId(id);
        thirdParty.setType(thirdPartyType);
        thirdParty.setFullName(fullName);
        thirdParty.setAddress(address);
        thirdParty.setZipCode(zipCode);
        thirdParty.setCity(city);
        thirdParty.setCountryCode(countryCode);
        thirdParty.setVatNumber(vatNumber);
        thirdParty.setPhoneNumber(phoneNumber);
        thirdParty.setBankAccountNumber(bankAccount);
        thirdParty.setLanguage(lang);
        return thirdParty;
    }

}

package be.valuya.winbooks.api.accountingtroll.converter;

import be.valuya.jbooks.model.WbClientSupplierType;

public class ATThirdPartyIdFactory {

    public static String getId(WbClientSupplierType clientSupplierType, String name) {
        String id = clientSupplierType.getValue() + name;
        return id;
    }

    public static String getThirdPartyCodeFromId(String id) {
        return id.substring(1);
    }
}

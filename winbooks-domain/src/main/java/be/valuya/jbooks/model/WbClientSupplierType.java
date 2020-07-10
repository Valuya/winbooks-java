package be.valuya.jbooks.model;

import java.util.stream.Stream;

/**
 * @author Yannick Majoros <yannick@valuya.be>
 */
public enum WbClientSupplierType implements WbValue {

    CLIENT("1"),
    SUPPLIER("2");
    private String value;

    WbClientSupplierType(String value) {
        this.value = value;
    }

    @Override
    public String getValue() {
        return value;
    }

    public static WbClientSupplierType fromCode(String code) {
        return Stream.of(values())
                .filter(wbClientSupplierType -> wbClientSupplierType.hasCode(code))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }

    public boolean hasCode(String code) {
        return value.equals(code);
    }
}

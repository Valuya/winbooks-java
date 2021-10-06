package be.valuya.jbooks.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

/**
 * From what appears in the dbf:<br/>
 * 0: fully deducible<br/>
 * 1: not deducible<br/>
 * 2: Partially deducible<br/>
 */
public enum WbVatDeducibility implements WbValue {

    DEDUCIBLE("0"),
    NO_DEDUCIBLE("1"),
    PARTIALLY_DEDUCIBLE("2");

    private final List<String> allCodes;
    private String value;

    WbVatDeducibility(String value, String... otherCodeArray) {
        this.value = value;
        List<String> otherCodes = Arrays.asList(otherCodeArray);
        List<String> allCodes = new ArrayList<>();
        allCodes.add(value);
        allCodes.addAll(otherCodes);
        this.allCodes = allCodes;
    }

    @Override
    public String getValue() {
        return value;
    }

    public static WbVatDeducibility fromCode(String code) {
        return Stream.of(values())
                .filter(wbVatCat -> wbVatCat.hasCode(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid vat deducibilty: " + code));
    }

    public boolean hasCode(String code) {
        return allCodes.contains(code);
    }
}

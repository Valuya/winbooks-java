package be.valuya.jbooks.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

/**
 * Winbooks doc:<br/>
 * 1 : Liable<br/>
 * 2: Zero-rated<br/>
 * 3: Non liable<br/>
 * 0: Indeterminate<br/>
 * WinBooks only manages VAT if VATCAT = ‘1’ (liable)
 *
 * @author Yannick Majoros
 * <yannick@valuya.be>
 */
public enum WbVatCat implements WbValue {

    // I'm sorry to have to make a difference with INDETERMINATE, but it seems Winbooks is really using that value
    UNKNOWN("0", "-"),
    INDETERMINATE("1"),
    ZERO_RATED("2"),
    NON_LIABLE("3");

    private final List<String> allCodes;
    private String value;

    WbVatCat(String value, String... otherCodeArray) {
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

    public static WbVatCat fromCode(String code) {
        return Stream.of(values())
                .filter(wbVatCat -> wbVatCat.hasCode(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid vat cat: " + code));
    }

    public boolean hasCode(String code) {
        return allCodes.contains(code);
    }
}

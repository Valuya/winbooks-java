package be.valuya.jbooks.model;

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
    UNKNOWN("0"),
    INDETERMINATE("1"),
    ZERO_RATED("2"),
    NON_LIABLE("3");

    private String value;

    WbVatCat(String value) {
        this.value = value;
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
        return this.value.equals(code);
    }
}

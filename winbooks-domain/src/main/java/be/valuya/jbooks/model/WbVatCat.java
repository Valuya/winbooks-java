package be.valuya.jbooks.model;

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

    INDETERMINATE("1"),
    ZERO_RATED("2"),
    NON_LIABLE("3");
    private String value;

    private WbVatCat(String value) {
        this.value = value;
    }

    @Override
    public String getValue() {
        return value;
    }
}

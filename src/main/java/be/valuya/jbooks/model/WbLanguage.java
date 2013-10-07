package be.valuya.jbooks.model;

/**
 *
 * @author Yannick Majoros <yannick@valuya.be>
 */
public enum WbLanguage implements WbValue {

    FRENCH("F"),
    DUTCH("N");
    private String value;

    private WbLanguage(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}

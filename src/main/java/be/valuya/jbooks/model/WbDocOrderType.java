package be.valuya.jbooks.model;

/**
 *
 * @author Yannick Majoros <yannick@valuya.be>
 */
public enum WbDocOrderType implements WbValue {

    NUMBER(null),
    VAT("VAT"),
    DEBIT_CENTRAL("998"),
    CREDIT_CENTRAL("999");
    //
    private String value;

    private WbDocOrderType(String value) {
        this.value = value;
    }

    @Override
    public String getValue() {
        return value;
    }
}

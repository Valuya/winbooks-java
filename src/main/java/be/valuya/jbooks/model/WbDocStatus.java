package be.valuya.jbooks.model;

/**
 *
 * @author Yannick Majoros <yannick@valuya.be>
 */
public enum WbDocStatus implements WbValue {

    NO_IMPUT_FOR_REMINDER(1);
    private int value;

    private WbDocStatus(int value) {
        this.value = value;
    }

    @Override
    public String getValue() {
        return Integer.toString(value);
    }
}

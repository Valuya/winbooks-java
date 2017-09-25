package be.valuya.jbooks.model;

import java.util.Optional;
import java.util.stream.Stream;

/**
 *
 * @author Yannick Majoros <yannick@valuya.be>
 */
public enum WbDocOrderType implements WbValue {

    NUMBER(null),
    VAT("VAT"),
    DEBIT_CENTRAL("998"),
    CREDIT_CENTRAL("999"),
    A("A.*"),
    BALANCE(null);
    //
    private String value;

    WbDocOrderType(String value) {
        this.value = value;
    }

    @Override
    public String getValue() {
        return value;
    }

    public static Optional<WbDocOrderType> fromStringOptional(String docOrderStr) {
        // find WbDocOrderType that matches that constant value
        return Stream.of(WbDocOrderType.values())
                .filter(wbDocOrderType -> wbDocOrderType.value != null && docOrderStr.matches(wbDocOrderType.value))
                .findAny()
                .map(Optional::of)
                // none found, so it should be a number
                .orElseGet(() -> getDocOrderTypeNumberOptional(docOrderStr));
    }

    private static Optional<WbDocOrderType> getDocOrderTypeNumberOptional(String docOrderStr) {
        if (docOrderStr.matches("^\\d+$")) {
            return Optional.of(NUMBER);
        }
//        throw new IllegalArgumentException("Unreadable docOrder: " + docOrderStr);
        return Optional.empty();
    }

}

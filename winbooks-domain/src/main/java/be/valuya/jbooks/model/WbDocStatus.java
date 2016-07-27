package be.valuya.jbooks.model;

import java.util.stream.Stream;

/**
 *
 * @author Yannick Majoros <yannick@valuya.be>
 */
public enum WbDocStatus implements WbValue {

    NO_IMPUT_FOR_REMINDER(1),
    UNKNOWN(null);

    private final Integer code;

    private WbDocStatus(Integer code) {
        this.code = code;
    }

    @Override
    public String getValue() {
        return Integer.toString(code);
    }

    public Integer getCode() {
        return code;
    }

    public static WbDocStatus fromCode(Integer code) {
        if (code == null) {
            return UNKNOWN;
        }
        return Stream.of(WbDocStatus.values())
                .filter(wbDocStatus -> wbDocStatus.code != null && wbDocStatus.code.equals(code))
                .findAny()
                .orElseThrow(IllegalArgumentException::new);
    }
}

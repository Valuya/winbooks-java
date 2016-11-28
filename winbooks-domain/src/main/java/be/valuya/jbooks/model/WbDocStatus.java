package be.valuya.jbooks.model;

import java.util.stream.Stream;

/**
 *
 * @author Yannick Majoros <yannick@valuya.be>
 */
public enum WbDocStatus implements WbValue {

    NONE(0),
    NO_IMPUT_FOR_REMINDER(1),
    UNKNOWN_2(2),
    UNKNOWN(null);

    private final Integer code;

    WbDocStatus(Integer code) {
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
                .orElseThrow(() -> new IllegalArgumentException("Unknown WbDocStatus code:" + code));
    }
}

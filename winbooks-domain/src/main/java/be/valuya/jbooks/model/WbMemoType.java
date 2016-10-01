package be.valuya.jbooks.model;

import java.util.stream.Stream;

/**
 *
 * @author Yannick Majoros <yannick@valuya.be>
 */
public enum WbMemoType {

    MEMO(1),
    URGENT_MEMO(2);

    private final int code;

    WbMemoType(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static WbMemoType fromCode(int code) {
        return Stream.of(values())
                .filter(wbMemoType -> wbMemoType.getCode() == code)
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}

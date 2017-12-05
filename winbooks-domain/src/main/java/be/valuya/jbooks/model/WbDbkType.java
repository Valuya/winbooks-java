package be.valuya.jbooks.model;

import java.util.stream.Stream;

/**
 * 0 = Achat<br/>
 * 1 = Note de crédit sur achat<br/>
 * 2 = Vente<br/>
 * 3 = Note de crédit sur vente<br/>
 * 4 = Financier<br/>
 * 5 = Opération diverse<br/>
 *
 * @author Yannick Majoros <yannick@valuya.be>
 */
public enum WbDbkType implements WbValue {

    PURCHASE(0),
    CREDIT_NOTE_PURCHASE(1),
    SALE(2),
    CREDIT_NOTE_SALE(3),
    FINANCE(4),
    MISC(5);

    private final int code;

    private WbDbkType(int code) {
        this.code = code;
    }

    @Override
    public String getValue() {
        return Integer.toString(code);
    }

    public int getCode() {
        return code;
    }

    public static WbDbkType fromCode(int code) {
        return Stream.of(WbDbkType.values())
                .filter(wbDbkType -> wbDbkType.code == code)
                .findAny()
                .orElseThrow(IllegalArgumentException::new);
    }
}

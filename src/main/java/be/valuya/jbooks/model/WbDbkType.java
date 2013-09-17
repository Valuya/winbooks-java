package be.valuya.jbooks.model;

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
public enum WbDbkType implements WbValue<Integer> {

    PURCHASE(0),
    CREDIT_NOTE_PURCHASE(1),
    SALE(2),
    CREDIT_NOTE_SALE(3),
    FINANCE(4),
    MISC(5);
    private int value;

    private WbDbkType(int value) {
        this.value = value;
    }

    @Override
    public Integer getValue() {
        return value;
    }
}

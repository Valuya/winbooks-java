package be.valuya.jbooks.model;

import java.util.Optional;
import java.util.stream.Stream;

/**
 * 0= Journal d'opération journalières
 * 1= Journal d'ouverture
 * 2= Journal de cloture
 * 3= Journal de virements émis
 * 4= Journal des amortissements
 * 5= Journal de réévaluations financières
 * 6= Journal de simulation
 * 7= Journal d'écart de lettrage
 *
 * @author Yannick Majoros <yannick@valuya.be>
 */
public enum WbDbkType2 implements WbValue {

    UNKNOWN(null),
    DAILY_OPERATIONS(0),
    OPENING(1),
    CLOSING(2),
    SENT_TRANSFERTS(3),
    AMORTISSEMENTS(4),
    FINANCIAL_REEVALUATION(5),
    SIMULATION(6),
    ECART_LETTRAGE(7);

    private final Optional<Integer> codeOptional;

    WbDbkType2(Integer codeNullable) {
        this.codeOptional = Optional.ofNullable(codeNullable);
    }

    public static WbDbkType2 fromCode(Integer codeNullable) {
        return Stream.of(WbDbkType2.values())
                .filter(wbDbkType -> WbDbkType2.isSameCode(wbDbkType, codeNullable))
                .findAny()
                .orElse(WbDbkType2.UNKNOWN);
    }


    private static boolean isSameCode(WbDbkType2 wbDbkType, Integer codeNullable) {
        Optional<Integer> checkCodeOptional = Optional.ofNullable(codeNullable);
        return wbDbkType.getCodeOptional().equals(checkCodeOptional);
    }

    public Optional<Integer> getCodeOptional() {
        return codeOptional;
    }

    @Override
    public String getValue() {
        return String.valueOf(codeOptional.orElse(-1));
    }
}

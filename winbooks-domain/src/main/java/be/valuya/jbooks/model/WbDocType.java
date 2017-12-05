package be.valuya.jbooks.model;

import java.util.stream.Stream;

/**
 * Winbooks documentation:<br/>
 * 1 = imputation sur compte client <br/>
 * 2 = imputation sur compte fournisseur <br/>
 * 3 = imputation sur compte général <br/>
 * 4 = Base d’un code Tva 0%
 *
 * Une facture de vente client sera toujours composée de : <br/>
 * 1 seul record de doctype = 1 ( imputation sur le compte individuel client et
 * son centralisateur )<br/>
 * 1 ou plusieurs records de doctype = 3 ( imputations comptables et Tva )<br/>
 * 1 ou plusieurs records de doctype = 4 si un ou plusieurs codes tva 0% ont été
 * utilisés
 *
 * Une facture d’achat sera toujours composée de :<br/>
 * 1 seul record de doctype = 2 ( imputation sur le compte fournisseur et son
 * centralisateur )<br/>
 * 1 ou plusieurs records de doctype = 3 ( imputations comptables et Tva )<br/>
 * 1 ou plusireurs records de doctype = 4 si un ou plusieurs codes tva 0% ont
 * été utilisés
 *
 * Une opération diverse ou un financier sera composé de records :<br/>
 * De doctype = 1 pour une imputation sur un compte individuel client<br/>
 * De doctype = 2 pour une imputationsur un compte individuel fournisseur<br/>
 * De doctype = 3 pour une imputation sur un compte général.
 *
 * @author Yannick Majoros <yannick@valuya.be>
 */
public enum WbDocType implements WbValue {

    IMPUT_CLIENT(1),
    IMPUT_SUPPLIER(2),
    IMPUT_GENERAL(3),
    VAT_ZERO(4);

    private final int code;

    private WbDocType(int code) {
        this.code = code;
    }

    @Override
    public String getValue() {
        return Integer.toString(code);
    }

    public int getCode() {
        return code;
    }

    public static WbDocType fromCode(int code) {
        return Stream.of(WbDocType.values())
                .filter(wbDocType -> wbDocType.code == code)
                .findAny()
                .orElseThrow(IllegalArgumentException::new);
    }
}

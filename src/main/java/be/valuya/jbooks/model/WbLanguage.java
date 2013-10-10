package be.valuya.jbooks.model;

import be.valuya.winbooks.LangueforVat;

/**
 *
 * @author Yannick Majoros <yannick@valuya.be>
 */
public enum WbLanguage implements WbValue {

    FRENCH("F", LangueforVat.wbFrench),
    DUTCH("N", LangueforVat.wbNetherland);
    private final String value;
    private final LangueforVat langueforVat;

    private WbLanguage(String value, LangueforVat langueforVat) {
        this.value = value;
        this.langueforVat = langueforVat;
    }

    @Override
    public String getValue() {
        return value;
    }

    public LangueforVat getLangueforVat() {
        return langueforVat;
    }
}

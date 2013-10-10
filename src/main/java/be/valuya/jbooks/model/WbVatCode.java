package be.valuya.jbooks.model;

import java.math.BigDecimal;

/**
 *
 * @author Yannick Majoros <yannick@valuya.be>
 */
public class WbVatCode {

    private String externalVatCode;
    private String internalVatCode;
    private BigDecimal vatRate;
    private String account1;
    private String account2;

    public String getExternalVatCode() {
        return externalVatCode;
    }

    public void setExternalVatCode(String externalVatCode) {
        this.externalVatCode = externalVatCode;
    }

    public String getInternalVatCode() {
        return internalVatCode;
    }

    public void setInternalVatCode(String internalVatCode) {
        this.internalVatCode = internalVatCode;
    }

    public BigDecimal getVatRate() {
        return vatRate;
    }

    public void setVatRate(BigDecimal vatRate) {
        this.vatRate = vatRate;
    }

    public String getAccount1() {
        return account1;
    }

    public void setAccount1(String account1) {
        this.account1 = account1;
    }

    public String getAccount2() {
        return account2;
    }

    public void setAccount2(String account2) {
        this.account2 = account2;
    }

}

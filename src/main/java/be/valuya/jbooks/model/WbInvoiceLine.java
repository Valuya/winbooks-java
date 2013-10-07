package be.valuya.jbooks.model;

import java.math.BigDecimal;

/**
 *
 * @author Yannick Majoros <yannick@valuya.be>
 */
public class WbInvoiceLine implements Cloneable {

    private String description;
    private BigDecimal eVat;
    private BigDecimal vat;
    private BigDecimal vatRate;
    private String accountGl = "700000";

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getEVat() {
        return eVat;
    }

    public void setEVat(BigDecimal eVat) {
        this.eVat = eVat;
    }

    public BigDecimal getVatRate() {
        return vatRate;
    }

    public void setVatRate(BigDecimal vatRate) {
        this.vatRate = vatRate;
    }

    public BigDecimal getVat() {
        return vat;
    }

    public void setVat(BigDecimal vat) {
        this.vat = vat;
    }

    public String getAccountGl() {
        return accountGl;
    }

    public void setAccountGl(String accountGl) {
        this.accountGl = accountGl;
    }

    @Override
    public WbInvoiceLine clone() {
        try {
            return (WbInvoiceLine) super.clone();
        } catch (CloneNotSupportedException ex) {
            throw new RuntimeException(ex);
        }
    }
}
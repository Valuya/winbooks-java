package be.valuya.jbooks.model;

/**
 *
 * @author Yannick Majoros <yannick@valuya.be>
 */
public class WbInvoiceLine implements Cloneable {

    private String description;
    private Double eVat;
    private Double vat;
    private Double vatRate = 21.0;
    private String vatCode;
    private String accountGl = "700000";

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getEVat() {
        return eVat;
    }

    public void setEVat(Double eVat) {
        this.eVat = eVat;
    }

    public Double getVatRate() {
        return vatRate;
    }

    public void setVatRate(Double vatRate) {
        this.vatRate = vatRate;
    }

    public Double getVat() {
        return vat;
    }

    public void setVat(Double vat) {
        this.vat = vat;
    }

    public String getVatCode() {
        return vatCode;
    }

    public void setVatCode(String vatCode) {
        this.vatCode = vatCode;
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
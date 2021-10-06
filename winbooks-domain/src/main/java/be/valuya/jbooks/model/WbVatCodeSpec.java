package be.valuya.jbooks.model;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;

public class WbVatCodeSpec {

    private String wbCode;
    private String frCode;
    private String nlCode;

    // Reference another wbCode
    // CODEND,C,6
    private String wbEndCode;

    private BigDecimal ratePercent;
    private String treeLevel;

    private Date startDate;
    private Date endDate;

    private String frHeader;
    private String frLabel;
    private String frDescription;

    private String nlHeader;
    private String nlLabel;
    private String nlDescription;

    private String accountInvCode1;
    private String accountInvCode2;
    private String accountCnCode1;
    private String accountCnCode2;

    private WbVatCat category;
    private WbVatDeducibility deducibility;

    // ISTAT,L
    private boolean intraComFlag;
    // ISFIN,L
    private boolean finFlag;

    private String baseFormula;
    private String taxFormula;


    public String getWbCode() {
        return wbCode;
    }

    public void setWbCode(String wbCode) {
        this.wbCode = wbCode;
    }

    public String getFrCode() {
        return frCode;
    }

    public void setFrCode(String frCode) {
        this.frCode = frCode;
    }

    public String getNlCode() {
        return nlCode;
    }

    public void setNlCode(String nlCode) {
        this.nlCode = nlCode;
    }

    public String getWbEndCode() {
        return wbEndCode;
    }

    public void setWbEndCode(String wbEndCode) {
        this.wbEndCode = wbEndCode;
    }

    public BigDecimal getRatePercent() {
        return ratePercent;
    }

    public void setRatePercent(BigDecimal ratePercent) {
        this.ratePercent = ratePercent;
    }

    public String getTreeLevel() {
        return treeLevel;
    }

    public void setTreeLevel(String treeLevel) {
        this.treeLevel = treeLevel;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getFrHeader() {
        return frHeader;
    }

    public void setFrHeader(String frHeader) {
        this.frHeader = frHeader;
    }

    public String getFrLabel() {
        return frLabel;
    }

    public void setFrLabel(String frLabel) {
        this.frLabel = frLabel;
    }

    public String getFrDescription() {
        return frDescription;
    }

    public void setFrDescription(String frDescription) {
        this.frDescription = frDescription;
    }

    public String getNlHeader() {
        return nlHeader;
    }

    public void setNlHeader(String nlHeader) {
        this.nlHeader = nlHeader;
    }

    public String getNlLabel() {
        return nlLabel;
    }

    public void setNlLabel(String nlLabel) {
        this.nlLabel = nlLabel;
    }

    public String getNlDescription() {
        return nlDescription;
    }

    public void setNlDescription(String nlDescription) {
        this.nlDescription = nlDescription;
    }

    public String getAccountInvCode1() {
        return accountInvCode1;
    }

    public void setAccountInvCode1(String accountInvCode1) {
        this.accountInvCode1 = accountInvCode1;
    }

    public String getAccountInvCode2() {
        return accountInvCode2;
    }

    public void setAccountInvCode2(String accountInvCode2) {
        this.accountInvCode2 = accountInvCode2;
    }

    public String getAccountCnCode1() {
        return accountCnCode1;
    }

    public void setAccountCnCode1(String accountCnCode1) {
        this.accountCnCode1 = accountCnCode1;
    }

    public String getAccountCnCode2() {
        return accountCnCode2;
    }

    public void setAccountCnCode2(String accountCnCode2) {
        this.accountCnCode2 = accountCnCode2;
    }

    public WbVatCat getCategory() {
        return category;
    }

    public void setCategory(WbVatCat category) {
        this.category = category;
    }

    public WbVatDeducibility getDeducibility() {
        return deducibility;
    }

    public void setDeducibility(WbVatDeducibility deducibility) {
        this.deducibility = deducibility;
    }

    public boolean isIntraComFlag() {
        return intraComFlag;
    }

    public void setIntraComFlag(boolean intraComFlag) {
        this.intraComFlag = intraComFlag;
    }

    public boolean isFinFlag() {
        return finFlag;
    }

    public void setFinFlag(boolean finFlag) {
        this.finFlag = finFlag;
    }

    public String getBaseFormula() {
        return baseFormula;
    }

    public void setBaseFormula(String baseFormula) {
        this.baseFormula = baseFormula;
    }

    public String getTaxFormula() {
        return taxFormula;
    }

    public void setTaxFormula(String taxFormula) {
        this.taxFormula = taxFormula;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WbVatCodeSpec that = (WbVatCodeSpec) o;
        return Objects.equals(wbCode, that.wbCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(wbCode);
    }

    @Override
    public String toString() {
        return "WbVatCodeSpec{" +
                "wbCode='" + wbCode + '\'' +
                ", frCode='" + frCode + '\'' +
                ", frLabel='" + frLabel + '\'' +
                '}';
    }
}

package be.valuya.jbooks.model;

import java.math.BigDecimal;
import java.util.Objects;

/**
 *
 * @author Yannick
 */
public class WbAccount {

    private String accountNumber;
    private String name11;
    private String name12;
    private String name21;
    private String name22;
    private String currency;
    private String accountTypeStr;
    private boolean inby1;
    private boolean inby2;
    private String accountCategoryStr;
    private BigDecimal debCredFlt;
    private boolean toMatch;
    private String centralId;
    private boolean locked;
    private boolean printSum;
    private String vatCode;
    private boolean cncyOnly;
    private BigDecimal totDeb1;
    private BigDecimal totCre1;
    private BigDecimal totDebTmp1;
    private BigDecimal totCreTmp1;
    private BigDecimal totDeb2;
    private BigDecimal totCre2;
    private BigDecimal totDebTmp2;
    private BigDecimal totCreTmp2;
    private BigDecimal totCur1;
    private BigDecimal totCur2;
    private String memoType;
    private boolean doc;
    private boolean analyt;
    private String accBilDb;
    private String accBilCd;
    private String accBnbDb;
    private String accBnbCd;
    private String f28150;
    private String defDed;

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getName11() {
        return name11;
    }

    public void setName11(String name11) {
        this.name11 = name11;
    }

    public String getName12() {
        return name12;
    }

    public void setName12(String name12) {
        this.name12 = name12;
    }

    public String getName21() {
        return name21;
    }

    public void setName21(String name21) {
        this.name21 = name21;
    }

    public String getName22() {
        return name22;
    }

    public void setName22(String name22) {
        this.name22 = name22;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getAccountTypeStr() {
        return accountTypeStr;
    }

    public void setAccountTypeStr(String accountTypeStr) {
        this.accountTypeStr = accountTypeStr;
    }

    public boolean isInby1() {
        return inby1;
    }

    public void setInby1(boolean inby1) {
        this.inby1 = inby1;
    }

    public boolean isInby2() {
        return inby2;
    }

    public void setInby2(boolean inby2) {
        this.inby2 = inby2;
    }

    public String getAccountCategoryStr() {
        return accountCategoryStr;
    }

    public void setAccountCategoryStr(String accountCategoryStr) {
        this.accountCategoryStr = accountCategoryStr;
    }

    public BigDecimal getDebCredFlt() {
        return debCredFlt;
    }

    public void setDebCredFlt(BigDecimal debCredFlt) {
        this.debCredFlt = debCredFlt;
    }

    public boolean isToMatch() {
        return toMatch;
    }

    public void setToMatch(boolean toMatch) {
        this.toMatch = toMatch;
    }

    public String getCentralId() {
        return centralId;
    }

    public void setCentralId(String centralId) {
        this.centralId = centralId;
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public boolean isPrintSum() {
        return printSum;
    }

    public void setPrintSum(boolean printSum) {
        this.printSum = printSum;
    }

    public String getVatCode() {
        return vatCode;
    }

    public void setVatCode(String vatCode) {
        this.vatCode = vatCode;
    }

    public boolean isCncyOnly() {
        return cncyOnly;
    }

    public void setCncyOnly(boolean cncyOnly) {
        this.cncyOnly = cncyOnly;
    }

    public BigDecimal getTotDeb1() {
        return totDeb1;
    }

    public void setTotDeb1(BigDecimal totDeb1) {
        this.totDeb1 = totDeb1;
    }

    public BigDecimal getTotCre1() {
        return totCre1;
    }

    public void setTotCre1(BigDecimal totCre1) {
        this.totCre1 = totCre1;
    }

    public BigDecimal getTotDebTmp1() {
        return totDebTmp1;
    }

    public void setTotDebTmp1(BigDecimal totDebTmp1) {
        this.totDebTmp1 = totDebTmp1;
    }

    public BigDecimal getTotCreTmp1() {
        return totCreTmp1;
    }

    public void setTotCreTmp1(BigDecimal totCreTmp1) {
        this.totCreTmp1 = totCreTmp1;
    }

    public BigDecimal getTotDeb2() {
        return totDeb2;
    }

    public void setTotDeb2(BigDecimal totDeb2) {
        this.totDeb2 = totDeb2;
    }

    public BigDecimal getTotCre2() {
        return totCre2;
    }

    public void setTotCre2(BigDecimal totCre2) {
        this.totCre2 = totCre2;
    }

    public BigDecimal getTotDebTmp2() {
        return totDebTmp2;
    }

    public void setTotDebTmp2(BigDecimal totDebTmp2) {
        this.totDebTmp2 = totDebTmp2;
    }

    public BigDecimal getTotCreTmp2() {
        return totCreTmp2;
    }

    public void setTotCreTmp2(BigDecimal totCreTmp2) {
        this.totCreTmp2 = totCreTmp2;
    }

    public BigDecimal getTotCur1() {
        return totCur1;
    }

    public void setTotCur1(BigDecimal totCur1) {
        this.totCur1 = totCur1;
    }

    public BigDecimal getTotCur2() {
        return totCur2;
    }

    public void setTotCur2(BigDecimal totCur2) {
        this.totCur2 = totCur2;
    }

    public String getMemoType() {
        return memoType;
    }

    public void setMemoType(String memoType) {
        this.memoType = memoType;
    }

    public boolean isDoc() {
        return doc;
    }

    public void setDoc(boolean doc) {
        this.doc = doc;
    }

    public boolean isAnalyt() {
        return analyt;
    }

    public void setAnalyt(boolean analyt) {
        this.analyt = analyt;
    }

    public String getAccBilDb() {
        return accBilDb;
    }

    public void setAccBilDb(String accBilDb) {
        this.accBilDb = accBilDb;
    }

    public String getAccBilCd() {
        return accBilCd;
    }

    public void setAccBilCd(String accBilCd) {
        this.accBilCd = accBilCd;
    }

    public String getAccBnbDb() {
        return accBnbDb;
    }

    public void setAccBnbDb(String accBnbDb) {
        this.accBnbDb = accBnbDb;
    }

    public String getAccBnbCd() {
        return accBnbCd;
    }

    public void setAccBnbCd(String accBnbCd) {
        this.accBnbCd = accBnbCd;
    }

    public String getF28150() {
        return f28150;
    }

    public void setF28150(String f28150) {
        this.f28150 = f28150;
    }

    public String getDefDed() {
        return defDed;
    }

    public void setDefDed(String defDed) {
        this.defDed = defDed;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + Objects.hashCode(this.accountNumber);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final WbAccount other = (WbAccount) obj;
        if (!Objects.equals(this.accountNumber, other.accountNumber)) {
            return false;
        }
        return true;
    }

}

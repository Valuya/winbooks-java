package be.valuya.jbooks.model;

import java.util.Date;
import java.util.List;

/**
 *
 * @author Yannick Majoros <yannick@valuya.be>
 */
public class WbInvoice {

    private WbDocType wbDocType = WbDocType.IMPUT_CLIENT;
    private WbDbkType wbDbkType;
    private String dbkCode;
    private String ref;
    private Date date;
    private Date dueDate;
    private WbClientSupplier wbClientSupplier;
    private String accountGl = "400000";
    private String description;
    private String commStruct;
    // fill those 2 for period / book year as expected by Winbooks
    private String periodStr;
    private String bookYearStr;
    // optionnally fill this one instead of period / bookYearStr
    private Date periodDate;
    private List<WbInvoiceLine> invoiceLines;

    public WbDocType getWbDocType() {
        return wbDocType;
    }

    public void setWbDocType(WbDocType wbDocType) {
        this.wbDocType = wbDocType;
    }

    public WbDbkType getWbDbkType() {
        return wbDbkType;
    }

    public void setWbDbkType(WbDbkType wbDbkType) {
        this.wbDbkType = wbDbkType;
    }

    public String getDbkCode() {
        return dbkCode;
    }

    public void setDbkCode(String dbkCode) {
        this.dbkCode = dbkCode;
    }

    public String getRef() {
        return ref;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public WbClientSupplier getWbClientSupplier() {
        return wbClientSupplier;
    }

    public void setWbClientSupplier(WbClientSupplier wbClientSupplier) {
        this.wbClientSupplier = wbClientSupplier;
    }

    public String getAccountGl() {
        return accountGl;
    }

    public void setAccountGl(String accountGl) {
        this.accountGl = accountGl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCommStruct() {
        return commStruct;
    }

    public void setCommStruct(String commStruct) {
        this.commStruct = commStruct;
    }

    public String getPeriodStr() {
        return periodStr;
    }

    public void setPeriodStr(String periodStr) {
        this.periodStr = periodStr;
    }

    public String getBookYearStr() {
        return bookYearStr;
    }

    public void setBookYearStr(String bookYearStr) {
        this.bookYearStr = bookYearStr;
    }

    public Date getPeriodDate() {
        return periodDate;
    }

    public void setPeriodDate(Date periodDate) {
        this.periodDate = periodDate;
    }

    public List<WbInvoiceLine> getInvoiceLines() {
        return invoiceLines;
    }

    public void setInvoiceLines(List<WbInvoiceLine> invoiceLines) {
        this.invoiceLines = invoiceLines;
    }
}
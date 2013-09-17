package be.valuya.jbooks.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author Yannick Majoros <yannick@valuya.be>
 */
public class WbInvoice {

    private WbDocType docType = WbDocType.IMPUT_CLIENT;
    private String ref;
    private String book;
    private Double eVat;
    private Double vat;
    private Date date;
    private Date dueDate;
    private String clientRef;
    private WbClientSupplier wbClientSupplier;
    private String accountGl = "400000";
    private String description;
    private String commStruct;
    private Date periodDate;
    private List<WbInvoiceLine> invoiceLines = new ArrayList<WbInvoiceLine>();

    public WbDocType getDocType() {
        return docType;
    }

    public void setDocType(WbDocType docType) {
        this.docType = docType;
    }

    public String getRef() {
        return ref;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }

    public String getBook() {
        return book;
    }

    public void setBook(String book) {
        this.book = book;
    }

    public Double geteVat() {
        return eVat;
    }

    public void seteVat(Double eVat) {
        this.eVat = eVat;
    }

    public Double getVat() {
        return vat;
    }

    public void setVat(Double vat) {
        this.vat = vat;
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

    public String getClientRef() {
        return clientRef;
    }

    public void setClientRef(String clientRef) {
        this.clientRef = clientRef;
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
package be.valuya.winbooks.api.accountingtroll.cache;

import be.valuya.accountingtroll.domain.ATBookPeriod;
import be.valuya.accountingtroll.domain.ATDocument;

import java.util.Objects;

public class ATDocumentCacheKey {

    private final String documentNumber;
    private final String dbkCode;
    private final ATBookPeriod period;

    public ATDocumentCacheKey(String documentNumber, String dbkCode, ATBookPeriod accountingPeriod) {
        this.documentNumber = documentNumber;
        this.dbkCode = dbkCode;
        this.period = accountingPeriod;
    }

    public ATDocumentCacheKey(ATDocument document) {
        String documentNumnber = document.getDocumentNumber();
        ATBookPeriod bookPeriod = document.getBookPeriod();
        String dbkCode = document.getDbkCode();

        this.documentNumber = documentNumnber;
        this.dbkCode = dbkCode;
        this.period = bookPeriod;
    }

    public String getDocumentNumber() {
        return documentNumber;
    }

    public String getDbkCode() {
        return dbkCode;
    }

    public ATBookPeriod getPeriod() {
        return period;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ATDocumentCacheKey that = (ATDocumentCacheKey) o;
        return Objects.equals(documentNumber, that.documentNumber) &&
                Objects.equals(dbkCode, that.dbkCode) &&
                Objects.equals(period, that.period);
    }

    @Override
    public int hashCode() {
        return Objects.hash(documentNumber, dbkCode, period);
    }
}

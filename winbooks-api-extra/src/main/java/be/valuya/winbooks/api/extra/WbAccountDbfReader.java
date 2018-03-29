package be.valuya.winbooks.api.extra;

import be.valuya.jbooks.model.WbAccount;
import net.iryndin.jdbf.core.DbfField;
import net.iryndin.jdbf.core.DbfRecord;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * @author Yannick
 */
public class WbAccountDbfReader {

    public WbAccount readWbAccountFromAcfDbfRecord(DbfRecord dbfRecord) {
        String accountTypeStr = dbfRecord.getString("TYPE");
        String accountNumber = dbfRecord.getString("NUMBER");
        String name11 = dbfRecord.getString("NAME11");
        String name12 = dbfRecord.getString("NAME12");
        String name21 = dbfRecord.getString("NAME21");
        String name22 = dbfRecord.getString("NAME22");
        boolean inby1 = dbfRecord.getBoolean("ISINBY1");
        boolean inby2 = dbfRecord.getBoolean("ISINBY2");
        String accountCategoryStr = dbfRecord.getString("CATEGORY");
        BigDecimal debCredFlt = getDbfFieldOptional(dbfRecord, "DEBCREDFLT")
                .map(DbfField::getStringRepresentation)
                .map(BigDecimal::new)
                .orElse(null);
        Boolean toMatchNullable = dbfRecord.getBoolean("ISTOMATCH");
        boolean toMatch = Optional.ofNullable(toMatchNullable).orElse(false);
        String centralId = dbfRecord.getString("CENTRALID");
        Boolean lockedNullable = dbfRecord.getBoolean("ISLOCKED");
        boolean locked = Optional.ofNullable(lockedNullable).orElse(false);
        Boolean printSumNullable = dbfRecord.getBoolean("ISPRINTSUM");
        boolean printSum = Optional.ofNullable(printSumNullable).orElse(false);
        String vatCodeStr = dbfRecord.getString("VATCODE");
        String currencyStr = dbfRecord.getString("CURRENCY");
        Boolean cncyOnlyNullable = dbfRecord.getBoolean("CNCYONLY");
        boolean cncyOnly = Optional.ofNullable(cncyOnlyNullable).orElse(false);
        BigDecimal totDeb1 = dbfRecord.getBigDecimal("TOTDEB1");
        BigDecimal totCre1 = dbfRecord.getBigDecimal("TOTCRE1");
        BigDecimal totDebTmp1 = dbfRecord.getBigDecimal("TOTDEBTMP1");
        BigDecimal totCreTmp1 = dbfRecord.getBigDecimal("TOTCRETMP1");
        BigDecimal totDeb2 = dbfRecord.getBigDecimal("TOTDEB2");
        BigDecimal totCre2 = dbfRecord.getBigDecimal("TOTCRE2");
        BigDecimal totDebTmp2 = dbfRecord.getBigDecimal("TOTDEBTMP2");
        BigDecimal totCreTmp2 = dbfRecord.getBigDecimal("TOTCRETMP2");
        BigDecimal totCur1 = dbfRecord.getBigDecimal("TOTCUR1");
        BigDecimal totCur2 = dbfRecord.getBigDecimal("TOTCUR2");
        String memoType = dbfRecord.getString("MEMOTYPE");
        boolean doc = getBooleanOptional(dbfRecord, "ISDOC")
                .orElse(false);
        boolean analyt = getBooleanOptional(dbfRecord, "ISANALYT")
                .orElse(false);
        String accBilDb = dbfRecord.getString("ACCBILDB");
        String accBilCd = dbfRecord.getString("ACCBILCD");
        String accBnbDb = dbfRecord.getString("ACCBNBDB");
        String accBnbCd = dbfRecord.getString("ACCBNBCD");
        String f28150 = dbfRecord.getString("F28150");
        String defDed = getStringOptional(dbfRecord, "DEFDED")
                .orElse(null);

        WbAccount wbAccount = new WbAccount();
        wbAccount.setAccountNumber(accountNumber);
        wbAccount.setName11(name11);
        wbAccount.setName12(name12);
        wbAccount.setName21(name21);
        wbAccount.setName22(name22);
        wbAccount.setAccBilCd(accBilCd);
        wbAccount.setAccBilDb(accBilDb);
        wbAccount.setAccBnbCd(accBnbCd);
        wbAccount.setAccBnbDb(accBnbDb);
        wbAccount.setAccountCategoryStr(accountCategoryStr);
        wbAccount.setAccountNumber(accountNumber);
        wbAccount.setAccountTypeStr(accountTypeStr);
        wbAccount.setAnalyt(analyt);
        wbAccount.setCentralId(centralId);
        wbAccount.setCncyOnly(cncyOnly);
        wbAccount.setCurrency(currencyStr);
        wbAccount.setDebCredFlt(debCredFlt);
        wbAccount.setDefDed(defDed);
        wbAccount.setDoc(doc);
        wbAccount.setF28150(f28150);
        wbAccount.setInby1(inby1);
        wbAccount.setInby2(inby2);
        wbAccount.setLocked(locked);
        wbAccount.setMemoType(memoType);
        wbAccount.setPrintSum(printSum);
        wbAccount.setToMatch(toMatch);
        wbAccount.setTotCre1(totCre1);
        wbAccount.setTotCre2(totCre2);
        wbAccount.setTotCreTmp1(totCreTmp1);
        wbAccount.setTotCreTmp2(totCreTmp2);
        wbAccount.setTotCur1(totCur1);
        wbAccount.setTotCur2(totCur2);
        wbAccount.setTotDeb1(totDeb1);
        wbAccount.setTotDeb2(totDeb2);
        wbAccount.setTotDebTmp1(totDebTmp1);
        wbAccount.setTotDebTmp2(totDebTmp2);
        wbAccount.setVatCode(vatCodeStr);

        return wbAccount;
    }

    private Optional<Boolean> getBooleanOptional(DbfRecord dbfRecord, String fieldName) {
        return getDbfFieldOptional(dbfRecord, fieldName)
                .map(docField -> dbfRecord.getBoolean(fieldName));
    }

    private Optional<String> getStringOptional(DbfRecord dbfRecord, String fieldName) {
        return getDbfFieldOptional(dbfRecord, fieldName)
                .map(docField -> dbfRecord.getString(fieldName));
    }

    private Optional<DbfField> getDbfFieldOptional(DbfRecord dbfRecord, String fieldName) {
        DbfField fieldNullable = dbfRecord.getField(fieldName);
        return Optional.ofNullable(fieldNullable);
    }

}

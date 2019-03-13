package be.valuya.winbooks.api.extra;

import be.valuya.jbooks.model.WbClientSupplier;
import be.valuya.jbooks.model.WbClientSupplierType;
import be.valuya.jbooks.model.WbMemoType;
import be.valuya.jbooks.model.WbVatCat;
import be.valuya.jbooks.model.factory.WbClientSupplierFactory;
import be.valuya.winbooks.domain.error.WinbooksError;
import be.valuya.winbooks.domain.error.WinbooksException;
import net.iryndin.jdbf.core.DbfRecord;

import java.text.ParseException;
import java.util.Date;
import java.util.Optional;

public class WbClientSupplierDbfReader {

    public WbClientSupplier readWbClientSupplierFromAcfDbfRecord(DbfRecord dbfRecord) {
        try {
            String number = dbfRecord.getString("NUMBER");
            String typeNullable = dbfRecord.getString("TYPE");
            String name1 = dbfRecord.getString("NAME1");
            String name2 = dbfRecord.getString("NAME2");
            String civName1 = dbfRecord.getString("CIVNAME1");
            String civName2 = dbfRecord.getString("CIVNAME2");
            String address1 = dbfRecord.getString("ADRESS1");
            String address2 = dbfRecord.getString("ADRESS2");
            String vatCatNullable = dbfRecord.getString("VATCAT");
            String countryCode = dbfRecord.getString("COUNTRY");
            String vatNumber = dbfRecord.getString("VATNUMBER");
            String payCode = dbfRecord.getString("PAYCODE");
            String telNumber = dbfRecord.getString("TELNUMBER");
            String faxNumber = dbfRecord.getString("FAXNUMBER");
            String bankAccount = dbfRecord.getString("BNKACCNT");
            String zipCode = dbfRecord.getString("ZIPCODE");
            String city = dbfRecord.getString("CITY");
            String defltPost = dbfRecord.getString("DEFLTPOST");
            String lang = dbfRecord.getString("LANG");
            String category = dbfRecord.getString("CATEGORY");
            String central = dbfRecord.getString("CENTRAL");
            String vatCode = dbfRecord.getString("VATCODE");
            String currency = dbfRecord.getString("CURRENCY");
            String lastRemLev = dbfRecord.getString("LASTREMLEV");
            Date lastRemDat = dbfRecord.getDate("LASTREMDAT");
            Boolean lockedNullable = dbfRecord.getBoolean("ISLOCKED");
            boolean locked = Optional.ofNullable(lockedNullable).orElse(false);
            String memoTypeNullable = dbfRecord.getString("MEMOTYPE");
            Boolean docNullable = dbfRecord.getBoolean("ISDOC");
            boolean doc = Optional.ofNullable(docNullable).orElse(false);


            WbClientSupplier wbClientSupplier = WbClientSupplierFactory.createWbClientSupplier();
            wbClientSupplier.setNumber(number);
            wbClientSupplier.setName1(name1);
            wbClientSupplier.setName2(name2);
            wbClientSupplier.setCivName1(civName1);
            wbClientSupplier.setCivName2(civName2);
            wbClientSupplier.setAddress1(address1);
            wbClientSupplier.setAddress2(address2);
            wbClientSupplier.setCountryCode(countryCode);
            wbClientSupplier.setVatNumber(vatNumber);
            wbClientSupplier.setPayCode(payCode);
            wbClientSupplier.setTelNumber(telNumber);
            wbClientSupplier.setFaxNumber(faxNumber);
            wbClientSupplier.setBankAccount(bankAccount);
            wbClientSupplier.setZipCode(zipCode);
            wbClientSupplier.setCity(city);
            wbClientSupplier.setDefltPost(defltPost);
            wbClientSupplier.setLang(lang);
            wbClientSupplier.setCategory(category);
            wbClientSupplier.setCentral(central);
            wbClientSupplier.setVatCode(vatCode);
            wbClientSupplier.setCurrency(currency);
            wbClientSupplier.setLastRemLev(lastRemLev);
            wbClientSupplier.setLastRemDat(lastRemDat);
            wbClientSupplier.setLocked(locked);
            wbClientSupplier.setDoc(doc);

            Optional.ofNullable(memoTypeNullable)
                    .map(Integer::parseInt)
                    .map(WbMemoType::fromCode)
                    .ifPresent(wbClientSupplier::setWbMemoType);

            Optional.ofNullable(typeNullable)
                    .map(WbClientSupplierType::fromCode)
                    .ifPresent(wbClientSupplier::setWbClientSupplierType);

            Optional.ofNullable(vatCatNullable)
                    .map(WbVatCat::fromCode);

            return wbClientSupplier;
        } catch (ParseException parseException) {
            throw new WinbooksException(WinbooksError.UNKNOWN_ERROR, parseException);
        }
    }
}

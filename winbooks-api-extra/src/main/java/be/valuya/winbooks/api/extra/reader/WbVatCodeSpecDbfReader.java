package be.valuya.winbooks.api.extra.reader;

import be.valuya.jbooks.model.WbVatCat;
import be.valuya.jbooks.model.WbVatCodeSpec;
import be.valuya.jbooks.model.WbVatDeducibility;
import be.valuya.winbooks.domain.error.WinbooksError;
import be.valuya.winbooks.domain.error.WinbooksException;
import net.iryndin.jdbf.core.DbfRecord;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Date;
import java.util.Optional;

public class WbVatCodeSpecDbfReader {

    public WbVatCodeSpec readWbVatCodeSpecFromDbf(DbfRecord dbfRecord) {
        try {
            String wbCode = dbfRecord.getString("CODE");
            String codeFr = dbfRecord.getString("USRCODE1");
            String codeNl = dbfRecord.getString("USRCODE2");
            BigDecimal rate = dbfRecord.getBigDecimal("RATE");
            String treeLevel = dbfRecord.getString("TREELEVEL");
            Date startDate = dbfRecord.getDate("DATE_BEG");
            Date endDate = dbfRecord.getDate("DATE_END");
            String headerFr = dbfRecord.getString("TREELIB1");
            String headerNl = dbfRecord.getString("TREELIB2");
            String labelFr = dbfRecord.getString("DESC1");
            String labelNl = dbfRecord.getString("DESC2");
            String taxCategoryCode = dbfRecord.getString("TAX");
            String deducibilityCode = dbfRecord.getString("TYPDED");
            Boolean TOVER = dbfRecord.getBoolean("TOVER");
            String ICOM = dbfRecord.getString("ICOM");
            Boolean istatFlag = dbfRecord.getBoolean("ISTAT");
            String BASE_INV = dbfRecord.getString("BASE_INV");
            String TAX_INV = dbfRecord.getString("TAX_INV");
            String BASE_CN = dbfRecord.getString("BASE_CN");
            String TAX_CN = dbfRecord.getString("TAX_CN");
            String invCode1 = dbfRecord.getString("ACCINV1");
            String invCode2 = dbfRecord.getString("ACCINV2");
            String cnCode1 = dbfRecord.getString("ACCCN1");
            String cnCode2 = dbfRecord.getString("ACCCN2");
            String DEFCLASS = dbfRecord.getString("DEFCLASS");
            String baseFormula = dbfRecord.getString("BASFORM");
            String taxFormula = dbfRecord.getString("TAXFORM");
            String resumeFr = dbfRecord.getString("RESUME1");
            String resumeNl = dbfRecord.getString("RESUME2");
            String endCode = dbfRecord.getString("CODEND");
            Boolean finFlag = dbfRecord.getBoolean("ISFIN");


            WbVatCodeSpec wbVatCodeSpec = new WbVatCodeSpec();
            wbVatCodeSpec.setWbCode(wbCode);
            wbVatCodeSpec.setFrCode(codeFr);
            wbVatCodeSpec.setNlCode(codeNl);
            wbVatCodeSpec.setWbEndCode(endCode);

            wbVatCodeSpec.setRatePercent(rate);
            wbVatCodeSpec.setTreeLevel(treeLevel);

            wbVatCodeSpec.setStartDate(startDate);
            wbVatCodeSpec.setEndDate(endDate);

            wbVatCodeSpec.setFrHeader(headerFr);
            wbVatCodeSpec.setFrLabel(labelFr);
            wbVatCodeSpec.setFrDescription(resumeFr);

            wbVatCodeSpec.setNlHeader(headerNl);
            wbVatCodeSpec.setNlLabel(labelNl);
            wbVatCodeSpec.setNlDescription(resumeNl);

            wbVatCodeSpec.setAccountInvCode1(invCode1);
            wbVatCodeSpec.setAccountInvCode2(invCode2);
            wbVatCodeSpec.setAccountCnCode1(cnCode1);
            wbVatCodeSpec.setAccountCnCode2(cnCode2);

            Optional.ofNullable(taxCategoryCode)
                    .map(WbVatCat::fromCode)
                    .ifPresent(wbVatCodeSpec::setCategory);

            Optional.ofNullable(deducibilityCode)
                    .map(WbVatDeducibility::fromCode)
                    .ifPresent(wbVatCodeSpec::setDeducibility);

            Optional.ofNullable(istatFlag)
                    .ifPresent(wbVatCodeSpec::setIntraComFlag);
            Optional.ofNullable(finFlag)
                    .ifPresent(wbVatCodeSpec::setFinFlag);

            wbVatCodeSpec.setBaseFormula(baseFormula);
            wbVatCodeSpec.setTaxFormula(taxFormula);
            return wbVatCodeSpec;

        } catch (ParseException parseException) {
            throw new WinbooksException(WinbooksError.UNKNOWN_ERROR, parseException);
        }
    }
}

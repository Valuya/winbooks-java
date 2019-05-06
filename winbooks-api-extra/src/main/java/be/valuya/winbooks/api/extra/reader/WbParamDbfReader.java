package be.valuya.winbooks.api.extra.reader;

import be.valuya.jbooks.model.WbAccount;
import be.valuya.jbooks.model.WbParam;
import net.iryndin.jdbf.core.DbfField;
import net.iryndin.jdbf.core.DbfRecord;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * @author Yannick
 */
public class WbParamDbfReader {

    public WbParam readWbParamFromDbfRecord(DbfRecord dbfRecord) {
        String id = dbfRecord.getString("ID");
        String valueStr = dbfRecord.getString("VALUE");

        WbParam wbParam = new WbParam();
        wbParam.setId(id);
        wbParam.setValue(valueStr);

        return wbParam;
    }

}

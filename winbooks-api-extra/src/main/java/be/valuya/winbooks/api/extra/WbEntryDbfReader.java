package be.valuya.winbooks.api.extra;

import be.valuya.jbooks.model.WbDbkType;
import be.valuya.jbooks.model.WbDocOrderType;
import be.valuya.jbooks.model.WbDocStatus;
import be.valuya.jbooks.model.WbDocType;
import be.valuya.jbooks.model.WbEntry;
import be.valuya.jbooks.model.WbMemoType;
import be.valuya.winbooks.domain.error.WinbooksError;
import be.valuya.winbooks.domain.error.WinbooksException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.util.Date;
import java.util.Locale;
import java.util.Optional;
import net.iryndin.jdbf.core.DbfRecord;

/**
 *
 * @author Yannick
 */
public class WbEntryDbfReader {

    public WbEntry readWbEntryFromActDbfRecord(DbfRecord dbfRecord) {
        try {
            DecimalFormat vatRateFormat = new DecimalFormat("0.00");
            DecimalFormat moneyFormat = new DecimalFormat("0.00");
            DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols(Locale.ENGLISH);
            vatRateFormat.setDecimalFormatSymbols(decimalFormatSymbols);
            moneyFormat.setDecimalFormatSymbols(decimalFormatSymbols);
            DecimalFormat docOrderFormat = new DecimalFormat("000");

            String accountGl = dbfRecord.getString("ACCOUNTGL");
            String accountRp = dbfRecord.getString("ACCOUNTRP");
            BigDecimal amount = dbfRecord.getBigDecimal("AMOUNT");
            BigDecimal amountEur = dbfRecord.getBigDecimal("AMOUNTEUR");
            String bookYear = dbfRecord.getString("BOOKYEAR");
            String comment = dbfRecord.getString("COMMENT");
            String commentExt = dbfRecord.getString("COMMENTEXT");
            BigDecimal curEurBase = dbfRecord.getBigDecimal("CUREURBASE");
            BigDecimal curRate = dbfRecord.getBigDecimal("CURRATE");
            BigDecimal currAmount = dbfRecord.getBigDecimal("CURRAMOUNT");
            String currCode = dbfRecord.getString("CURRCODE");
            Date date = dbfRecord.getDate("DATE");
            Date dateDoc = dbfRecord.getDate("DATEDOC");
            String docOrderStr = Optional.ofNullable(dbfRecord.getString("DOCORDER")).orElse("0");

            WbDocOrderType docOrderType = WbDocOrderType.fromString(docOrderStr);
            Integer docOrder;
            if (docOrderType == WbDocOrderType.NUMBER) {
                docOrder = docOrderFormat.parse(docOrderStr).intValue();
            } else {
                docOrder = null;
            }
            String docNumber = dbfRecord.getString("DOCNUMBER");

            WbDocStatus docStatus = Optional.ofNullable(dbfRecord.getBigDecimal("DOCSTATUS"))
                    .map(BigDecimal::intValue)
                    .map(WbDocStatus::fromCode)
                    .orElse(WbDocStatus.UNKNOWN);

            Date dueDate = dbfRecord.getDate("DUEDATE");
            String matchNo = dbfRecord.getString("MATCHNO");
            WbMemoType memoType = Optional.ofNullable(dbfRecord.getString("MEMOTYPE"))
                    .map(Integer::valueOf)
                    .map(WbMemoType::fromCode)
                    .orElse(WbMemoType.MEMO);
            Date oldDate = dbfRecord.getDate("OLDDATE");
            String period = dbfRecord.getString("PERIOD");
            BigDecimal vatBase = dbfRecord.getBigDecimal("VATBASE");
            String vatCode = dbfRecord.getString("VATCODE");
            String vatImput = dbfRecord.getString("VATIMPUT");
            BigDecimal vatTax = dbfRecord.getBigDecimal("VATTAX");
            String dbkCode = dbfRecord.getString("ACCOUNTGL");
            WbDbkType wbDbkType = Optional.ofNullable(dbfRecord.getBigDecimal("DBKTYPE"))
                    .map(BigDecimal::intValue)
                    .map(WbDbkType::fromCode)
                    .orElse(null);
            WbDocType wbDocType = Optional.ofNullable(dbfRecord.getBigDecimal("DOCTYPE"))
                    .map(BigDecimal::intValue)
                    .map(WbDocType::fromCode)
                    .orElse(null);

            WbEntry wbEntry = new WbEntry();
            wbEntry.setAccountGl(accountGl);
            wbEntry.setAccountRp(accountRp);
            wbEntry.setAmount(amount);
            wbEntry.setAmountEur(amountEur);
            wbEntry.setBookYear(bookYear);
            wbEntry.setComment(comment);
            wbEntry.setCommentExt(commentExt);
            wbEntry.setCurEurBase(curEurBase);
            wbEntry.setCurRate(curRate);
            wbEntry.setCurrAmount(currAmount);
            wbEntry.setCurrCode(currCode);
            wbEntry.setDate(date);
            wbEntry.setDateDoc(dateDoc);
            wbEntry.setWbDocOrderType(docOrderType);
            wbEntry.setDocOrder(docOrder);
            wbEntry.setDocNumber(docNumber);
            wbEntry.setDocStatus(docStatus);
            wbEntry.setDueDate(dueDate);
            wbEntry.setMatchNo(matchNo);
            wbEntry.setMemoType(memoType);
            wbEntry.setOldDate(oldDate);
            wbEntry.setPeriod(period);
            wbEntry.setVatBase(vatBase);
            wbEntry.setVatCode(vatCode);
            wbEntry.setVatImput(vatImput);
            wbEntry.setVatTax(vatTax);
            wbEntry.setDbkCode(dbkCode);
            wbEntry.setWbDbkType(wbDbkType);
            wbEntry.setWbDocType(wbDocType);

            return wbEntry;
        } catch (ParseException exception) {
            throw new WinbooksException(WinbooksError.UNKNOWN_ERROR, exception);
        }
    }

}

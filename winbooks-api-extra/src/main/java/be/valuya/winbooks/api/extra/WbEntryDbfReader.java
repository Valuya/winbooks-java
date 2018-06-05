package be.valuya.winbooks.api.extra;

import be.valuya.jbooks.model.WbBookYearFull;
import be.valuya.jbooks.model.WbDbkType;
import be.valuya.jbooks.model.WbDocOrderType;
import be.valuya.jbooks.model.WbDocStatus;
import be.valuya.jbooks.model.WbDocType;
import be.valuya.jbooks.model.WbEntry;
import be.valuya.jbooks.model.WbMemoType;
import be.valuya.jbooks.model.WbPeriod;
import be.valuya.winbooks.domain.error.WinbooksError;
import be.valuya.winbooks.domain.error.WinbooksException;
import net.iryndin.jdbf.core.DbfRecord;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Optional;

/**
 * @author Yannick
 */
public class WbEntryDbfReader {

    private final PeriodResolver periodResolver;

    public WbEntryDbfReader(PeriodResolver periodResolver) {
        this.periodResolver = periodResolver;
    }

    public Optional<WbEntry> readWbEntryFromActDbfRecord(DbfRecord dbfRecord) {
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
            if (bookYear == null) {
                return Optional.empty();
            }
            int bookYearInt = parseBookYear(bookYear);
            WbBookYearFull wbBookYearFull = periodResolver.findWbBookYearFull(bookYearInt);

            String period = dbfRecord.getString("PERIOD");
            if (period == null) {
                return Optional.empty();
            }
            int periodIndex = Integer.parseInt(period);
            WbPeriod wbPeriod = periodResolver.findWbPeriod(wbBookYearFull, periodIndex);

            String comment = dbfRecord.getString("COMMENT");
            String commentExt = dbfRecord.getString("COMMENTEXT");
            BigDecimal curEurBase = dbfRecord.getBigDecimal("CUREURBASE");
            BigDecimal curRate = dbfRecord.getBigDecimal("CURRATE");
            BigDecimal currAmount = dbfRecord.getBigDecimal("CURRAMOUNT");
            String currCode = dbfRecord.getString("CURRCODE");
            Date date = getDate(dbfRecord, "DATE");
            Date dateDoc = getDate(dbfRecord, "DATEDOC");
            String docOrderNullable = dbfRecord.getString("DOCORDER");

            WbDocOrderType docOrderType = Optional.ofNullable(docOrderNullable)
                    .map(WbDocOrderType::fromStringOptional)
                    .orElseGet(Optional::empty) // unknown doc order
                    .orElse(WbDocOrderType.BALANCE); // no doc order
            Integer docOrder;
            if (docOrderType == WbDocOrderType.NUMBER) {
                docOrder = docOrderFormat.parse(docOrderNullable).intValue();
            } else {
                docOrder = null;
            }
            String docNumber = dbfRecord.getString("DOCNUMBER");

            WbDocStatus docStatus = Optional.ofNullable(dbfRecord.getBigDecimal("DOCSTATUS"))
                    .map(BigDecimal::intValue)
                    .map(WbDocStatus::fromCode)
                    .orElse(WbDocStatus.UNKNOWN);

            Date dueDate = getDate(dbfRecord, "DUEDATE");
            String matchNo = dbfRecord.getString("MATCHNO");
            String memotype = dbfRecord.getString("MEMOTYPE");
            WbMemoType memoType = Optional.ofNullable(memotype)
                    .map(Integer::valueOf)
                    .map(WbMemoType::fromCode)
                    .orElse(WbMemoType.MEMO);
            Date oldDate = getDate(dbfRecord, "OLDDATE");
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

            int recordNumber = dbfRecord.getRecordNumber();

            WbEntry wbEntry = new WbEntry();
            wbEntry.setRecordNumber(recordNumber);
            wbEntry.setAccountGl(accountGl);
            wbEntry.setAccountRp(accountRp);
            wbEntry.setAmount(amount);
            wbEntry.setAmountEur(amountEur);
            wbEntry.setBookYear(bookYear);
            wbEntry.setWbBookYearFull(wbBookYearFull);
            wbEntry.setWbPeriod(wbPeriod);
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

            return Optional.of(wbEntry);
        } catch (ParseException exception) {
            throw new WinbooksException(WinbooksError.UNKNOWN_ERROR, exception);
        }
    }

    private int parseBookYear(String bookYear) {
        char bookYearChar = bookYear.charAt(0);
        if (bookYearChar >= '0' && bookYearChar <= '9') {
            return bookYearChar - '0';
        }
        char upperCaseBookYearChar = Character.toUpperCase(bookYearChar);
        if (upperCaseBookYearChar >= 'A' && upperCaseBookYearChar <= 'Z') {
            return upperCaseBookYearChar - 'A' + 10;
        }
        throw new IllegalArgumentException("Unknown book year value: " + bookYear);
    }

    public Date getDate(DbfRecord dbfRecord, String fieldName) {
        String stringValueNullable = dbfRecord.getString(fieldName);
        return Optional.ofNullable(stringValueNullable)
                .map(String::trim)
                .filter(dateStr -> !dateStr.isEmpty())
                .map(this::parseDate)
                .orElse(null);
    }

    private Date parseDate(String dateStr) {
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        try {
            return dateFormat.parse(dateStr);
        } catch (ParseException e) {
            throw new IllegalArgumentException(dateStr);
        }
    }
}

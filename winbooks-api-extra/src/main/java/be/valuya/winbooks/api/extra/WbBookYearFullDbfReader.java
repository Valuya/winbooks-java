package be.valuya.winbooks.api.extra;

import be.valuya.jbooks.model.WbBookYearFull;
import be.valuya.jbooks.model.WbBookYearStatus;
import be.valuya.winbooks.domain.error.WinbooksError;
import be.valuya.winbooks.domain.error.WinbooksException;
import java.nio.ByteBuffer;
import java.text.ParseException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import net.iryndin.jdbf.core.DbfRecord;

/**
 *
 * @author Yannick
 */
public class WbBookYearFullDbfReader {

    public WbBookYearFull readWbBookYearFromSlbkyDbfRecord(DbfRecord dbfRecord) {
        try {
            String bookYearStr = dbfRecord.getString("BOOKYEAR");
            int bookYearIndex = readIndex(bookYearStr);

            String periodsStr = dbfRecord.getString("PERIODS");
            int periods = Integer.valueOf(periodsStr);

            int yearStartInt = readYearField(dbfRecord, "YEAR_BEGIN");
            int yearEndInt = readYearField(dbfRecord, "YEAR_END");

            String longName = dbfRecord.getString("YEAR_CPT");
            String shortName = dbfRecord.getString("YEAR_SHORT");

            Date oldStartDate = dbfRecord.getDate("BKY_START");
            LocalDate startDate = convertDateToLocalDate(oldStartDate);

            Date oldEndDate = dbfRecord.getDate("BKY_END");
            LocalDate endDate = convertDateToLocalDate(oldEndDate);

            WbBookYearFull wbBookYearFull = new WbBookYearFull();
            wbBookYearFull.setIndex(bookYearIndex);
            wbBookYearFull.setPeriods(periods);
            wbBookYearFull.setYearBeginInt(yearStartInt);
            wbBookYearFull.setYearEndInt(yearEndInt);
            wbBookYearFull.setShortName(shortName);
            wbBookYearFull.setLongName(longName);
            wbBookYearFull.setStartDate(startDate);
            wbBookYearFull.setEndDate(endDate);
            wbBookYearFull.setWbBookYearStatus(WbBookYearStatus.OPEN);

            return wbBookYearFull;
        } catch (ParseException parseException) {
            throw new WinbooksException(WinbooksError.UNKNOWN_ERROR, parseException);
        }
    }

    public Integer readIndex(String bookYearStr) {
        int index = Integer.parseInt(bookYearStr, 16);
        return index;
    }

    private int readYearField(DbfRecord dbfRecord, String fieldName) {
        byte[] yearStartBytes = dbfRecord.getBytes(fieldName);
        ByteBuffer byteBuffer = ByteBuffer.wrap(yearStartBytes);
        int invertedBytesInt = byteBuffer.getInt();
        int yearStartInt = Integer.reverseBytes(invertedBytesInt);
        return yearStartInt;
    }

    private static LocalDate convertDateToLocalDate(Date date) {
        if (date == null) {
            return null;
        }
        LocalDateTime localDateTime = convertDateToLocalDateTime(date);
        LocalDate localDate = localDateTime.toLocalDate();
        return localDate;
    }

    private static LocalDateTime convertDateToLocalDateTime(Date date) {
        if (date == null) {
            return null;
        }
        Instant instant = date.toInstant();
        ZoneId zoneId = ZoneId.systemDefault();
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, zoneId);
        return localDateTime;
    }

}

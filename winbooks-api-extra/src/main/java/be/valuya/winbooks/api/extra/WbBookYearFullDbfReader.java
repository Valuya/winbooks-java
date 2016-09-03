package be.valuya.winbooks.api.extra;

import be.valuya.jbooks.model.WbBookYearFull;
import be.valuya.winbooks.domain.error.WinbooksError;
import be.valuya.winbooks.domain.error.WinbooksException;
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
            int bookYearIndex = Integer.valueOf(bookYearStr);

            String periodsStr = dbfRecord.getString("PERIODS");
            int periods = Integer.valueOf(periodsStr);

            String yearBeginStr = dbfRecord.getString("YEAR_BEGIN");
            int yearStartInt = Integer.valueOf(yearBeginStr);

            String yearEndStr = dbfRecord.getString("YEAR_END");
            int yearEndInt = Integer.valueOf(yearEndStr);

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

            return wbBookYearFull;
        } catch (ParseException parseException) {
            throw new WinbooksException(WinbooksError.UNKNOWN_ERROR, parseException);
        }
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

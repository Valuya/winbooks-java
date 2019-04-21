package be.valuya.winbooks.api.accountingtroll.converter;

import be.valuya.accountingtroll.domain.ATBookYear;
import be.valuya.jbooks.model.WbBookYearFull;

import java.time.LocalDate;

public class ATBookYearConverter {

    public ATBookYear convertToTrollBookYear(WbBookYearFull wbYear) {
        String shortName = wbYear.getShortName();
        LocalDate startDate = wbYear.getStartDate();
        LocalDate endDate = wbYear.getEndDate();

        ATBookYear bookYear = new ATBookYear();
        bookYear.setName(shortName);
        bookYear.setStartDate(startDate);
        bookYear.setEndDate(endDate);
        return bookYear;
    }
}

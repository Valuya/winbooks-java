package be.valuya.winbooks.api.extra.reader;

import be.valuya.jbooks.model.WbBookYearFull;
import be.valuya.jbooks.model.WbPeriod;

import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PeriodResolver {

    private Map<Integer, WbBookYearFull> wbBookYearFullMap;
    private Map<WbBookYearFull, Map<Integer, WbPeriod>> wbBookYearPeriodFullMap;

    private boolean resolveUnmappedPeriodsFromDates;

    public PeriodResolver(boolean resolveUnmappedPeriodsFromDates) {
        this.resolveUnmappedPeriodsFromDates = resolveUnmappedPeriodsFromDates;
    }

    public void init(List<WbBookYearFull> wbBookYearFullList) {
        wbBookYearFullMap = wbBookYearFullList.stream()
                .collect(Collectors.toMap(WbBookYearFull::getIndex, Function.identity()));

        wbBookYearPeriodFullMap = new HashMap<>();
        for (WbBookYearFull wbBookYearFull : wbBookYearFullList) {
            List<WbPeriod> periodList = wbBookYearFull.getPeriodList();
            for (WbPeriod wbPeriod : periodList) {
                Map<Integer, WbPeriod> wbPeriodMap = wbBookYearPeriodFullMap.computeIfAbsent(wbBookYearFull, missingWbBookYearFullKey -> new HashMap<>());
                int periodIndex = wbPeriod.getIndex();
                wbPeriodMap.put(periodIndex, wbPeriod);
            }
        }
    }

    public WbBookYearFull findWbBookYearFull(int bookYearInt) {
        return wbBookYearFullMap.get(bookYearInt);
    }

    public WbPeriod findWbPeriod(WbBookYearFull wbBookYearFull, int periodIndex, Optional<Date> dateOptional) {
        if (wbBookYearFull == null) {
            if (resolveUnmappedPeriodsFromDates && dateOptional.isPresent()) {
                Date date = dateOptional.get();
                return this.findPeriodFromDate(date)
                        .orElse(null);
            } else {
                return null;
            }
        } else {
            Map<Integer, WbPeriod> periodMap = wbBookYearPeriodFullMap.get(wbBookYearFull);
            return periodMap.get(periodIndex);
        }
    }

    private Optional<WbPeriod> findPeriodFromDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;

        Optional<WbPeriod> firstPeriodOptional = wbBookYearFullMap.values().stream()
                .findFirst()
                .flatMap(bookYear -> bookYear.getPeriodList().stream().findFirst());
        Optional<WbPeriod> foundPeriodOptionlal = wbBookYearFullMap.values()
                .stream()
                .filter(bookYear -> this.dateMatchBookYear(bookYear, year, month))
                .map(bookyear -> this.findBookYearPeriodOptional(bookyear, year, month))
                .flatMap(this::streamOptional)
                .findAny();
        if (foundPeriodOptionlal.isPresent()) {
            return foundPeriodOptionlal;
        } else {
            return firstPeriodOptional;
        }
    }


    private boolean dateMatchBookYear(WbBookYearFull bookYear, int year, int month) {
        LocalDate testDate = LocalDate.of(year, month, 1);
        LocalDate startDate = bookYear.getStartDate();
        LocalDate endDate = bookYear.getEndDate();
        return !testDate.isBefore(startDate) && testDate.isBefore(endDate);
    }


    private Optional<WbPeriod> findBookYearPeriodOptional(WbBookYearFull bookyear, int year, int month) {
        return bookyear.getPeriodList().stream()
                .filter(wbPeriod -> this.dateMatchPeriod(wbPeriod, year, month))
                .findAny();
    }

    private boolean dateMatchPeriod(WbPeriod wbPeriod, int year, int month) {
        LocalDate periodStartDate = wbPeriod.getStartDate();
        int periodMonth = periodStartDate.getMonthValue();
        int periodYear = periodStartDate.getYear();
        return periodMonth == month && periodYear == year;
    }


    private <T> Stream<T> streamOptional(Optional<T> optionalValue) {
        return Stream.of(optionalValue.orElse(null))
                .filter(Objects::nonNull);
    }

}

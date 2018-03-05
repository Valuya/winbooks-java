package be.valuya.winbooks.api.extra;

import be.valuya.jbooks.model.WbBookYearFull;
import be.valuya.jbooks.model.WbPeriod;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class PeriodResolver {

    private Map<Integer, WbBookYearFull> wbBookYearFullMap;
    private Map<WbBookYearFull, Map<Integer, WbPeriod>> wbBookYearPeriodFullMap;

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

    public WbPeriod findWbPeriod(WbBookYearFull wbBookYearFull, int periodIndex) {
        if (wbBookYearFull == null) {
            return null;
        }
        Map<Integer, WbPeriod> periodMap = wbBookYearPeriodFullMap.get(wbBookYearFull);
        return periodMap.get(periodIndex);
    }
}

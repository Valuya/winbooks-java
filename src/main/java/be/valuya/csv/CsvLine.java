package be.valuya.csv;

import java.util.List;

/**
 *
 * @author Yannick Majoros <yannick@valuya.be>
 */
public class CsvLine {

    private List<String> csvValues;

    public List<String> getCsvValues() {
        return csvValues;
    }

    public void setCsvValues(List<String> csvValues) {
        this.csvValues = csvValues;
    }
}

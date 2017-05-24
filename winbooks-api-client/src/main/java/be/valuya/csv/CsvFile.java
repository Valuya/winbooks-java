package be.valuya.csv;

import java.util.List;

public class CsvFile {

    public CsvHeader csvHeader;
    public List<CsvLine> csvLines;

    public CsvHeader getCsvHeader() {
        return csvHeader;
    }

    public void setCsvHeader(CsvHeader csvHeader) {
        this.csvHeader = csvHeader;
    }

    public List<CsvLine> getCsvLines() {
        return csvLines;
    }

    public void setCsvLines(List<CsvLine> csvLines) {
        this.csvLines = csvLines;
    }
}

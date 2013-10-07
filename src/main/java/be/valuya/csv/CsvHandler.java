package be.valuya.csv;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.Format;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Yannick Majoros <yannick@valuya.be>
 */
public class CsvHandler {

    private final CsvFile csvFile;
    private String separator = ",";
    private Charset charset = Charset.forName("iso-8859-1");
    private final Map<String, Integer> headerIndexMap = new HashMap<>();
    private boolean writeHeaders = true;

    public CsvHandler() {
        CsvHeader csvHeader = new CsvHeader();
        List<String> csvHeaderValues = new ArrayList<>();
        csvHeader.setCsvValues(csvHeaderValues);

        List<CsvLine> csvLines = new ArrayList<>();

        csvFile = new CsvFile();
        csvFile.setCsvHeader(csvHeader);
        csvFile.setCsvLines(csvLines);
    }

    public CsvHandler(CsvFile csvFile) {
        this.csvFile = csvFile;
    }

    public CsvLine addCsvLine() {
        CsvLine csvLine = new CsvLine();
        List<String> csvValues = new ArrayList<>();
        csvLine.setCsvValues(csvValues);
        List<CsvLine> csvLines = csvFile.getCsvLines();
        csvLines.add(csvLine);
        return csvLine;
    }

    public void addHeader(String header) {
        CsvHeader csvHeader = csvFile.getCsvHeader();
        List<String> headers = csvHeader.getCsvValues();
        int index = headers.size();
        headers.add(header);
        headerIndexMap.put(header, index);
    }

    public void addValue(String valueStr) {
        List<String> lastLineCsvValues = getLastLineValues();
        lastLineCsvValues.add(valueStr);
    }

    public <T> void addValue(Format format, T value) {
        String valueStr = format.format(value);
        addValue(valueStr);
    }

    public void putValue(String header, String value) {
        if (!headerIndexMap.containsKey(header)) {
            // header not found, add it
            addHeader(header);
        }
        int columnIndex = headerIndexMap.get(header);
        List<String> lastLineCsvValues = getLastLineValues();
        int lastLineSize = lastLineCsvValues.size();
        while (lastLineSize <= columnIndex) {
            // add missing columns to last line
            lastLineCsvValues.add(null);
            lastLineSize++;
        }
        lastLineCsvValues.set(columnIndex, value);
    }

    public <T> void putValue(String header, Format format, T value) {
        String valueStr;
        if (value == null) {
            valueStr = null;
        } else {
            valueStr = format.format(value);
        }
        putValue(header, valueStr);
    }

    public List<List<String>> renderAllCsvValueList() {
        List<List<String>> renderedLines = new ArrayList<>();
        List<CsvLine> csvLines = csvFile.getCsvLines();

        CsvHeader csvHeader = csvFile.getCsvHeader();
        List<String> headers = csvHeader.getCsvValues();

        if (writeHeaders) {
            renderedLines.add(headers);
        }

        for (CsvLine csvLine : csvLines) {
            List<String> csvValues = csvLine.getCsvValues();
            renderedLines.add(csvValues);
        }

        return renderedLines;
    }

    public List<String> renderLines() {
        List<List<String>> allCsvValueList = renderAllCsvValueList();
        List<String> renderedLines = new ArrayList<>();
        for (List<String> csvValueList : allCsvValueList) {
            String csvLine = renderLine(csvValueList);
            renderedLines.add(csvLine);
        }

        return renderedLines;
    }

    public String renderLine(List<String> csvValueList) {
        StringBuilder stringBuilder = new StringBuilder();
        boolean first = true;
        for (String string : csvValueList) {
            if (first) {
                first = false;
            } else {
                stringBuilder.append(separator);
            }
            String str;
            if (string != null) {
                str = string;
            } else {
                str = "";
            }
            String escapedString = escape(str);
            stringBuilder.append(escapedString);
        }
        String renderedLine = stringBuilder.toString();
        return renderedLine;
    }

    public String escape(String string) {
        return "\"" + string + "\"";
    }

    public void dumpToFile(Path path) throws IOException {
        List<String> lines = renderLines();
        try (BufferedWriter bufferedWriter = Files.newBufferedWriter(path, charset)) {
            for (String line : lines) {
                bufferedWriter.write(line);
                bufferedWriter.write("\r\n");
            }
        }
    }

    private CsvLine getLastCsvLine() {
        List<CsvLine> csvLines = csvFile.getCsvLines();
        int lastIndex = csvLines.size() - 1;
        CsvLine lastLine = csvLines.get(lastIndex);
        return lastLine;
    }

    private List<String> getLastLineValues() {
        CsvLine lastLine = getLastCsvLine();
        List<String> lastLineCsvValues = lastLine.getCsvValues();
        return lastLineCsvValues;
    }

    public Charset getCharset() {
        return charset;
    }

    public void setCharset(Charset charset) {
        this.charset = charset;
    }

    public String getSeparator() {
        return separator;
    }

    public void setSeparator(String separator) {
        this.separator = separator;
    }

    public boolean isWriteHeaders() {
        return writeHeaders;
    }

    public void setWriteHeaders(boolean writeHeaders) {
        this.writeHeaders = writeHeaders;
    }
}
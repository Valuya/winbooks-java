package be.valuya.winbooks.api.extra;

import be.valuya.jbooks.model.WbAccount;
import be.valuya.jbooks.model.WbEntry;
import be.valuya.winbooks.domain.error.WinbooksError;
import be.valuya.winbooks.domain.error.WinbooksException;
import com.sun.nio.file.ExtendedOpenOption;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.text.MessageFormat;
import java.text.ParseException;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import net.iryndin.jdbf.core.DbfRecord;

public class WinbooksExtraService {

    public Stream<WbEntry> streamAct(WinbooksFileConfiguration winbooksFileConfiguration) {
        return streamTable(winbooksFileConfiguration, "ACT", new WbEntryDbfReader()::readWbEntryFromActDbfRecord);
    }

    public Stream<WbEntry> streamAct(InputStream inputStream, Charset charset) {
        return DbfUtils.streamDbf(inputStream, charset)
                .map(new WbEntryDbfReader()::readWbEntryFromActDbfRecord);
    }

    public Stream<WbAccount> streamAcf(WinbooksFileConfiguration winbooksFileConfiguration) {
        return streamTable(winbooksFileConfiguration, "ACF", new WbAccountDbfReader()::readWbAccountFromAcfDbfRecord);
    }

    public Stream<WbAccount> streamAcf(InputStream inputStream, Charset charset) {
        return DbfUtils.streamDbf(inputStream, charset)
                .map(new WbAccountDbfReader()::readWbAccountFromAcfDbfRecord);
    }

    public <T> Stream<T> streamTable(WinbooksFileConfiguration winbooksFileConfiguration, String tableName, Function<DbfRecord, T> readFunction) {
        InputStream tableInputStream = getTableInputStream(winbooksFileConfiguration, tableName);
        Charset charset = winbooksFileConfiguration.getCharset();
        return DbfUtils.streamDbf(tableInputStream, charset)
                .onClose(() -> closeInputStream(tableInputStream))
                .map(readFunction);
    }

    public void dumpDbf(WinbooksFileConfiguration winbooksFileConfiguration, String tableName) {
        try (Stream<DbfRecord> streamTable = streamTable(winbooksFileConfiguration, tableName, Function.identity())) {
            streamTable.forEach(this::dumpDbfRecord);
        }
    }

    private InputStream getTableInputStream(WinbooksFileConfiguration winbooksFileConfiguration, String tableName) {
        try {
            Path path = resolveTablePath(winbooksFileConfiguration, tableName);
            return Files.newInputStream(path);
        } catch (IOException exception) {
            throw new WinbooksException(WinbooksError.UNKNOWN_ERROR, exception);
        }
    }

    private Path resolveTablePath(WinbooksFileConfiguration winbooksFileConfiguration, String tableName) {
        try {
            Path baseFolderPath = winbooksFileConfiguration.getBaseFolderPath();
            String baseName = winbooksFileConfiguration.getBaseName();
            String fileName = baseName + "_" + tableName + ".dbf";
            Path tablePath = Files.find(baseFolderPath, 1,
                    (path, attr) -> path.getFileName().toString().equalsIgnoreCase(fileName))
                    .findFirst()
                    .orElseThrow(() -> {
                        String message = MessageFormat.format("Could not find file {0} in folder {1}", fileName, baseFolderPath.toString());
                        return new WinbooksException(WinbooksError.DOSSIER_NOT_FOUND, message);
                    });
            return tablePath;
        } catch (IOException exception) {
            throw new WinbooksException(WinbooksError.UNKNOWN_ERROR, exception);
        }
    }

    private void dumpDbfRecord(DbfRecord dbfRecord) {
        try {
            int recordNumber = dbfRecord.getRecordNumber();
            Map<String, Object> valueMap = dbfRecord.toMap();

            System.out.println("Record #" + recordNumber + ": " + valueMap);
        } catch (ParseException parseException) {
            throw new WinbooksException(WinbooksError.UNKNOWN_ERROR, parseException);
        }
    }

    private void closeInputStream(InputStream tableInputStream) {
        try {
            tableInputStream.close();
        } catch (IOException exception) {
            throw new WinbooksException(WinbooksError.UNKNOWN_ERROR, exception);
        }
    }

}

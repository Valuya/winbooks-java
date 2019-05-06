package be.valuya.winbooks.api.extra;

import be.valuya.jbooks.model.WbBookYearFull;
import be.valuya.jbooks.model.WbDocument;
import be.valuya.jbooks.model.WbPeriod;
import be.valuya.winbooks.api.extra.config.WinbooksFileConfiguration;
import be.valuya.winbooks.domain.error.WinbooksError;
import be.valuya.winbooks.domain.error.WinbooksException;
import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.PdfCopyFields;
import com.lowagie.text.pdf.PdfReader;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.Optional;
import java.util.function.Function;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

class WinbooksDocumentsService {
    private static Logger LOGGER = Logger.getLogger(WinbooksDocumentsService.class.getName());

    // Documents path: /<book year>/<dbk>/<file_name>
    private static final Pattern BOOK_YEAR_DOCUMENT_FILENAME_PATTERN = Pattern.compile("^(\\w+)_(\\d+)_(\\d+)_(\\d+).pdf$");


    Stream<WbDocument> streamBookYearDocuments(WinbooksFileConfiguration fileConfiguration, WbBookYearFull bookYear) {
        String bookYearName = bookYear.getShortName();
        boolean resolveCaseInsensitiveSiblings = fileConfiguration.isResolveCaseInsensitiveSiblings();
        boolean resolveDocumentTimes = fileConfiguration.isResolveDocumentTimes();

        // Stream documents at /${basePath}/${documentsPath}/${bookYearName}/<book year doc format>
        return WinbooksPathUtils.getBookYearBasePath(fileConfiguration, bookYear)
                .flatMap(this::resolveDocumentsPath)
                .flatMap(documentsPath -> WinbooksPathUtils.resolvePath(documentsPath, bookYearName, resolveCaseInsensitiveSiblings))
                .map(root -> this.streamBookYearDocuments(root, bookYear, resolveDocumentTimes))
                .orElseGet(Stream::empty);
    }

    Optional<byte[]> getDocumentData(WinbooksFileConfiguration fileConfiguration, WbDocument document) {
        boolean resolveCaseInsensitiveSiblings = fileConfiguration.isResolveCaseInsensitiveSiblings();
        return getDocumentAbsolutePath(fileConfiguration, document)
                .flatMap(docPath -> getDocumentAllPagesPdfContent(docPath, document, resolveCaseInsensitiveSiblings));
    }

    private Stream<WbDocument> streamBookYearDocuments(Path bookYearDocumentFolderPath, WbBookYearFull bookYear, boolean resolveAccessTimes) {
        try {
            return Files.list(bookYearDocumentFolderPath)
                    .flatMap(bookYearDbkPath -> streamDbkBookYearDocuments(bookYearDbkPath, bookYear, resolveAccessTimes));
        } catch (IOException e) {
            return Stream.empty();
        }
    }

    private Stream<WbDocument> streamDbkBookYearDocuments(Path path, WbBookYearFull bookYear, boolean resolveAccessTime) {
        Collector<WbDocument, ?, Optional<WbDocument>> maxPageNrComparator = Collectors.maxBy(Comparator.comparing(WbDocument::getPageCount));
        return WinbooksPathUtils.streamDirectoryFiles(path, this::isDocument)
                .map(documentPath -> getDocumentOptional(documentPath, bookYear, resolveAccessTime))
                .flatMap(this::streamOptional)
                .collect(Collectors.groupingBy(Function.identity(), maxPageNrComparator))
                .values()
                .stream()
                .flatMap(this::streamOptional)
                .map(this::getPageNumberDocument);
    }


    private WbDocument getPageNumberDocument(WbDocument pageIndexDocument) {
        WbPeriod wbPeriod = pageIndexDocument.getWbPeriod();
        int pageCount = pageIndexDocument.getPageCount() + 1;
        String documentNumber = pageIndexDocument.getDocumentNumber();
        LocalDateTime creationTime = pageIndexDocument.getCreationTime();
        LocalDateTime updatedTime = pageIndexDocument.getUpdatedTime();
        String dbkCode = pageIndexDocument.getDbkCode();

        WbDocument pageNumberDocument = new WbDocument();
        pageNumberDocument.setWbPeriod(wbPeriod);
        pageNumberDocument.setPageCount(pageCount);
        pageNumberDocument.setDocumentNumber(documentNumber);
        pageNumberDocument.setCreationTime(creationTime);
        pageNumberDocument.setUpdatedTime(updatedTime);
        pageNumberDocument.setDbkCode(dbkCode);

        return pageNumberDocument;
    }


    private Optional<Path> resolveDocumentsPath(Path basePath) {
        String defaultDocumentPathName = "Document";
        Path defaultDocumentsPath = basePath.resolve(defaultDocumentPathName);
        return Stream.of(
                defaultDocumentsPath,
                basePath.resolve("document"),
                basePath.resolve("DOCUMENT")
        )
                .filter(Files::exists)
                .findAny();
    }

    private <T> Stream<T> streamOptional(Optional<T> optional) {
        return optional.map(Stream::of)
                .orElseGet(Stream::empty);
    }

    private boolean isDocument(Path documentPath) {
        String fileName = documentPath.getFileName().toString();
        Matcher matcher = BOOK_YEAR_DOCUMENT_FILENAME_PATTERN.matcher(fileName);
        return matcher.matches();
    }


    private Optional<WbDocument> getDocumentOptional(Path documentPath, WbBookYearFull bookYear, boolean resolveAccessTimes) {
        String fileName = documentPath.getFileName().toString();
        Matcher matcher = BOOK_YEAR_DOCUMENT_FILENAME_PATTERN.matcher(fileName);
        if (!matcher.matches()) {
            return Optional.empty();
        }

        String actualDbkCode = matcher.group(1);
        String periodName = matcher.group(2);
        String documentNumber = matcher.group(3);
        String pageNrStr = matcher.group(4);
        int pageNr = Integer.valueOf(pageNrStr);

        WbDocument wbDocument = new WbDocument();
        wbDocument.setDbkCode(actualDbkCode);
        wbDocument.setDocumentNumber(documentNumber);
        wbDocument.setPageCount(pageNr);
        wbDocument.setWbPeriod(getWbPeriod(bookYear, periodName));

        if (resolveAccessTimes) {
            long time0 = System.currentTimeMillis();
            LocalDateTime lastModifiedLocalTime = WinbooksPathUtils.getLastModifiedTime(documentPath);
            LocalDateTime creationTime = WinbooksPathUtils.getCreationTime(documentPath);
            long time1 = System.currentTimeMillis();
            long totalTIme = time1 - time0;
            LOGGER.finer("*** file times(" + documentPath.toString() + ": " + totalTIme + "ms");

            wbDocument.setUpdatedTime(lastModifiedLocalTime);
            wbDocument.setCreationTime(creationTime);
        }

        return Optional.of(wbDocument);
    }

    private WbPeriod getWbPeriod(WbBookYearFull bookYear, String periodName) {
        return bookYear.getPeriodList()
                .stream()
                .filter(wbPeriod -> isPeriodIndex(wbPeriod, periodName))
                .findFirst()
                .orElseThrow(() -> new WinbooksException(WinbooksError.NO_PERIOD, "Period not found: " + periodName));
    }

    private boolean isPeriodIndex(WbPeriod wbPeriod, String expectedPeriodName) {
        int periodIndex = wbPeriod.getIndex();
        String periodIndexName = String.format("%02d", periodIndex);
        return expectedPeriodName.equals(periodIndexName);
    }


    private Optional<Path> getDocumentAbsolutePath(WinbooksFileConfiguration fileConfiguration, WbDocument document) {
        boolean resolveCaseInsensitiveSiblings = fileConfiguration.isResolveCaseInsensitiveSiblings();
        WbBookYearFull bookYearFull = document.getWbPeriod().getWbBookYearFull();
        return WinbooksPathUtils.getBookYearBasePath(fileConfiguration, bookYearFull)
                .flatMap(this::resolveDocumentsPath)
                .map(documentsPath -> resolveDocumentDirectoryPath(documentsPath, document, resolveCaseInsensitiveSiblings));
    }

    private Path resolveDocumentDirectoryPath(Path baseDocumentPath, WbDocument document, boolean resolveCaseInsensitiveSiblings) {
        WbBookYearFull wbBookYearFull = document.getWbPeriod().getWbBookYearFull();
        String bookYearShortName = wbBookYearFull.getShortName();
        String dbCode = document.getDbkCode();
        return WinbooksPathUtils.resolvePath(baseDocumentPath, bookYearShortName, resolveCaseInsensitiveSiblings)
                .flatMap(bookYearPath -> WinbooksPathUtils.resolvePath(bookYearPath, dbCode, resolveCaseInsensitiveSiblings))
                .orElseGet(() -> baseDocumentPath.resolve(bookYearShortName).resolve(dbCode));
    }


    private Optional<byte[]> getDocumentAllPagesPdfContent(Path documentPath, WbDocument document, boolean resolveCaseInsitiveSiblings) {
        return streamDocumentPagesPaths(documentPath, document, resolveCaseInsitiveSiblings)
                .map(this::readAllBytes)
                .reduce(this::mergePdf);
    }

    private Stream<Path> streamDocumentPagesPaths(Path basePath, WbDocument document, boolean resolveCaseInsensitiveSiblings) {
        int pageCount = document.getPageCount();

        return IntStream.range(0, pageCount)
                .mapToObj(pageIndex -> getDocumentPagePathName(pageIndex, document))
                .map(pagePathName -> WinbooksPathUtils.resolvePath(basePath, pagePathName, resolveCaseInsensitiveSiblings))
                .flatMap(this::streamOptional);
    }


    private String getDocumentPagePathName(int pageIndex, WbDocument document) {
        WbPeriod wbPeriod = document.getWbPeriod();
        int wbPeriodIndex = wbPeriod.getIndex();
        String periodIndexName = String.format("%02d", wbPeriodIndex);
        String pageIndexName = String.format("%02d", pageIndex);

        String fileName = MessageFormat.format("{0}_{1}_{2}_{3}.pdf",
                document.getDbkCode(),
                periodIndexName,
                document.getDocumentNumber(),
                pageIndexName
        );
        return fileName;
    }


    private byte[] readAllBytes(Path path) {
        try {
            return Files.readAllBytes(path);
        } catch (IOException exception) {
            throw new WinbooksException(WinbooksError.USER_FILE_ERROR, exception);
        }
    }

    private byte[] mergePdf(byte[] pdfData1, byte[] pdfData2) {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            PdfCopyFields pdfCopyFields = new PdfCopyFields(byteArrayOutputStream);

            PdfReader pdfReader1 = new PdfReader(pdfData1);
            pdfCopyFields.addDocument(pdfReader1);

            PdfReader pdfReader2 = new PdfReader(pdfData2);
            pdfCopyFields.addDocument(pdfReader2);

            pdfCopyFields.close();
            pdfReader1.close();
            pdfReader2.close();

            return byteArrayOutputStream.toByteArray();
        } catch (IOException | DocumentException exception) {
            throw new WinbooksException(WinbooksError.USER_FILE_ERROR, "Error while processing pdf files", exception);
        }
    }


}

package be.valuya.winbooks.api.extra.config;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Optional;

public class WinbooksFileConfiguration {

    private String username;
    private String password;
    private Path baseFolderPath;
    private String baseName;
    private Charset charset = StandardCharsets.ISO_8859_1;

    private boolean ignoreConversionErrors = true;
    private boolean ignoreMissingArchives = true;
    private boolean readTablesToMemory = true;
    private boolean resolveArchivedBookYears = true;
    private boolean resolveCaseInsensitiveSiblings = true;
    private boolean resolveUnmappedPeriodFromEntryDate = true;
    private boolean resolveDocumentTimes = true;
    private DocumentMatchingMode documentMatchingMode = DocumentMatchingMode.EAGERLY_CACHE_ALL_DOCUMENTS;
    private Optional<LocalDate> bookYearStartMinDateOptional = Optional.empty();
    private Optional<LocalDate> bookYearStartMaxDateOptional = Optional.empty();

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Path getBaseFolderPath() {
        return baseFolderPath;
    }

    public void setBaseFolderPath(Path baseFolderPath) {
        this.baseFolderPath = baseFolderPath;
    }

    public String getBaseName() {
        return baseName;
    }

    public void setBaseName(String baseName) {
        this.baseName = baseName;
    }

    public Charset getCharset() {
        return charset;
    }

    public void setCharset(Charset charset) {
        this.charset = charset;
    }

    public boolean isIgnoreMissingArchives() {
        return ignoreMissingArchives;
    }

    public void setIgnoreMissingArchives(boolean ignoreMissingArchives) {
        this.ignoreMissingArchives = ignoreMissingArchives;
    }

    public boolean isReadTablesToMemory() {
        return readTablesToMemory;
    }

    public void setReadTablesToMemory(boolean readTablesToMemory) {
        this.readTablesToMemory = readTablesToMemory;
    }

    public boolean isResolveArchivedBookYears() {
        return resolveArchivedBookYears;
    }

    public void setResolveArchivedBookYears(boolean resolveArchivedBookYears) {
        this.resolveArchivedBookYears = resolveArchivedBookYears;
    }

    public boolean isResolveCaseInsensitiveSiblings() {
        return resolveCaseInsensitiveSiblings;
    }

    public void setResolveCaseInsensitiveSiblings(boolean resolveCaseInsensitiveSiblings) {
        this.resolveCaseInsensitiveSiblings = resolveCaseInsensitiveSiblings;
    }

    public boolean isResolveUnmappedPeriodFromEntryDate() {
        return resolveUnmappedPeriodFromEntryDate;
    }

    public void setResolveUnmappedPeriodFromEntryDate(boolean resolveUnmappedPeriodFromEntryDate) {
        this.resolveUnmappedPeriodFromEntryDate = resolveUnmappedPeriodFromEntryDate;
    }

    public DocumentMatchingMode getDocumentMatchingMode() {
        return documentMatchingMode;
    }

    public void setDocumentMatchingMode(DocumentMatchingMode documentMatchingMode) {
        this.documentMatchingMode = documentMatchingMode;
    }

    public boolean isResolveDocumentTimes() {
        return resolveDocumentTimes;
    }

    public void setResolveDocumentTimes(boolean resolveDocumentTimes) {
        this.resolveDocumentTimes = resolveDocumentTimes;
    }

    public Optional<LocalDate> getBookYearStartMinDateOptional() {
        return bookYearStartMinDateOptional;
    }

    public void setBookYearStartMinDate(LocalDate bookYearStartMinLoclDate) {
        this.bookYearStartMinDateOptional = Optional.of(bookYearStartMinLoclDate);
    }

    public Optional<LocalDate> getBookYearStartMaxDateOptional() {
        return bookYearStartMaxDateOptional;
    }

    public void setBookYearStartMaxDate(LocalDate bookYearStartMaxLoclDate) {
        this.bookYearStartMaxDateOptional = Optional.of(bookYearStartMaxLoclDate);
    }

    public boolean isIgnoreConversionErrors() {
        return ignoreConversionErrors;
    }

    public void setIgnoreConversionErrors(boolean ignoreConversionErrors) {
        this.ignoreConversionErrors = ignoreConversionErrors;
    }
}

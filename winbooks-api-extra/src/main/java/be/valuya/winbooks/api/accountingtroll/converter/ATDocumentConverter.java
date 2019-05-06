package be.valuya.winbooks.api.accountingtroll.converter;

import be.valuya.accountingtroll.domain.ATBookPeriod;
import be.valuya.accountingtroll.domain.ATBookYear;
import be.valuya.accountingtroll.domain.ATDocument;
import be.valuya.jbooks.model.WbBookYearFull;
import be.valuya.jbooks.model.WbDocument;
import be.valuya.jbooks.model.WbPeriod;
import be.valuya.winbooks.api.accountingtroll.cache.AccountingManagerCache;
import be.valuya.winbooks.domain.error.WinbooksError;
import be.valuya.winbooks.domain.error.WinbooksException;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ATDocumentConverter {

    private final AccountingManagerCache accountingManagerCache;

    public ATDocumentConverter(AccountingManagerCache accountingManagerCache) {
        this.accountingManagerCache = accountingManagerCache;
    }

    public ATDocument convertDocument(WbDocument wbDocument) {
        String documentNumber = wbDocument.getDocumentNumber();
        String dbkCode = wbDocument.getDbkCode();
        WbPeriod wbPeriod = wbDocument.getWbPeriod();
        int pageCount = wbDocument.getPageCount();
        LocalDateTime creationTime = wbDocument.getCreationTime();
        LocalDateTime updatedTime = wbDocument.getUpdatedTime();

        ATBookPeriod bookPeriod = accountingManagerCache.getCachedBookPeriodOrThrow(wbPeriod);
        String documentId = this.createDocumentId(dbkCode, bookPeriod, documentNumber);

        ATDocument atDocument = new ATDocument();
        atDocument.setId(documentId);
        atDocument.setDocumentNumnber(documentNumber);
        atDocument.setDbkCode(dbkCode);
        atDocument.setBookPeriod(bookPeriod);
        atDocument.setPageCountOptional(Optional.of(pageCount));
        atDocument.setCreationTimeOptional(Optional.ofNullable(creationTime));
        atDocument.setUpdateTimeOptional(Optional.ofNullable(updatedTime));
        return atDocument;
    }

    private String createDocumentId(String dbkCode, ATBookPeriod bookPeriod, String documentNumber) {
        String bookYearName = bookPeriod.getBookYear().getName();
        String periodName = bookPeriod.getName();
        String documentId = MessageFormat.format("wbdoc-{0}-{1}-{2}-{3}",
                dbkCode, bookYearName, periodName, documentNumber
        );
        return documentId;
    }

    public WbDocument convertWbDocument(ATDocument atDocument) {
        ATBookPeriod bookPeriod = atDocument.getBookPeriod();
        String dbkCode = atDocument.getDbkCode();
        String documentNumber = atDocument.getDocumentNumnber();
        Optional<Integer> pageCountOptional = atDocument.getPageCountOptional();
        Optional<LocalDateTime> creationTimeOptional = atDocument.getCreationTimeOptional();
        Optional<LocalDateTime> updateTimeOptional = atDocument.getUpdateTimeOptional();

        List<WbBookYearFull> wbBookYearFulls = accountingManagerCache.streamWbBookYearFulls()
                .collect(Collectors.toList());
        WbPeriod wbPeriod = this.findWbPeriod(wbBookYearFulls, bookPeriod);

        WbDocument wbDocument = new WbDocument();
        wbDocument.setDbkCode(documentNumber);
        wbDocument.setDbkCode(dbkCode);
        wbDocument.setWbPeriod(wbPeriod);
        wbDocument.setUpdatedTime(updateTimeOptional.orElse(null));
        wbDocument.setCreationTime(creationTimeOptional.orElse(null));
        wbDocument.setPageCount(pageCountOptional.orElse(0));
        return wbDocument;
    }

    private WbPeriod findWbPeriod(List<WbBookYearFull> wbBookYearFulls, ATBookPeriod bookPeriod) {
        return wbBookYearFulls.stream()
                .map(WbBookYearFull::getPeriodList)
                .flatMap(List::stream)
                .filter(wbPeriod -> this.isSamePeriod(wbPeriod, bookPeriod))
                .findAny()
                .orElseThrow(() -> new WinbooksException(WinbooksError.FATAL_ERRORS, "No period found"));
    }

    private boolean isSamePeriod(WbPeriod wbPeriod, ATBookPeriod bookPeriod) {
        WbBookYearFull wbBookYearFull = wbPeriod.getWbBookYearFull();
        String bookYearFullShortName = wbBookYearFull.getShortName();
        String wbPeriodShortName = wbPeriod.getShortName();

        ATBookYear bookYear = bookPeriod.getBookYear();
        String bookYearName = bookYear.getName();
        String periodName = bookPeriod.getName();

        return bookYearFullShortName.equals(bookYearName)
                && wbPeriodShortName.equals(periodName);
    }
}

package be.valuya.winbooks.api.accountingtroll;

import be.valuya.accountingtroll.TrollAccount;
import be.valuya.accountingtroll.TrollAccountingEntry;
import be.valuya.accountingtroll.TrollPeriod;
import be.valuya.accountingtroll.TrollThirdParty;
import be.valuya.jbooks.model.WbEntry;
import be.valuya.winbooks.domain.error.WinbooksError;
import be.valuya.winbooks.domain.error.WinbooksException;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class WinbooksTrollEntry implements TrollAccountingEntry {
    private WbEntry wbEntry;

    public WinbooksTrollEntry(WbEntry wbEntry) {
        this.wbEntry = wbEntry;
    }

    @Override
    public String getAccountCode() {
        return wbEntry.getAccountGl();
    }

    @Override
    public String getRemotePartyName() {
        return wbEntry.getAccountRp();
    }

    @Override
    public String getPeriodName() {
        return wbEntry.getPeriod();
    }

    @Override
    public LocalDate getDate() {
        return getLocalDate(wbEntry.getDate())
                .orElseThrow(() -> new WinbooksException(WinbooksError.UNKNOWN_ERROR, "No date field on accounting entry"));
    }

    @Override
    public BigDecimal getAmountEur() {
        return wbEntry.getAmountEur();
    }

    @Override
    public BigDecimal getVatRate() {
        return wbEntry.getVatBase();
    }

    @Override
    public BigDecimal getAccountBalance() {
        return wbEntry.getCurEurBase();
    }

    @Override
    public Optional<LocalDate> getDocDate() {
        return getLocalDate(wbEntry.getDateDoc());
    }

    @Override
    public Optional<LocalDate> getDueDate() {
        return getLocalDate(wbEntry.getDueDate());
    }

    @Override
    public Optional<String> getComment() {
        String comment = Stream.of(wbEntry.getComment(), wbEntry.getCommentExt())
                .filter(Objects::nonNull)
                .collect(Collectors.joining(" "));
        return Optional.of(comment);
    }

    private Optional<LocalDate> getLocalDate(Date date) {
        if (date == null) {
            return Optional.empty();
        }
        Instant dateInstant = date.toInstant();
        LocalDate localDate = LocalDate.from(dateInstant);
        return Optional.of(localDate);
    }
}

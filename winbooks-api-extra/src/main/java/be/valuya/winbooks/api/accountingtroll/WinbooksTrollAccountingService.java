package be.valuya.winbooks.api.accountingtroll;

import be.valuya.accountingtroll.TrollAccount;
import be.valuya.accountingtroll.TrollAccountingEntry;
import be.valuya.accountingtroll.TrollAccountingService;
import be.valuya.accountingtroll.TrollBookYear;
import be.valuya.accountingtroll.TrollPeriod;
import be.valuya.accountingtroll.TrollSession;
import be.valuya.accountingtroll.TrollThirdParty;
import be.valuya.jbooks.model.WbClientSupplier;
import be.valuya.winbooks.api.extra.WinbooksExtraService;
import be.valuya.winbooks.api.extra.WinbooksSession;
import be.valuya.winbooks.domain.error.WinbooksError;
import be.valuya.winbooks.domain.error.WinbooksException;

import java.util.Optional;
import java.util.stream.Stream;

public class WinbooksTrollAccountingService implements TrollAccountingService {

    private WinbooksExtraService extraService;

    public WinbooksTrollAccountingService() {
        extraService = new WinbooksExtraService();
    }

    @Override
    public Stream<TrollAccount> streamAccounts(TrollSession trollSession) {
        WinbooksSession session = checkSession(trollSession);
        return extraService.streamAcf(session)
                .map(WinbooksTrollAccount::new);
    }

    @Override
    public Stream<TrollBookYear> streamBookYears(TrollSession trollSession) {
        WinbooksSession session = checkSession(trollSession);
        return extraService.streamBookYears(session)
                .map(WinbooksTrollBookYear::new);
    }

    @Override
    public Stream<TrollPeriod> streamPeriods(TrollSession trollSession) {
        WinbooksSession session = checkSession(trollSession);
        return extraService.streamBookYears(session)
                .map(WinbooksTrollBookYear::new)
                .flatMap(WinbooksTrollBookYear::streamPeriods);
    }

    @Override
    public Stream<TrollThirdParty> streamThirdParties(TrollSession trollSession) {
        WinbooksSession session = checkSession(trollSession);
        return extraService.streamCsf(session)
                .filter(this::isValidClientSupplier)
                .map(WinbooksTrollThirdParty::new);
    }

    @Override
    public Stream<TrollAccountingEntry> streamAccountingEntries(TrollSession trollSession) {
        WinbooksSession session = checkSession(trollSession);
        return extraService.streamAct(session)
                .map(WinbooksTrollEntry::new);
    }


    private WinbooksSession checkSession(TrollSession trollSession) {
        if (trollSession.getSessionType().equals(WinbooksSession.SESSION_TYPE)) {
            return (WinbooksSession) trollSession;
        } else {
            throw new WinbooksException(WinbooksError.INVALID_PARAMETER, "Session type mismatch");
        }
    }


    private boolean isValidClientSupplier(WbClientSupplier wbClientSupplier) {
        String nameNullable = wbClientSupplier.getName1();
        return Optional.ofNullable(nameNullable).isPresent();
    }
}

package be.valuya.winbooks.api.accountingtroll;

import be.valuya.accountingtroll.AccountingEventListener;
import be.valuya.accountingtroll.event.ArchiveNotFoundIgnoredEvent;
import be.valuya.accountingtroll.event.BalanceChangeEvent;

public class TestAccountingEventListener implements AccountingEventListener {

    @Override
    public void handleBalanceChangeEvent(BalanceChangeEvent balanceChangeEvent) {
        System.err.println(balanceChangeEvent);
    }

    @Override
    public void handleArchiveNotFoundIgnoredEvent(ArchiveNotFoundIgnoredEvent archiveNotFoundIgnoredEvent) {
        System.err.println(archiveNotFoundIgnoredEvent);
    }
}

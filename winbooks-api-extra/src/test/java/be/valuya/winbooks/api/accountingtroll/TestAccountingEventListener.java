package be.valuya.winbooks.api.accountingtroll;

import be.valuya.accountingtroll.AccountingEventListener;
import be.valuya.accountingtroll.event.ArchiveFileNotFoundIgnoredEvent;
import be.valuya.accountingtroll.event.ArchiveFolderNotFoundIgnoredEvent;
import be.valuya.accountingtroll.event.BalanceChangeEvent;

public class TestAccountingEventListener implements AccountingEventListener {

    @Override
    public void handleBalanceChangeEvent(BalanceChangeEvent balanceChangeEvent) {
        System.err.println(balanceChangeEvent);
    }

    @Override
    public void handleArchiveFileNotFoundIgnoredEvent(ArchiveFileNotFoundIgnoredEvent archiveFileNotFoundIgnoredEvent) {
        System.err.println(archiveFileNotFoundIgnoredEvent);

    }

    @Override
    public void handleArchiveFolderNotFoundIgnoredEvent(ArchiveFolderNotFoundIgnoredEvent archiveFolderNotFoundIgnoredEvent) {
        System.err.println(archiveFolderNotFoundIgnoredEvent);

    }

}

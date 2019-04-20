package be.valuya.winbooks.api.accountingtroll;

import be.valuya.accountingtroll.AccountingEventListener;
import be.valuya.accountingtroll.event.ArchiveFileNotFoundIgnoredEvent;
import be.valuya.accountingtroll.event.ArchiveFolderNotFoundIgnoredEvent;
import be.valuya.accountingtroll.event.BalanceChangeEvent;

public class TestAccountingEventListener implements AccountingEventListener {

    @Override
    public void handleBalanceChangeEvent(BalanceChangeEvent balanceChangeEvent) {
        System.out.print("BALANCE CHANGE ");
        System.out.println(balanceChangeEvent);
    }

    @Override
    public void handleArchiveFileNotFoundIgnoredEvent(ArchiveFileNotFoundIgnoredEvent archiveFileNotFoundIgnoredEvent) {
        System.out.print("ARCHIVE FILE NOT FOUND ");
        System.out.println(archiveFileNotFoundIgnoredEvent);

    }

    @Override
    public void handleArchiveFolderNotFoundIgnoredEvent(ArchiveFolderNotFoundIgnoredEvent archiveFolderNotFoundIgnoredEvent) {
        System.out.print("ARCHIVE DIR NOT FOUND ");
        System.out.println(archiveFolderNotFoundIgnoredEvent);

    }

}

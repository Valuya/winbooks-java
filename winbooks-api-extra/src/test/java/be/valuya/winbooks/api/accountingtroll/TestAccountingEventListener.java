package be.valuya.winbooks.api.accountingtroll;

import be.valuya.accountingtroll.AccountingEventListener;
import be.valuya.accountingtroll.event.AccountBalanceChangeEvent;
import be.valuya.accountingtroll.event.ArchiveFileNotFoundIgnoredEvent;
import be.valuya.accountingtroll.event.ArchiveFolderNotFoundIgnoredEvent;
import be.valuya.accountingtroll.event.ThirdPartyBalanceChangeEvent;

public class TestAccountingEventListener implements AccountingEventListener {


    @Override
    public void handleAccountBalanceChangeEvent(AccountBalanceChangeEvent accountBalanceChangeEvent) {
        System.out.print("ACCOUNT BALANCE CHANGE ");
        System.out.println(accountBalanceChangeEvent);
    }

    @Override
    public void handleThirdPartyBalanceChangeEvent(ThirdPartyBalanceChangeEvent thirdPartyBalanceChangeEvent) {
        System.out.print("THIRD PARTY BALANCE CHANGE ");
        System.out.println(thirdPartyBalanceChangeEvent);
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

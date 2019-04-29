package be.valuya.winbooks.api.accountingtroll;

import be.valuya.accountingtroll.AccountingEventListener;
import be.valuya.accountingtroll.domain.ATAccount;
import be.valuya.accountingtroll.event.ArchiveFileNotFoundIgnoredEvent;
import be.valuya.accountingtroll.event.ArchiveFolderNotFoundIgnoredEvent;
import be.valuya.accountingtroll.event.BalanceChangeEvent;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class BalanceAccountingEventListener implements AccountingEventListener {

    private Map<String, BigDecimal> balanceByAccountCode = new HashMap<>();

    @Override
    public void handleBalanceChangeEvent(BalanceChangeEvent balanceChangeEvent) {
        ATAccount account = balanceChangeEvent.getAccount();
        String code = account.getCode();
        BigDecimal newBalance = balanceChangeEvent.getNewBalance();
        balanceByAccountCode.put(code, newBalance);

//        if (code.equals("609400")) {
//            System.out.println("New balance for " + code + " : " + newBalance + " from entry " + balanceChangeEvent.getAccountingEntryOptional());
//        }
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

    public Map<String, BigDecimal> getBalanceByAccountCode() {
        return balanceByAccountCode;
    }
}

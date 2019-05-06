package be.valuya.winbooks.api.extra.event;

public enum WinbooksEventCategory {

    ARCHIVE_NOT_FOUND(WinbooksEventType.WARNING);

    private WinbooksEventType winbooksEventType;

    WinbooksEventCategory(WinbooksEventType winbooksEventType) {
        this.winbooksEventType = winbooksEventType;
    }

    public WinbooksEventType getWinbooksEventType() {
        return winbooksEventType;
    }
}

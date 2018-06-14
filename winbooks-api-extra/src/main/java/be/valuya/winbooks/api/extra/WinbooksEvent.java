package be.valuya.winbooks.api.extra;

import java.util.Arrays;
import java.util.List;

public class WinbooksEvent {

    private WinbooksEventCategory winbooksEventCategory;
    private String message;
    private List<Object> arguments;

    public WinbooksEvent() {
    }

    public WinbooksEvent(WinbooksEventCategory winbooksEventCategory, String message, List<Object> arguments) {
        this.winbooksEventCategory = winbooksEventCategory;
        this.message = message;
        this.arguments = arguments;
    }

    public WinbooksEvent(WinbooksEventCategory winbooksEventCategory, String message, Object... argumentArray) {
        this.winbooksEventCategory = winbooksEventCategory;
        this.message = message;
        this.arguments = Arrays.asList(argumentArray);
    }

    public WinbooksEventCategory getWinbooksEventCategory() {
        return winbooksEventCategory;
    }

    public void setWinbooksEventCategory(WinbooksEventCategory winbooksEventCategory) {
        this.winbooksEventCategory = winbooksEventCategory;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<Object> getArguments() {
        return arguments;
    }
}

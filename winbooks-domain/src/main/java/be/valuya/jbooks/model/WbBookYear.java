package be.valuya.jbooks.model;

/**
 *
 * @author Yannick Majoros <yannick@valuya.be>
 */
public class WbBookYear {

    private int index;
    private String shortName;
    private String longName;

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getLongName() {
        return longName;
    }

    public void setLongName(String longName) {
        this.longName = longName;
    }
}

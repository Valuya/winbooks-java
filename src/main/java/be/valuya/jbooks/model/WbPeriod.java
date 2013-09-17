package be.valuya.jbooks.model;

/**
 *
 * @author Yannick Majoros <yannick@valuya.be>
 */
public class WbPeriod {

    private WbBookYear wbBookYear;
    private String name;
    private String num;

    public WbBookYear getWbBookYear() {
        return wbBookYear;
    }

    public void setWbBookYear(WbBookYear wbBookYear) {
        this.wbBookYear = wbBookYear;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }
}

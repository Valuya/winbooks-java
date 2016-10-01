package be.valuya.jbooks.model;

import java.util.Objects;

/**
 * @author Yannick
 */
public class WbParam {

    private String id;
    private String value;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        WbParam wbParam = (WbParam) o;
        return Objects.equals(id, wbParam.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

package be.valuya.jbooks.util;

/**
 *
 * @author Yannick Majoros <yannick@valuya.be>
 */
public class WbFatalError {

    private String code;
    private String target;
    private String description;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 47 * hash + (this.code != null ? this.code.hashCode() : 0);
        hash = 47 * hash + (this.target != null ? this.target.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final WbFatalError other = (WbFatalError) obj;
        if ((this.code == null) ? (other.code != null) : !this.code.equals(other.code)) {
            return false;
        }
        return !((this.target == null) ? (other.target != null) : !this.target.equals(other.target));
    }
}

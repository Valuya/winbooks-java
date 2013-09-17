package be.valuya.jbooks.model;

import be.valuya.winbooks.TypeSolution;
import java.util.Set;

/**
 *
 * @author Yannick Majoros <yannick@valuya.be>
 */
public class WbWarning {

    private String code;
    private String target;
    private String description;
    private Set<TypeSolution> typeSolutions;

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

    public Set<TypeSolution> getTypeSolutions() {
        return typeSolutions;
    }

    public void setTypeSolutions(Set<TypeSolution> typeSolutions) {
        this.typeSolutions = typeSolutions;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 97 * hash + (this.code != null ? this.code.hashCode() : 0);
        hash = 97 * hash + (this.target != null ? this.target.hashCode() : 0);
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
        final WbWarning other = (WbWarning) obj;
        if ((this.code == null) ? (other.code != null) : !this.code.equals(other.code)) {
            return false;
        }
        if ((this.target == null) ? (other.target != null) : !this.target.equals(other.target)) {
            return false;
        }
        return true;
    }
}

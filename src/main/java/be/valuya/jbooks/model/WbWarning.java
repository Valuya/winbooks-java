package be.valuya.jbooks.model;

import be.valuya.winbooks.TypeSolution;
import java.util.Objects;
import java.util.Set;

/**
 *
 * @author Yannick Majoros <yannick@valuya.be>
 */
public class WbWarning {

    private String code;
    private String target;
    private String description;
    private Set<TypeSolution> typesSolutions;
    private boolean mitigated;

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

    public Set<TypeSolution> getTypesSolutions() {
        return typesSolutions;
    }

    public void setTypesSolutions(Set<TypeSolution> typesSolutions) {
        this.typesSolutions = typesSolutions;
    }

    public boolean isMitigated() {
        return mitigated;
    }

    public void setMitigated(boolean mitigated) {
        this.mitigated = mitigated;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 79 * hash + Objects.hashCode(this.code);
        hash = 79 * hash + Objects.hashCode(this.target);
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
        if (!Objects.equals(this.code, other.code)) {
            return false;
        }
        return Objects.equals(this.target, other.target);
    }

}

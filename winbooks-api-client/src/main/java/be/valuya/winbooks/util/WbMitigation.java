package be.valuya.winbooks.util;

import be.valuya.winbooks.TypeSolution;
import java.io.Serializable;
import java.util.Objects;

/**
 *
 * @author Yannick Majoros <yannick@valuya.be>
 */
public class WbMitigation implements Serializable {

    private String code;
    private String target;
    private String errorDescription;
    private TypeSolution typeSolution;

    public WbMitigation() {
    }

    public WbMitigation(String code, String target, String errorDescription, TypeSolution typeSolution) {
        this.code = code;
        this.target = target;
        this.errorDescription = errorDescription;
        this.typeSolution = typeSolution;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public TypeSolution getTypeSolution() {
        return typeSolution;
    }

    public void setTypeSolution(TypeSolution typeSolution) {
        this.typeSolution = typeSolution;
    }

    public String getErrorDescription() {
        return errorDescription;
    }

    public void setErrorDescription(String errorDescription) {
        this.errorDescription = errorDescription;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 59 * hash + Objects.hashCode(this.code);
        hash = 59 * hash + Objects.hashCode(this.target);
        hash = 59 * hash + Objects.hashCode(this.typeSolution);
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
        final WbMitigation other = (WbMitigation) obj;
        if (!Objects.equals(this.code, other.code)) {
            return false;
        }
        if (!Objects.equals(this.target, other.target)) {
            return false;
        }
        return this.typeSolution == other.typeSolution;
    }
}

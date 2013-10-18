package be.valuya.jbooks.model;

import be.valuya.winbooks.TypeSolution;
import java.io.Serializable;

/**
 *
 * @author Yannick Majoros <yannick@valuya.be>
 */
public class WbMitigation implements Serializable {

    private String code;
    private String target;
    private TypeSolution typeSolution;

    public WbMitigation() {
    }

    public WbMitigation(String code, String target, TypeSolution typeSolution) {
        this.code = code;
        this.target = target;
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
}

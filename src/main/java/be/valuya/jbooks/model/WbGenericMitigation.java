package be.valuya.jbooks.model;

import be.valuya.winbooks.TypeSolution;

/**
 *
 * @author Yannick Majoros <yannick@valuya.be>
 */
public class WbGenericMitigation {

    private String code;
    private TypeSolution typeSolution;

    public WbGenericMitigation() {
    }

    public WbGenericMitigation(String code, TypeSolution typeSolution) {
        this.code = code;
        this.typeSolution = typeSolution;
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

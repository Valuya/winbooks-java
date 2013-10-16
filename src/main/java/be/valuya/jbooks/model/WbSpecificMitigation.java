package be.valuya.jbooks.model;

import be.valuya.winbooks.TypeSolution;

/**
 *
 * @author Yannick Majoros <yannick@valuya.be>
 */
public class WbSpecificMitigation {

    private WbWarning warning;
    private TypeSolution typeSolution;

    public WbWarning getWarning() {
        return warning;
    }

    public void setWarning(WbWarning warning) {
        this.warning = warning;
    }

    public TypeSolution getTypeSolution() {
        return typeSolution;
    }

    public void setTypeSolution(TypeSolution typeSolution) {
        this.typeSolution = typeSolution;
    }
}

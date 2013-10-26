package be.valuya.jbooks.model;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author Yannick Majoros <yannick@valuya.be>
 */
public class WbWarning implements WbError, Serializable {

    private String code;
    private String target;
    private String description;
    private List<WbWarningResolution> wbWarningResolutions;
    private boolean mitigated;

    public WbWarning() {
    }

    public WbWarning(String code, String target) {
        this.code = code;
        this.target = target;
    }

    public WbWarning(String code, String target, String description) {
        this.code = code;
        this.target = target;
        this.description = description;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public String getTarget() {
        return target;
    }

    @Override
    public void setTarget(String target) {
        this.target = target;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    public List<WbWarningResolution> getWbWarningResolutions() {
        return wbWarningResolutions;
    }

    public void setWbWarningResolutions(List<WbWarningResolution> wbWarningResolutions) {
        this.wbWarningResolutions = wbWarningResolutions;
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

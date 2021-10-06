package be.valuya.winbooks.api.accountingtroll.converter;

import be.valuya.accountingtroll.domain.ATVatCode;
import be.valuya.accountingtroll.domain.VatDeducibility;
import be.valuya.accountingtroll.domain.VatLiability;
import be.valuya.jbooks.model.WbVatCat;
import be.valuya.jbooks.model.WbVatCodeSpec;
import be.valuya.jbooks.model.WbVatDeducibility;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

public class ATVatCodeConverter {
    public ATVatCode convertToTrollVatCode(WbVatCodeSpec wbSpec) {
        ATVatCode atVatCode = new ATVatCode();

        atVatCode.setId(wbSpec.getWbCode());
        atVatCode.setCode(wbSpec.getFrCode());
        atVatCode.setHeader(wbSpec.getFrHeader());
        atVatCode.setLabel(wbSpec.getFrLabel());
        atVatCode.setDescription(wbSpec.getFrDescription());
        atVatCode.setRate(wbSpec.getRatePercent()
                .divide(BigDecimal.valueOf(100L), 6, RoundingMode.UNNECESSARY));

        Optional.ofNullable(wbSpec.getCategory())
                .flatMap(this::convertCategory)
                .ifPresent(atVatCode::setLiability);

        Optional.ofNullable(wbSpec.getDeducibility())
                .flatMap(this::convertDeducibility)
                .ifPresent(atVatCode::setDeducibility);

        atVatCode.setFinancial(wbSpec.isFinFlag());
        atVatCode.setIntraCom(wbSpec.isIntraComFlag());

        return atVatCode;
    }

    private <U> Optional<VatDeducibility> convertDeducibility(WbVatDeducibility wbVatDeducibility) {
        switch (wbVatDeducibility) {
            case DEDUCIBLE:
                return Optional.of(VatDeducibility.DEDUCIBLE);
            case NO_DEDUCIBLE:
                return Optional.of(VatDeducibility.NO_DEDUCIBLE);
            case PARTIALLY_DEDUCIBLE:
                return Optional.of(VatDeducibility.PARTIALLY_DEDUCIBLE);
            default:
                throw new IllegalArgumentException(wbVatDeducibility.name());
        }
    }

    private Optional<VatLiability> convertCategory(WbVatCat wbVatCat) {
        switch (wbVatCat) {
            case UNKNOWN:
                return Optional.empty();
            case LIABLE:
                return Optional.of(VatLiability.LIABLE);
            case ZERO_RATED:
                return Optional.of(VatLiability.ZERO_RATED);
            case NON_LIABLE:
                return Optional.of(VatLiability.NON_LIABLE);
            default:
                throw new IllegalArgumentException(wbVatCat.name());
        }
    }
}

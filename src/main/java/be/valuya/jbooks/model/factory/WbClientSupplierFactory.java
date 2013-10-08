package be.valuya.jbooks.model.factory;

import be.valuya.jbooks.model.WbClientSupplier;
import be.valuya.jbooks.model.WbCustomClientAttribute;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Yannick Majoros <yannick@valuya.be>
 */
public class WbClientSupplierFactory {

    public static WbClientSupplier createWbClientSupplier() {
        WbClientSupplier wbClientSupplier = new WbClientSupplier();
        List<WbCustomClientAttribute> wbCustomClientAttributes = new ArrayList<>();
        wbClientSupplier.setWbCustomClientAttributes(wbCustomClientAttributes);
        return wbClientSupplier;
    }
}

package be.valuya.jbooks;

import be.valuya.jbooks.model.WbValue;
import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;

/**
 *
 * @author Yannick Majoros <yannick@valuya.be>
 */
public class WbValueFormat extends Format {

    @Override
    public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
        if (obj == null) {
            return null;
        }
        WbValue wbValue = (WbValue) obj;
        String value = wbValue.getValue();
        StringBuffer stringBuffer = new StringBuffer(value);
        return stringBuffer;
    }

    @Override
    public Object parseObject(String source, ParsePosition pos) {
        throw new IllegalStateException("not implemented");
    }
}

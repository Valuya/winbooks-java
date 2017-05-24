package be.valuya.csv;

import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author Yannick Majoros <yannick@valuya.be>
 */
public class CsvHandlerTest {

    @Test
    public void testCorrectForCharset() {
        CsvHandler csvHandler = new CsvHandler();

        String corrected1 = csvHandler.correctForCharset("xyz");
        Assert.assertEquals("xyz", corrected1);

        String corrected2 = csvHandler.correctForCharset("xyéz");
        Assert.assertEquals("xyéz", corrected2);

        String corrected3 = csvHandler.correctForCharset("aa卐");
        Assert.assertEquals("aa?", corrected3);
    }

}

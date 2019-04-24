package be.valuya.csv;

import be.valuya.jbooks.DefaultCategory;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

/**
 * @author Yannick Majoros <yannick@valuya.be>
 */
@Category(DefaultCategory.class)
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

package be.valuya.jbooks;

import be.valuya.jbooks.model.WbBookYear;
import be.valuya.jbooks.model.WbClientSupplier;
import be.valuya.jbooks.model.WbClientSupplierType;
import be.valuya.jbooks.model.WbEntry;
import be.valuya.jbooks.model.WbImport;
import be.valuya.jbooks.model.WbInvoice;
import be.valuya.jbooks.model.WbInvoiceLine;
import be.valuya.jbooks.model.WbPeriod;
import be.valuya.jbooks.model.WbVatCode;
import be.valuya.jbooks.model.factory.WbClientSupplierFactory;
import be.valuya.winbooks.domain.error.WbFatalError;
import be.valuya.winbooks.util.WbImportResult;
import be.valuya.winbooks.util.WbLanguage;
import be.valuya.winbooks.util.WbWarning;
import be.valuya.winbooks.util.WbWarningResolution;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Yannick Majoros <yannick@valuya.be>
 */
//@Ignore
public class WinbooksTest {

    private Winbooks winbooks;

    @Before
    public void setUp() {
        winbooks = new Winbooks();
        winbooks.login("SYSTEM", "", WbLanguage.FRENCH);
        winbooks.openDossier("PARFILUX");
        winbooks.openBookYear("Ex. 2012");
    }

    @Test
    public void bla() {
        winbooks.doStuff();
    }
    
    @Test
    public void testImportDataTest() {
        WbImport wbImport = createTestWbImport();

        WbImportResult wbImportResult = winbooks.importDataTest(wbImport);
        Assert.assertNotNull(wbImportResult);

        List<WbWarning> wbWarnings = wbImportResult.getWbWarnings();
        Assert.assertNotNull(wbWarnings);

        System.out.println("********************");
        System.out.println("* WARNINGS");
        System.out.println("********************");
        int warningIndex = 1;
        for (WbWarning wbWarning : wbWarnings) {
            System.out.println("Warning " + warningIndex++);
            System.out.println(" code: " + wbWarning.getCode());
            System.out.println(" description: " + wbWarning.getDescription());
            System.out.println(" target: " + wbWarning.getTarget());
            List<WbWarningResolution> wbWarningResolutions = wbWarning.getWbWarningResolutions();
            System.out.println(" resolutions: " + wbWarningResolutions);
        }

        List<WbFatalError> wbFatalErrors = wbImportResult.getWbFatalErrors();
        Assert.assertNotNull(wbFatalErrors);

        System.out.println("********************");
        System.out.println("* ERRORS");
        System.out.println("********************");
        int errorIndex = 1;
        for (WbFatalError wbFatalError : wbFatalErrors) {
            System.out.println("Error " + errorIndex++);
            System.out.println(" code: " + wbFatalError.getCode());
            System.out.println(" description: " + wbFatalError.getDescription());
            System.out.println(" target: " + wbFatalError.getTarget());
        }
    }

    /**
     * List<WbGenericMitigation> genericWarningResolutions = new ArrayList<>(); WbGenericMitigation
     * wbGenericWarningResolution1 = new WbGenericMitigation(); wbGenericWarningResolution1.setCode("SEQ_RUPT");
     * wbGenericWarningResolution1.setTypeSolution(TypeSolution.wbAccept);
     * genericWarningResolutions.add(wbGenericWarningResolution1); WbGenericMitigation wbGenericWarningResolution2 = new
     * WbGenericMitigation(); wbGenericWarningResolution2.setCode("OUT_DAT");
     * wbGenericWarningResolution2.setTypeSolution(TypeSolution.wbAccept);
     * genericWarningResolutions.add(wbGenericWarningResolution2);
     *
     * WbMitigation wbMitigation = new WbMitigation(null, genericWarningResolutions);
     * wbImport.setWbMitigation(wbMitigation);
     */

    @Test
    public void testGetInternalVatCode() {
        BigDecimal vatRate = BigDecimal.valueOf(2100, 4);
        WbVatCode wbVatCode = winbooks.getInternalVatCode(vatRate, WbClientSupplierType.CLIENT, WbLanguage.FRENCH);
        Assert.assertNotNull(wbVatCode);

        String account1 = wbVatCode.getAccount1();
        Assert.assertEquals("451000", account1);

        BigDecimal wbVatRate = wbVatCode.getVatRate();
        Assert.assertTrue(vatRate.multiply(BigDecimal.valueOf(100)).compareTo(wbVatRate) == 0);

        String internalVatCode = wbVatCode.getInternalVatCode();
        Assert.assertEquals("211400", internalVatCode);
    }

    @Test
    public void testGetDllVersion() {
        String dllVersion = winbooks.getDllVersion();
        Assert.assertTrue(dllVersion.matches("^\\d+(\\.\\d+)*$"));
    }

    @Test
    public void testGetDllCompilDate() {
        Date dllCompilDate = winbooks.getDllCompilDate();
        Assert.assertNotNull(dllCompilDate);
    }

    @Test
    public void testGetBookYears() {
        List<WbBookYear> bookYears = winbooks.getBookYears();
        Assert.assertFalse(bookYears.isEmpty());
        for (WbBookYear wbBookYear : bookYears) {
            String bookYearName = wbBookYear.getLongName();
            String shortBookYearName = wbBookYear.getShortName();
            System.out.println("Book year: " + shortBookYearName + " " + bookYearName);
        }
    }

    @Test
    public void testGetBookYearPeriods() {
        List<WbBookYear> bookYears = winbooks.getBookYears();
        Assert.assertFalse(bookYears.isEmpty());
        for (WbBookYear wbBookYear : bookYears) {
            String bookYearName = wbBookYear.getLongName();
            String shortBookYearName = wbBookYear.getShortName();
            System.out.println("Book year: " + shortBookYearName + " " + bookYearName);
            List<WbPeriod> bookYearPeriods = winbooks.getBookYearPeriods(wbBookYear);
            Assert.assertFalse(bookYearPeriods.isEmpty());
            for (WbPeriod wbPeriod : bookYearPeriods) {
                String periodName = wbPeriod.getShortName();
                System.out.println(periodName);
            }
        }
    }

    @Test
    public void testGetBookYear() {
        Date date = new Date(112, Calendar.JANUARY, 2);
        WbBookYear wbBookYear = winbooks.getBookYear(date);
        String bookYearStr = wbBookYear.getShortName();
        Assert.assertTrue(bookYearStr.contains("2012"));
        Assert.assertNotNull(wbBookYear);
    }

    private WbImport createTestWbImport() {
        WbClientSupplier wbClientSupplier1 = WbClientSupplierFactory.createWbClientSupplier();
        wbClientSupplier1.setName1("Jean");
        wbClientSupplier1.setName2("Dupont");
        wbClientSupplier1.setBankAccount("063-9818425-23");
        wbClientSupplier1.setZipCode("1348");
        wbClientSupplier1.setCity("Louvain-la-Neuve");
        wbClientSupplier1.setCivName1("Mr");
        wbClientSupplier1.setCountryCode("BE");
        wbClientSupplier1.setTelNumber("0498707213");
        wbClientSupplier1.setNumber("DUPONT");
        //importData(...)
        List<WbClientSupplier> wbClientSupplierList = new ArrayList<>();
        wbClientSupplierList.add(wbClientSupplier1);
        WbInvoiceLine wbInvoiceLine1 = new WbInvoiceLine();
        BigDecimal eVat1 = BigDecimal.valueOf(10000, 2);
        BigDecimal vatRate1 = BigDecimal.valueOf(2100, 2);
        BigDecimal vat1 = eVat1.multiply(vatRate1).scaleByPowerOfTen(-2);
        wbInvoiceLine1.setEVat(eVat1);
        wbInvoiceLine1.setVatRate(vatRate1);
        wbInvoiceLine1.setVat(vat1);
        WbInvoice wbInvoice = new WbInvoice();
        List<WbInvoiceLine> wbInvoiceLines = Arrays.asList(wbInvoiceLine1);
        wbInvoice.setInvoiceLines(wbInvoiceLines);
        wbInvoice.setWbClientSupplier(wbClientSupplier1);
        wbInvoice.setDbkCode("VENTES");
        wbInvoice.setRef("20139001");
        Date date = new Date(113, Calendar.JANUARY, 2);
        wbInvoice.setDate(date);
        wbInvoice.setDescription("test invoice");
        List<WbEntry> wbEntries = winbooks.convertInvoiceToEntries(wbInvoice, true);
        WbImport wbImport = new WbImport();
        wbImport.setWbClientSupplierList(wbClientSupplierList);
        wbImport.setWbEntries(wbEntries);

        return wbImport;
    }
}

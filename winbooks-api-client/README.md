winbooks-java
=============

Free Winbooks Java API

This lib lets you communicate with Winbooks accounting software in Java.

Let's be clear: Winbooks' API is a real mess. Exporting stuff involves writing CSV (or worse, DBF) files, instead of a good-designed object-oriented API.

This is my attempt at abstracting this away.

It's far from a clean, documented project. Yet. I hope I'll make it one someday. For now, it works for me.

Some features:
* query some basic stuff (internal vat codes, version, ...)
* import clients / suppliers, accounting journal- entries
* convert invoices to journal entries

Some examples(find more in tests):

Winbooks winbooks = new Winbooks();
winbooks.login("SYSTEM", "", WbLanguage.FRENCH);
winbooks.openDossier("PARFILUX");
winbooks.openBookYear("Ex. 2003");

BigDecimal vatRate = BigDecimal.valueOf(2100, 2);
String internalVatCode = winbooks.getInternalVatCode(vatRate, WbClientSupplierType.CLIENT, WbLanguage.FRENCH);

WbBookYear wbBookYear = winbooks.getBookYear(date);

```java
// importing stuff (client + invoice)
WbClientSupplier wbClientSupplier1 = WbClientSupplierFactory.createWbClientSupplier();
wbClientSupplier1.setName1("Jean");
wbClientSupplier1.setName2("Dupont");
wbClientSupplier1.setBankAccount("063-9818425-23");
wbClientSupplier1.setZipCode("1348");
wbClientSupplier1.setCity("Louvain-la-Neuve");
wbClientSupplier1.setCivName1("Mr");
wbClientSupplier1.setCountry("BE");
wbClientSupplier1.setTelNumber("0498707213");
wbClientSupplier1.setNumber("DUPONT");
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
wbInvoice.setRef("20039001");
Date date = new Date(103, Calendar.JANUARY, 2);
wbInvoice.setDate(date);
wbInvoice.setDescription("test invoice");
List<WbEntry> wbEntries = winbooks.convertInvoiceToEntries(wbInvoice, true);
WbImport wbImport = new WbImport();
wbImport.setWbClientSupplierList(wbClientSupplierList);
wbImport.setWbEntries(wbEntries);
List<WbGenericWarningResolution> genericWarningResolutions = new ArrayList<>();
WbGenericWarningResolution wbGenericWarningResolution1 = new WbGenericWarningResolution();
wbGenericWarningResolution1.setCode("SEQ_RUPT");
wbGenericWarningResolution1.setTypeSolution(TypeSolution.wbAccept);
genericWarningResolutions.add(wbGenericWarningResolution1);
WbGenericWarningResolution wbGenericWarningResolution2 = new WbGenericWarningResolution();
wbGenericWarningResolution2.setCode("OUT_DAT");
wbGenericWarningResolution2.setTypeSolution(TypeSolution.wbAccept);
genericWarningResolutions.add(wbGenericWarningResolution2);
wbImport.setGenericWarningResolutions(genericWarningResolutions);
WbImportResult wbImportResult = winbooks.importDataTest(wbImport);```


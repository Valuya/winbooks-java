package be.valuya.jbooks;

import be.valuya.csv.CsvHandler;
import be.valuya.jbooks.exception.WinbooksError;
import be.valuya.jbooks.exception.WinbooksException;
import be.valuya.jbooks.exception.WinbooksInitException;
import be.valuya.jbooks.exception.WinbooksLoginException;
import be.valuya.jbooks.exception.WinbooksOpenBookyearException;
import be.valuya.jbooks.exception.WinbooksOpenDossierException;
import be.valuya.jbooks.model.WbBookYear;
import be.valuya.jbooks.model.WbClientSupplier;
import be.valuya.jbooks.model.WbClientSupplierType;
import be.valuya.jbooks.model.WbCustomClientAttribute;
import be.valuya.jbooks.model.WbDbkType;
import be.valuya.jbooks.model.WbDocOrderType;
import be.valuya.jbooks.model.WbDocStatus;
import be.valuya.jbooks.model.WbDocType;
import be.valuya.jbooks.model.WbEntry;
import be.valuya.jbooks.model.WbImport;
import be.valuya.jbooks.model.WbImportResult;
import be.valuya.jbooks.model.WbInvoice;
import be.valuya.jbooks.model.WbInvoiceLine;
import be.valuya.jbooks.model.WbLanguage;
import be.valuya.jbooks.model.WbMemoType;
import be.valuya.jbooks.model.WbMitigation;
import be.valuya.jbooks.model.WbPeriod;
import be.valuya.jbooks.model.WbVatCat;
import be.valuya.jbooks.model.WbVatCode;
import be.valuya.jbooks.model.WbWarning;
import be.valuya.jbooks.model.WbWarningResolution;
import be.valuya.jbooks.util.WbFatalError;
import be.valuya.jbooks.util.WbValueFormat;
import be.valuya.winbooks.ClassFactory;
import be.valuya.winbooks.LangueforVat;
import be.valuya.winbooks.TypeSolution;
import be.valuya.winbooks.WinbooksObject___v0;
import be.valuya.winbooks._BookYear;
import be.valuya.winbooks._BookYears;
import be.valuya.winbooks._Comptes;
import be.valuya.winbooks._ErrorCode;
import be.valuya.winbooks._FatalError;
import be.valuya.winbooks._FatalErrors;
import be.valuya.winbooks._Field;
import be.valuya.winbooks._Fields;
import be.valuya.winbooks._Import;
import be.valuya.winbooks._Param;
import be.valuya.winbooks._Period;
import be.valuya.winbooks._Periods;
import be.valuya.winbooks._Transactions;
import be.valuya.winbooks._Warning;
import be.valuya.winbooks._Warnings;
import com4j.Holder;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.Format;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Hello world!
 *
 */
public class Winbooks {

    private final WinbooksObject___v0 winbooksCom;
    /**
     * Book year override, will replace book year for dates, period, ... (e.g. demo is limited to some book years).
     */
    private Integer bookYearOverride;
    private String bookYearNameOverride;
    private String dossierOverride;

    public Winbooks() {
        winbooksCom = ClassFactory.createWinbooksObject();
        short result = winbooksCom.init();
        String lastErrorMessage = winbooksCom.lastErrorMessage();
        switch (result) {
            case 0:
            case 3:
                break;
            case 1:
                throw new WinbooksInitException(WinbooksError.USER_FILE_ERROR, lastErrorMessage);
            default:
                throw new WinbooksInitException(WinbooksError.UNKNOWN_ERROR, lastErrorMessage);
        }
    }

    public void login(String userName, String userPassword, WbLanguage WbLanguage) {
        String languangeStr = WbLanguage.getValue();
        short result = winbooksCom.login(userName, userPassword, languangeStr);
        String lastErrorMessage = winbooksCom.lastErrorMessage();
        switch (result) {
            case 0:
                break;
            case 1:
                throw new WinbooksLoginException(WinbooksError.NOT_INITIALIZED, lastErrorMessage);
            case 2:
                throw new WinbooksLoginException(WinbooksError.USER_FILE_ERROR, lastErrorMessage);
            default:
                throw new WinbooksLoginException(WinbooksError.UNKNOWN_ERROR, lastErrorMessage);
        }
    }

    public void close() {
        winbooksCom.closeDossier();
    }

    public short openDossier(String shortName) {
        short result;
        if (dossierOverride != null) {
            result = winbooksCom.openDossier(dossierOverride);
        } else {
            result = winbooksCom.openDossier(shortName);
        }
        String lastErrorMessage = winbooksCom.lastErrorMessage();
        switch (result) {
            case 0:
                break;
            case 1:
                throw new WinbooksOpenDossierException(WinbooksError.NO_DOSSIER, lastErrorMessage);
            case 2:
                throw new WinbooksOpenDossierException(WinbooksError.NOT_INITIALIZED, lastErrorMessage);
            case 3:
                throw new WinbooksOpenDossierException(WinbooksError.NOT_LOGGED_IN, lastErrorMessage);
            case 4:
                throw new WinbooksOpenDossierException(WinbooksError.BAD_PASSWORD, lastErrorMessage);
            case 5:
                throw new WinbooksOpenDossierException(WinbooksError.DOSSIER_NOT_FOUND, lastErrorMessage);
            case 6:
                throw new WinbooksOpenDossierException(WinbooksError.DOSSIER_LOCKED, lastErrorMessage);
            case 7:
                throw new WinbooksOpenDossierException(WinbooksError.API_UNLICENSED, lastErrorMessage);
            case 8:
                throw new WinbooksOpenDossierException(WinbooksError.DEMO, lastErrorMessage);
            default:
                throw new WinbooksInitException(WinbooksError.UNKNOWN_ERROR, lastErrorMessage);
        }
        return result;
    }

    public short openBookYear(String bookYearShortName) {
        short result;
        if (bookYearNameOverride != null) {
            result = winbooksCom.openBookYear(bookYearNameOverride);
        } else {
            result = winbooksCom.openBookYear(bookYearShortName);
        }
        String lastErrorMessage = winbooksCom.lastErrorMessage();
        switch (result) {
            case 0:
                break;
            case 1:
                throw new WinbooksOpenBookyearException(WinbooksError.NO_DOSSIER, lastErrorMessage);
            case 2:
                throw new WinbooksOpenBookyearException(WinbooksError.NO_BOOKYEAR, lastErrorMessage);
            case 3:
                throw new WinbooksOpenBookyearException(WinbooksError.BOOKYEAR_NOT_FOUND, lastErrorMessage);
            case 4:
                throw new WinbooksOpenBookyearException(WinbooksError.CANNOT_OPEN_DOSSIER, lastErrorMessage);
            default:
                throw new WinbooksInitException(WinbooksError.UNKNOWN_ERROR, lastErrorMessage);
        }
        return result;
    }

    public WbImportResult importDataTest(WbImport wbImport) {
        _Import internalImport = testImportInternal(wbImport);
        WbImportResult wbImportResult = createImportResult(internalImport);
        return wbImportResult;
    }

    public WbImportResult importData(WbImport wbImport, List<WbMitigation> wbMitigations) {
        return importData(wbImport, wbMitigations, false);
    }

    public WbImportResult importData(WbImport wbImport, List<WbMitigation> wbMitigations, boolean throwException) {
        _Import internalImport = testImportInternal(wbImport);
        // mitigation
        mitigateWarnings(internalImport, wbMitigations);
        // import
        short executeResult = internalImport.execute();

        // process result
        WinbooksError winbooksError = getWinbooksErrorForInternalImportResult(executeResult);
        String errorMessage = winbooksCom.lastErrorMessage();
        if (winbooksError != null && throwException) {
            throw new WinbooksException(winbooksError, errorMessage);
        }

        WbImportResult wbImportResult = createImportResult(internalImport);
        wbImportResult.setWinbooksError(winbooksError);
        wbImportResult.setErrorMessage(errorMessage);

        return wbImportResult;
    }

    private WinbooksError getWinbooksErrorForInternalImportResult(short internalImportResult) {
        WinbooksError winbooksError;
        switch (internalImportResult) {
            case 0:
                winbooksError = null;
                break;
            case 1:
                winbooksError = WinbooksError.UNTESTED;
                break;
            case 2:
                winbooksError = WinbooksError.UNRESOLVED_WARNINGS;
                break;
            case 3:
                winbooksError = WinbooksError.FATAL_ERRORS;
                break;
            case 4:
                winbooksError = WinbooksError.RESOLUTION_UNSYCHRONIZED;
                break;
            default:
                winbooksError = WinbooksError.UNKNOWN_ERROR;
        }
        return winbooksError;
    }

    private void checkBooleanResult(boolean result) {
        if (!result) {
            String lastErrorMessage = winbooksCom.lastErrorMessage();
            throw new WinbooksException(WinbooksError.UNKNOWN_ERROR, lastErrorMessage);
        }
    }

    private Path generateDataFilesInTempDirectory(WbImport wbImport) {
        try {
            Path tempPath = Files.createTempDirectory("Wb");
            List<WbClientSupplier> wbClients = wbImport.getWbClientSupplierList();
            List<WbEntry> wbEntries = wbImport.getWbEntries();
            generateDataFiles(tempPath, wbClients, wbEntries);
            return tempPath;
        } catch (IOException ioException) {
            throw new RuntimeException("Could not create temporary directory", ioException);
        }
    }

    private void generateDataFiles(Path path, List<WbClientSupplier> wbClientSupplierList, List<WbEntry> wbEntries) throws IOException {
        generateClientSupplierCsv(wbClientSupplierList, path);
        generateEntriesCsv(wbEntries, path);
    }

    private List<WbWarning> getWarnings(_Import wbImport) {
        List<WbWarning> wbWarnings = new ArrayList<>();

        _Warnings internalWarnings = wbImport.warnings();
        short warningCount = internalWarnings.count();
        for (short i = 1; i <= warningCount; i++) {
            _Warning internalWarning = internalWarnings.item(i);
            WbWarning wbWarning = convertInternalWarning(internalWarning, wbImport);
            wbWarnings.add(wbWarning);
        }

        return wbWarnings;
    }

    private List<WbFatalError> getFatalErrors(_Import wbImport) {
        List<WbFatalError> wbFatalErrors = new ArrayList<>();

        _FatalErrors internalFatalErrors = wbImport.fatalErrors();
        short errorCount = internalFatalErrors.count();
        for (short i = 1; i <= errorCount; i++) {
            _FatalError internalFatalError = internalFatalErrors.item(i);
            WbFatalError wbFatalError = convertInternalFatalError(internalFatalError, wbImport);
            wbFatalErrors.add(wbFatalError);
        }

        return wbFatalErrors;
    }

    private void mitigateWarnings(_Import wbImport, List<WbMitigation> wbMitigations) {
        // generic mitigation (not invoice-specific)
        if (wbMitigations != null) {
            for (WbMitigation wbMitigation : wbMitigations) {
                String target = wbMitigation.getTarget();
                if (target != null) {
                    // specific mitigation, handle later
                    continue;
                }
                String code = wbMitigation.getCode();
                TypeSolution typeSolution = wbMitigation.getTypeSolution();
                _ErrorCode internalErrorCode = wbImport.errorCodes(code);
                internalErrorCode.setResolution(typeSolution);
            }
        }

        // create a map of specific mitigations
        // key type has code+target as identity
        Map<WbWarning, TypeSolution> specificWarningResolutionMap = new HashMap<>();
        if (wbMitigations != null) {
            for (WbMitigation wbMitigation : wbMitigations) {
                String code = wbMitigation.getCode();
                String target = wbMitigation.getTarget();
                if (target == null) {
                    continue; // generic resolution, already handled
                }
                WbWarning wbWarning = new WbWarning(code, target);
                TypeSolution typeSolution = wbMitigation.getTypeSolution();
                System.out.println(MessageFormat.format("Specific resolution for ---{0}---/---{1}---", wbWarning.getCode(), wbWarning.getTarget()));
                specificWarningResolutionMap.put(wbWarning, typeSolution);
            }
        }

        // iterate over warnings and apply specific resolutions
        _Warnings internalWarnings = wbImport.warnings();
        short warningCount = internalWarnings.count();
        for (short i = 1; i <= warningCount; i++) {
            _Warning internalWarning = internalWarnings.item(i);
            WbWarning wbWarning = convertInternalWarning(internalWarning, wbImport);
            TypeSolution typeSolution = specificWarningResolutionMap.get(wbWarning);
            if (typeSolution != null) {
                Holder<TypeSolution> typeSolutionHolder = new Holder<>(typeSolution);
                internalWarning.setResolution(typeSolutionHolder);
            } else {
                System.out.println(MessageFormat.format("No resolution for ---{0}---/---{1}---", wbWarning.getCode(), wbWarning.getTarget()));
            }
        }
    }

    private WbFatalError convertInternalFatalError(_FatalError internalFatalError, _Import wbImport) {
        String code = internalFatalError.code();
        String target = internalFatalError.param();
        target = trim(target);
        _ErrorCode internalErrorCode = wbImport.errorCodes(code);
        String description = internalErrorCode.description();
        WbFatalError wbFatalError = new WbFatalError(code, target, description);
        return wbFatalError;
    }

    private WbWarning convertInternalWarning(_Warning internalWarning, _Import wbImport) {
        String code = internalWarning.code();
        code = trim(code);
        String target = internalWarning.param();
        target = trim(target);
        _ErrorCode internalErrorCode = wbImport.errorCodes(code);
        List<WbWarningResolution> wbWarningResolutions = new ArrayList<>();
        List<TypeSolution> typesSolutions = getTypesSolutions(internalErrorCode);
        for (TypeSolution typeSolution : typesSolutions) {
            WbWarningResolution wbWarningResolution = convertTypeSolution(typeSolution);
            wbWarningResolutions.add(wbWarningResolution);
        }
        String description = internalErrorCode.description();
        description = trim(description);

        WbWarning wbWarning = new WbWarning(code, target, description);
        wbWarning.setWbWarningResolutions(wbWarningResolutions);
        return wbWarning;
    }

    private List<TypeSolution> getTypesSolutions(_ErrorCode internalErrorCode) {
        String concatenedAllowableAction = internalErrorCode.allowableActions();
        String[] allowableActionArray = concatenedAllowableAction.split(",");
        List<String> allowableActions = Arrays.asList(allowableActionArray);
        List<TypeSolution> typesSolutions = new ArrayList<>();
        for (String allowableAction : allowableActions) {
            if (!allowableAction.isEmpty()) {
                TypeSolution typeSolution = TypeSolution.valueOf(allowableAction);
                typesSolutions.add(typeSolution);
            }
        }
        if (!typesSolutions.contains(TypeSolution.wbReplace)) {
            typesSolutions.add(TypeSolution.wbReplace);
        }
        return typesSolutions;
    }

    public WbVatCode getInternalVatCode(BigDecimal vatRate, WbClientSupplierType wbClientSupplierType, WbLanguage wbLanguage) {
        _Param param = winbooksCom.param();
        int vatRateInt = vatRate.multiply(BigDecimal.valueOf(100)).intValue();
        String vatRateStr = Integer.toString(vatRateInt);
        String clientSupplierTypeStr = wbClientSupplierType.getValue();
        String langStr = wbLanguage.getValue();
        // ATTENTION: Winbooks DLL sometimes crashes (then works again randomly) when putting anythig else here
        Holder<String> langHolder = new Holder<>();

        String internalVatCode = param.vatInternalCode(vatRateStr, clientSupplierTypeStr, langHolder).trim();
        String vatAcc1 = param.vatAcc1(internalVatCode).trim();
        String vatAcc2 = param.vatAcc2(internalVatCode).trim();
        LangueforVat langueforVat = wbLanguage.getLangueforVat();
        String externalVatCode = (String) param.vatExternalCode(internalVatCode, langueforVat);
        Double vatRateDouble = (Double) param.vatRate(internalVatCode);
        BigDecimal internalVatRate = BigDecimal.valueOf(vatRateDouble);

        WbVatCode wbVatCode = new WbVatCode();
        wbVatCode.setInternalVatCode(internalVatCode);
        wbVatCode.setAccount1(vatAcc1);
        wbVatCode.setAccount2(vatAcc2);
        wbVatCode.setExternalVatCode(externalVatCode);
        wbVatCode.setVatRate(internalVatRate);

        return wbVatCode;
    }

    public String getDllVersion() {
        String dllVersion = winbooksCom.getDllVersion();
        return dllVersion;
    }

    public Date getDllCompilDate() {
        try {
            String dllCompilDateStr = winbooksCom.getDllCompilDate();
            DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
            Date dllCompilDate = dateFormat.parse(dllCompilDateStr);
            return dllCompilDate;
        } catch (ParseException parseException) {
            throw new RuntimeException(parseException);
        }
    }

    public List<WbBookYear> getBookYears() {
        List<WbBookYear> wbBookYears = new ArrayList<>();

        _BookYears bookYears = winbooksCom.bookYear();
        short bookYearCount = bookYears.count();
        for (short yearIndex = 1; yearIndex <= bookYearCount; yearIndex++) {
            _BookYear bookYear = bookYears.item(yearIndex);
            String shortName = bookYear.shortName();
            String longName = bookYear.longName();

            WbBookYear wbBookYear = new WbBookYear();
            wbBookYear.setShortName(shortName);
            wbBookYear.setLongName(longName);
            wbBookYear.setIndex(yearIndex);

            wbBookYears.add(wbBookYear);
        }

        return wbBookYears;
    }

    public List<WbPeriod> getBookYearPeriods(WbBookYear wbBookYear) {
        List<WbPeriod> wbPeriods = new ArrayList<>();

        int index = wbBookYear.getIndex();
        _BookYear internalBookYear = winbooksCom.bookYear((short) index);
        _Periods internalPeriods = internalBookYear.periods();
        short periodCount = internalPeriods.count();
        for (short periodIndex = 1; periodIndex <= periodCount; periodIndex++) {
            _Period internalPeriod = internalBookYear.periods(periodIndex);
            String periodName = internalPeriod.name();
            String numOfPeriod = internalPeriod.numOfPeriod(periodName);

            WbPeriod wbPeriod = new WbPeriod();
            wbPeriod.setName(periodName);
            wbPeriod.setNum(numOfPeriod);

            wbPeriods.add(wbPeriod);
        }

        return wbPeriods;
    }

    public void doStuff() {
//        _Dossiers dossiers = winbooksCom.companies();
//        short dossierCount = dossiers.count();
//        for (short dossierIndex = 0; dossierIndex < dossierCount; dossierIndex++) {
//            _Dossier dossier = dossiers.item(dossierIndex);
//            System.out.println(dossier.name());
//        }

        _Transactions accountTrans = winbooksCom.accountTrans();
        _Fields fields = accountTrans.fields();

        _Comptes comptes = winbooksCom.customers();
//        _Fields fields = comptes.fields();

//        _Tables tables = winbooksCom.tables();
//        _TablesUser catCustomer = tables.diaries();
//        _Fields fields = catCustomer.fields();
        short fieldCount = fields.count();
        for (short fieldIndex = 0; fieldIndex < fieldCount; fieldIndex++) {
            _Field field = fields.item(Short.toString(fieldIndex));
            String name = field.name();
            System.out.println("field: " + name);
        }
    }

    public CsvHandler generateClientSupplierCsv(List<WbClientSupplier> wbClientSupplierList) {
        Format wbValueFormat = new WbValueFormat();
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd", Locale.ENGLISH);

        CsvHandler csvHandler = new CsvHandler();
        for (WbClientSupplier wbClientSupplier : wbClientSupplierList) {
            String number = wbClientSupplier.getNumber();
            if (number == null || number.isEmpty()) {
                throw new WinbooksException(WinbooksError.USER_FILE_ERROR, "La référence client ne peut pas être vide");
            }
            if (!number.matches("[a-zA-Z0-9_]+")) {
                String message = MessageFormat.format("La référence client ne peut contenir que des chiffres et des lettres - ''{0}''", number);
                throw new WinbooksException(WinbooksError.USER_FILE_ERROR, message);
            }

            String address1 = wbClientSupplier.getAddress1();
            String address2 = wbClientSupplier.getAddress2();
            String bankAccount = wbClientSupplier.getBankAccount();
            String category = wbClientSupplier.getCategory();
            String central = wbClientSupplier.getCentral();
            String city = wbClientSupplier.getCity();
            String civName1 = wbClientSupplier.getCivName1();
            String civName2 = wbClientSupplier.getCivName2();
            String countryCode = wbClientSupplier.getCountryCode();
            String currency = wbClientSupplier.getCurrency();
            String defltPost = wbClientSupplier.getDefltPost();
            String faxNumber = wbClientSupplier.getFaxNumber();
            String lang = wbClientSupplier.getLang();
            Date lastRemDat = wbClientSupplier.getLastRemDat();
            String lastRemLev = wbClientSupplier.getLastRemLev();
            String name1 = wbClientSupplier.getName1();
            String name2 = wbClientSupplier.getName2();
            String payCode = wbClientSupplier.getPayCode();
            String telNumber = wbClientSupplier.getTelNumber();
            String vatCode = wbClientSupplier.getVatCode();
            String vatNumber = wbClientSupplier.getVatNumber();
            WbClientSupplierType wbClientSupplierType = wbClientSupplier.getWbClientSupplierType();
            WbMemoType wbMemoType = wbClientSupplier.getWbMemoType();
            WbVatCat wbVatCat = wbClientSupplier.getWbVatCat();
            List<WbCustomClientAttribute> WbCustomClientAttributes = wbClientSupplier.getWbCustomClientAttributes();
            String zipCode = wbClientSupplier.getZipCode();

            csvHandler.addCsvLine();
            csvHandler.putValue("NUMBER", number);
            csvHandler.putValue("TYPE", wbValueFormat, wbClientSupplierType);
            csvHandler.putValue("NAME1", name1);
            csvHandler.putValue("NAME2", name2);
            csvHandler.putValue("CIVNAME1", civName1);
            csvHandler.putValue("CIVNAME2", civName2);
            csvHandler.putValue("ADDRESS1", address1);
            csvHandler.putValue("ADDRESS2", address2);
            csvHandler.putValue("VATCAT", wbValueFormat, wbVatCat);
            csvHandler.putValue("COUNTRY", countryCode);
            csvHandler.putValue("VATNUMBER", vatNumber);
            csvHandler.putValue("PAYCODE", payCode);
            csvHandler.putValue("TELNUMBER", telNumber);
            csvHandler.putValue("FAXNUMBER", faxNumber);
            csvHandler.putValue("BNKACCNT", bankAccount);
            csvHandler.putValue("ZIPCODE", zipCode);
            csvHandler.putValue("CITY", city);
            csvHandler.putValue("DELFTPOST", defltPost);
            csvHandler.putValue("LANG", lang);
            csvHandler.putValue("CATEGORY", category);
            csvHandler.putValue("CENTRAL", central);
            csvHandler.putValue("VATCODE", vatCode);
            csvHandler.putValue("CURRENCY", currency);
            csvHandler.putValue("LASTREMLEV", lastRemLev);
            csvHandler.putValue("LASTREMDAT", dateFormat, lastRemDat);
            csvHandler.putValue("MEMOTYPE", wbValueFormat, wbMemoType);

            for (WbCustomClientAttribute wbCustomClientAttribute : WbCustomClientAttributes) {
                String name = wbCustomClientAttribute.getName();
                String value = wbCustomClientAttribute.getValue();
                csvHandler.putValue(name, value);
            }

        }
        return csvHandler;
    }

    public void generateClientSupplierCsv(List<WbClientSupplier> wbClientSupplierList, Path path) throws IOException {
        CsvHandler clientSupplierCsvHandler = generateClientSupplierCsv(wbClientSupplierList);
        Path clientSupplierPath = path.resolve("csf.txt");
        clientSupplierCsvHandler.setWriteHeaders(false);
        clientSupplierCsvHandler.dumpToFile(clientSupplierPath);
    }

    private CsvHandler generateEntriesCsv(List<WbEntry> wbEntries) {
        Format wbValueFormat = new WbValueFormat();
        DecimalFormat vatRateFormat = new DecimalFormat("0.00");
        DecimalFormat moneyFormat = new DecimalFormat("0.00");
        DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols(Locale.ENGLISH);
        vatRateFormat.setDecimalFormatSymbols(decimalFormatSymbols);
        moneyFormat.setDecimalFormatSymbols(decimalFormatSymbols);
        DecimalFormat docOrderFormat = new DecimalFormat("000");
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd", Locale.ENGLISH);

        CsvHandler csvHandler = new CsvHandler();
        for (WbEntry wbEntry : wbEntries) {
            String accountGl = wbEntry.getAccountGl();
            String accountRp = wbEntry.getAccountRp();
            BigDecimal amount = wbEntry.getAmount();
            BigDecimal amountEur = wbEntry.getAmountEur();
            String bookYear = wbEntry.getBookYear();
            String comment = wbEntry.getComment();
            String commentExt = wbEntry.getCommentExt();
            BigDecimal curEurBase = wbEntry.getCurEurBase();
            BigDecimal curRate = wbEntry.getCurRate();
            BigDecimal currAmount = wbEntry.getCurrAmount();
            String currCode = wbEntry.getCurrCode();
            Date date = wbEntry.getDate();
            Date dateDoc = wbEntry.getDateDoc();
            WbDocOrderType wbDocOrderType = wbEntry.getWbDocOrderType();
            String docOrderStr = wbDocOrderType.getValue();
            if (docOrderStr == null) {
                Integer docOrder = wbEntry.getDocOrder();
                docOrderStr = docOrderFormat.format(docOrder);
            }
            String docNumber = wbEntry.getDocNumber();
            WbDocStatus docStatus = wbEntry.getDocStatus();
            Date dueDate = wbEntry.getDueDate();
            String matchNo = wbEntry.getMatchNo();
            WbMemoType memoType = wbEntry.getMemoType();
            Date oldDate = wbEntry.getOldDate();
            String period = wbEntry.getPeriod();
            BigDecimal vatBase = wbEntry.getVatBase();
            String vatCode = wbEntry.getVatCode();
            String vatImput = wbEntry.getVatImput();
            BigDecimal vatTax = wbEntry.getVatTax();
            String dbkCode = wbEntry.getDbkCode();
            WbDbkType wbDbkType = wbEntry.getWbDbkType();
            WbDocType wbDocType = wbEntry.getWbDocType();

            csvHandler.addCsvLine();

            csvHandler.putValue("DOCTYPE", wbValueFormat, wbDocType);
            csvHandler.putValue("DBKCODE", dbkCode);
            csvHandler.putValue("DBKTYPE", wbValueFormat, wbDbkType);
            csvHandler.putValue("DOCNUMBER", docNumber);
            csvHandler.putValue("DOCORDER", docOrderStr);
            csvHandler.putValue("OPCODE", null);
            csvHandler.putValue("ACCOUNTGL", accountGl);
            csvHandler.putValue("ACCOUNTRP", accountRp);
            csvHandler.putValue("BOOKYEAR", bookYear);
            csvHandler.putValue("PERIOD", period);
            csvHandler.putValue("DATE", dateFormat, date);
            csvHandler.putValue("DATEDOC", dateFormat, dateDoc);
            csvHandler.putValue("DUEDATE", dateFormat, dueDate);
            csvHandler.putValue("COMMENT", comment);
            csvHandler.putValue("COMMENTEXT", commentExt);
            csvHandler.putValue("AMOUNT", moneyFormat, amount);
            csvHandler.putValue("AMOUNTEUR", moneyFormat, amountEur);
            csvHandler.putValue("VATBASE", vatRateFormat, vatBase);
            csvHandler.putValue("VATCODE", vatCode);
            csvHandler.putValue("CURRAMOUNT", moneyFormat, currAmount);
            csvHandler.putValue("CURRCODE", currCode);
            csvHandler.putValue("CUREURBASE", moneyFormat, curEurBase);
            csvHandler.putValue("VATTAX", moneyFormat, vatTax);
            csvHandler.putValue("VATIMPUT", vatImput);
            csvHandler.putValue("CURRATE", moneyFormat, curRate);
            csvHandler.putValue("REMINDLEV", null);
            csvHandler.putValue("MATCHNO", matchNo);
            csvHandler.putValue("OLDDATE", dateFormat, oldDate);
            csvHandler.putValue("ISMATCHED", null);
            csvHandler.putValue("ISLOCKED", null);
            csvHandler.putValue("ISIMPORTED", null);
            csvHandler.putValue("ISPOSITIVE", null);
            csvHandler.putValue("ISTEMP", null);
            csvHandler.putValue("MEMOTYPE", wbValueFormat, memoType);
            csvHandler.putValue("ISDOC", null);
            csvHandler.putValue("DOCSTATUS", wbValueFormat, docStatus);
            csvHandler.putValue("DICFROM", null);
            csvHandler.putValue("CODAKEY", null);
        }

        return csvHandler;
    }

    private void generateEntriesCsv(List<WbEntry> wbEntries, Path path) throws IOException {
        CsvHandler entriesCsvHandler = generateEntriesCsv(wbEntries);
        Path entriesPath = path.resolve("act.txt");
        entriesCsvHandler.setWriteHeaders(false);
        entriesCsvHandler.dumpToFile(entriesPath);
    }

    public List<WbEntry> convertInvoiceToEntries(WbInvoice wbInvoice, boolean singleLine) {
        List<WbEntry> wbEntries = new ArrayList<>();

        // ajustement montants
        List<WbInvoiceLine> invoiceLines = wbInvoice.getInvoiceLines();
        List<WbInvoiceLine> regroupedInvoiceLines = regroupInvoiceLines(invoiceLines);
        List<WbInvoiceLine> effectiveInvoiceLines;
        if (singleLine) {
            effectiveInvoiceLines = regroupedInvoiceLines;
        } else {
            effectiveInvoiceLines = wbInvoice.getInvoiceLines();
        }

        WbClientSupplier wbClientSupplier = wbInvoice.getWbClientSupplier();

        String accountGl = wbInvoice.getAccountGl();
        if (accountGl == null || accountGl.isEmpty()) {
            accountGl = wbClientSupplier.getCentral();
        }
        WbDocType wbDocType = wbInvoice.getWbDocType();

        String accountRp = wbClientSupplier.getNumber();
        Date invoiceDate = wbInvoice.getDate();
        Date dueDate = wbInvoice.getDueDate();
        if (dueDate == null) {
            dueDate = invoiceDate;
            wbInvoice.setDueDate(dueDate);
        }
        String dbkCode = wbInvoice.getDbkCode();

        WbDbkType wbDbkType = wbInvoice.getWbDbkType();

        WbClientSupplierType wbClientSupplierType = wbClientSupplier.getWbClientSupplierType();

        // main accounting entry
        WbEntry mainEntry = new WbEntry();
        mainEntry.setAccountGl(accountGl);
        mainEntry.setWbDocType(wbDocType);
        mainEntry.setAccountRp(accountRp);
        mainEntry.setDate(invoiceDate);
        mainEntry.setDateDoc(invoiceDate);
        mainEntry.setDueDate(dueDate);
        String invoiceDescription = wbInvoice.getDescription();
        String invoiceRef = wbInvoice.getRef();
        mainEntry.setComment(invoiceDescription);
        mainEntry.setDoc(true);
        mainEntry.setDbkCode(dbkCode);
        mainEntry.setWbDbkType(wbDbkType);

        Date periodDate = wbInvoice.getPeriodDate();
        if (periodDate == null) {
            periodDate = invoiceDate;
        }
        int bookYear;
        if (periodDate == null) {
            periodDate = invoiceDate;
        }
        if (periodDate == null) {
            String message = MessageFormat.format("Pas de période pour facture {0}", invoiceRef);
            throw new WinbooksException(WinbooksError.NO_PERIOD, message);
        }
        String periodInternalCode = getPeriodInternalCode(periodDate);
        WbBookYear wbBookYear = getBookYear(periodDate);
        if (wbBookYear == null) {
            String message = MessageFormat.format("Pas d''exercice pour cette année: {0,date,short}, facture {1}", periodDate, invoiceRef);
            throw new WinbooksException(WinbooksError.NO_BOOKYEAR, message);
        }
        bookYear = wbBookYear.getIndex();
        mainEntry.setBookYear(Integer.toString(bookYear));
        mainEntry.setPeriod(periodInternalCode);
        mainEntry.setDocNumber(invoiceRef);
        mainEntry.setDocOrder(1);
        String commStruct = wbInvoice.getCommStruct();
        if (commStruct != null && !commStruct.isEmpty()) {
            mainEntry.setCommentExt(commStruct);
        }

        wbEntries.add(mainEntry);

        BigDecimal eVatTot = BigDecimal.ZERO;
        BigDecimal vatTot = BigDecimal.ZERO;

        int docOrder = 2;
        for (WbInvoiceLine wbInvoiceLine : effectiveInvoiceLines) {
            String operationAccountGl = wbInvoiceLine.getAccountGl();

            BigDecimal eVat = wbInvoiceLine.getEVat();
            BigDecimal vatRate = wbInvoiceLine.getVatRate();
            BigDecimal vat = wbInvoiceLine.getVat();

            BigDecimal newEvatTot = eVatTot.add(eVat);
            System.out.println(eVatTot + " + " + eVat + " = " + newEvatTot);
            eVatTot = newEvatTot;
            vatTot = vatTot.add(vat);

            WbEntry clientWbEntry = mainEntry.clone();

            if (operationAccountGl != null && !operationAccountGl.isEmpty()) {
                clientWbEntry.setAccountGl(operationAccountGl);
            }
            clientWbEntry.setDoc(false);
            clientWbEntry.setWbDocType(WbDocType.IMPUT_GENERAL);
            BigDecimal minEVat = eVat.negate();
            clientWbEntry.setAmountEur(minEVat);
            clientWbEntry.setVatBase(BigDecimal.ZERO);
            clientWbEntry.setVatTax(BigDecimal.ZERO);
            WbVatCode wbVatCode = getInternalVatCode(vatRate, wbClientSupplierType, WbLanguage.FRENCH);
            String internalVatCode = wbVatCode.getInternalVatCode();
            clientWbEntry.setVatImput(internalVatCode);
            clientWbEntry.setDocOrder(docOrder);
            String lineDescription = wbInvoiceLine.getDescription();
            if (lineDescription == null || lineDescription.isEmpty()) {
                lineDescription = invoiceDescription + "+";
            }
            clientWbEntry.setComment(lineDescription);

            wbEntries.add(clientWbEntry);
            docOrder++;
        }

        for (WbInvoiceLine wbInvoiceLine : regroupedInvoiceLines) {
            BigDecimal eVat = wbInvoiceLine.getEVat();
            BigDecimal vat = wbInvoiceLine.getVat();
            BigDecimal minVat = vat.negate();
            BigDecimal vatRate = wbInvoiceLine.getVatRate();
            WbVatCode wbVatCode = getInternalVatCode(vatRate, wbClientSupplierType, WbLanguage.FRENCH);
            String internalVatCode = wbVatCode.getInternalVatCode();
            String vatAccount = wbVatCode.getAccount1();
            String description = wbInvoice.getDescription();

            WbEntry vatWbEntry = mainEntry.clone();
            vatWbEntry.setDoc(false);
            vatWbEntry.setWbDocType(WbDocType.IMPUT_GENERAL);
            vatWbEntry.setWbDocOrderType(WbDocOrderType.VAT);
            vatWbEntry.setVatBase(eVat);
            vatWbEntry.setAmountEur(minVat);
            vatWbEntry.setVatTax(BigDecimal.ZERO);
            vatWbEntry.setVatCode(internalVatCode);
            vatWbEntry.setComment(MessageFormat.format("{0} (tva)", description));
            vatWbEntry.setAccountGl(vatAccount);

            wbEntries.add(vatWbEntry);
        }

        // adjust main
        BigDecimal tot = eVatTot.add(vatTot);
        mainEntry.setAmountEur(tot);
        mainEntry.setVatBase(eVatTot);
        mainEntry.setVatTax(vatTot);

        return wbEntries;
    }

    private String getPeriodInternalCode(Date periodDate) {
        String periodInternalCode;
        _Param param = winbooksCom.param();
        Date effectiveDate;
        if (this.bookYearOverride != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(periodDate);
            calendar.set(Calendar.YEAR, bookYearOverride);
            effectiveDate = calendar.getTime();
        } else {
            effectiveDate = periodDate;
        }
        periodInternalCode = param.periodInternalCode(effectiveDate);
        return periodInternalCode;
    }

    public List<WbEntry> convertInvoicesToEntries(List<WbInvoice> wbInvoices, boolean singleLine) {
        List<WbEntry> allWbEntries = new ArrayList<>();
        for (WbInvoice wbInvoice : wbInvoices) {
            List<WbEntry> wbEntries = convertInvoiceToEntries(wbInvoice, singleLine);
            allWbEntries.addAll(wbEntries);
        }
        return allWbEntries;
    }

    public WbBookYear getBookYear(Date periodDate) {
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(periodDate);
        int year;
        if (bookYearOverride == null) {
            year = calendar.get(Calendar.YEAR);
        } else {
            year = bookYearOverride;
        }
        String yearStr = Integer.toString(year);

        List<WbBookYear> bookYears = getBookYears();
        for (WbBookYear wbBookYear : bookYears) {
            String shortName = wbBookYear.getShortName();
            if (shortName.contains(yearStr)) {
                return wbBookYear;
            }
        }
        return null;
    }

    private List<WbInvoiceLine> regroupInvoiceLines(List<WbInvoiceLine> wbInvoiceLines) {
        Map<BigDecimal, WbInvoiceLine> vatRateInvoiceLineMap = new LinkedHashMap<>();
        for (WbInvoiceLine wbInvoiceLine : wbInvoiceLines) {
            BigDecimal vatRate = wbInvoiceLine.getVatRate();
            WbInvoiceLine groupWbInvoiceLine = vatRateInvoiceLineMap.get(vatRate);
            if (groupWbInvoiceLine == null) {
                String accountGl = wbInvoiceLine.getAccountGl();
                String description = MessageFormat.format("Total TVA {0} %", vatRate);

                groupWbInvoiceLine = new WbInvoiceLine();
                groupWbInvoiceLine.setAccountGl(accountGl);
                groupWbInvoiceLine.setDescription(description);
                groupWbInvoiceLine.setEVat(BigDecimal.ZERO);
                groupWbInvoiceLine.setVatRate(vatRate);
                groupWbInvoiceLine.setVat(BigDecimal.ZERO);

                vatRateInvoiceLineMap.put(vatRate, groupWbInvoiceLine);
            }
            BigDecimal groupEVat = groupWbInvoiceLine.getEVat();
            BigDecimal eVat = wbInvoiceLine.getEVat();
            BigDecimal newGroupEvat = groupEVat.add(eVat);

            BigDecimal groupVat = groupWbInvoiceLine.getVat();
            BigDecimal vat = wbInvoiceLine.getVat();
            BigDecimal newVat = groupVat.add(vat);

            groupWbInvoiceLine.setEVat(newGroupEvat);
            groupWbInvoiceLine.setVat(newVat);
        }

        Collection<WbInvoiceLine> wbInvoiceLineCollection = vatRateInvoiceLineMap.values();
        List<WbInvoiceLine> regroupedInvoiceLines = new ArrayList<>(wbInvoiceLineCollection);
        return regroupedInvoiceLines;
    }

    private _Import testImportInternal(WbImport wbImport) {
        _Import internalImport = winbooksCom._import();
        boolean fileFormatResult = internalImport.fileFormat("TXT");
        checkBooleanResult(fileFormatResult);
        Path tempPath = generateDataFilesInTempDirectory(wbImport);
        String tempPathStr = tempPath.toString();
        System.out.println(tempPath);
        boolean directoryResult = internalImport.directory(tempPathStr);
        checkBooleanResult(directoryResult);
        boolean testResult = internalImport.test();
        checkBooleanResult(testResult);
        return internalImport;
    }

    private WbImportResult createImportResult(_Import internalImport) {
        List<WbWarning> wbWarnings = getWarnings(internalImport);
        List<WbFatalError> wbFatalErrors = getFatalErrors(internalImport);
        // return results
        WbImportResult wbImportResult = new WbImportResult();
        wbImportResult.setWbFatalErrors(wbFatalErrors);
        wbImportResult.setWbWarnings(wbWarnings);
        return wbImportResult;
    }

    // TODO: i18n
    private WbWarningResolution convertTypeSolution(TypeSolution typeSolution) {
        String description;
        switch (typeSolution) {
            case wbToResolve:
                description = "Résoudre";
                break;
            case wbAccept:
                description = "Accepter";
                break;
            case wbReplace:
                description = "Remplacer";
                break;
            case wbBlankRecord:
                description = "Mise à blanc";
                break;
            case wbIgnore:
                description = "Ignorer";
                break;
            case WbDontCopy:
                description = "Ne pas copier";
                break;
            case wbEraseAll:
                description = "Tout effacer";
                break;
            case wbInformation:
                description = "Information";
                break;
            default:
                throw new AssertionError(typeSolution.name());

        }
        WbWarningResolution wbWarningResolution = new WbWarningResolution();
        wbWarningResolution.setTypeSolution(typeSolution);
        wbWarningResolution.setDescription(description);
        return wbWarningResolution;
    }

    public Integer getBookYearOverride() {
        return bookYearOverride;
    }

    public void setBookYearOverride(Integer bookYearOverride) {
        this.bookYearOverride = bookYearOverride;
    }

    public String getBookYearNameOverride() {
        return bookYearNameOverride;
    }

    public void setBookYearNameOverride(String bookYearNameOverride) {
        this.bookYearNameOverride = bookYearNameOverride;
    }

    public String getDossierOverride() {
        return dossierOverride;
    }

    public void setDossierOverride(String dossierOverride) {
        this.dossierOverride = dossierOverride;
    }

    private String trim(String str) {
        if (str == null) {
            return null;
        }
        return str.trim();
    }
}

package be.valuya.jbooks;

import be.valuya.jbooks.exception.WinbooksError;
import be.valuya.jbooks.exception.WinbooksException;
import be.valuya.jbooks.exception.WinbooksInitException;
import be.valuya.jbooks.exception.WinbooksLoginException;
import be.valuya.jbooks.exception.WinbooksOpenBookyearException;
import be.valuya.jbooks.exception.WinbooksOpenDossierException;
import be.valuya.jbooks.model.WbBookYear;
import be.valuya.jbooks.model.WbClientSupplier;
import be.valuya.jbooks.model.WbClientSupplierType;
import be.valuya.jbooks.model.WbGenericWarningResolution;
import be.valuya.jbooks.model.WbImport;
import be.valuya.jbooks.model.WbImportResult;
import be.valuya.jbooks.model.WbInternalOperation;
import be.valuya.jbooks.model.WbLanguage;
import be.valuya.jbooks.model.WbPeriod;
import be.valuya.jbooks.model.WbSpecificWarningResolution;
import be.valuya.jbooks.model.WbWarning;
import be.valuya.winbooks.ClassFactory;
import be.valuya.winbooks.Compte___v0;
import be.valuya.winbooks.TypeSolution;
import be.valuya.winbooks.WinbooksObject___v0;
import be.valuya.winbooks._BookYear;
import be.valuya.winbooks._BookYears;
import be.valuya.winbooks._Comptes;
import be.valuya.winbooks._Dossiers;
import be.valuya.winbooks._ErrorCode;
import be.valuya.winbooks._FatalError;
import be.valuya.winbooks._FatalErrors;
import be.valuya.winbooks._Field;
import be.valuya.winbooks._Fields;
import be.valuya.winbooks._Import;
import be.valuya.winbooks._Param;
import be.valuya.winbooks._Period;
import be.valuya.winbooks._Periods;
import be.valuya.winbooks._Tables;
import be.valuya.winbooks._TablesUser;
import be.valuya.winbooks._Warning;
import be.valuya.winbooks._Warnings;
import com4j.Holder;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Hello world!
 *
 */
public class Winbooks {

    private final WinbooksObject___v0 winbooksCom;

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

    private void login(String userName, String userPassword, WbLanguage wbkLanguage) {
        String languangeStr = wbkLanguage.getValue();
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

    public short openDossier(String shortName) {
        short result = winbooksCom.openDossier(shortName);
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
        short result = winbooksCom.openBookYear(bookYearShortName);
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

    public WbImportResult importData(WbImport wbImport) {
        try {
            _Import internalImport = winbooksCom._import();
            boolean fileFormatResult = internalImport.fileFormat("TXT");
            checkBooleanResult(fileFormatResult);

            Path tempPath = Files.createTempDirectory("wbk");
            List<WbClientSupplier> wbClients = wbImport.getWbClientSupplierList();
            List<WbInternalOperation> wbInternalOperations = wbImport.getWbInternalOperationList();
            generateDataFiles(tempPath, wbClients, wbInternalOperations);

            String tempPathStr = tempPath.toString();
            boolean directoryResult = internalImport.directory(tempPathStr);

            checkBooleanResult(directoryResult);
            boolean testResult = internalImport.test();
            checkBooleanResult(testResult);

            List<WbWarning> wbWarnings = getWarnings(internalImport);
            List<WbFatalError> wbFatalErrors = getFatalErrors(internalImport);

            List<WbGenericWarningResolution> genericWarningResolutions = wbImport.getGenericWarningResolutions();
            List<WbSpecificWarningResolution> specificWarningResolutions = wbImport.getSpecificWarningResolutions();
            mitigateWarnings(internalImport, genericWarningResolutions, specificWarningResolutions);

            short executeResult = internalImport.execute();
            String lastErrorMessage = winbooksCom.lastErrorMessage();
            switch (executeResult) {
                case 0:
                    break;
                case 1:
                    throw new WinbooksOpenBookyearException(WinbooksError.UNTESTED, lastErrorMessage);
                case 2:
                    throw new WinbooksOpenBookyearException(WinbooksError.UNRESOLVED_WARNINGS, lastErrorMessage);
                case 3:
                    throw new WinbooksOpenBookyearException(WinbooksError.FATAL_ERRORS, lastErrorMessage);
                case 4:
                    throw new WinbooksOpenBookyearException(WinbooksError.RESOLUTION_UNSYCHRONIZED, lastErrorMessage);
                default:
                    throw new WinbooksInitException(WinbooksError.UNKNOWN_ERROR, lastErrorMessage);
            }

            // return results
            WbImportResult wbImportResult = new WbImportResult();
            wbImportResult.setWbFatalErrors(wbFatalErrors);
            wbImportResult.setWbWarnings(wbWarnings);

            return wbImportResult;
        } catch (IOException exception) {
            throw new WinbooksException(WinbooksError.UNKNOWN_ERROR, exception);
        }
    }

    private void checkBooleanResult(boolean result) {
        if (!result) {
            String lastErrorMessage = winbooksCom.lastErrorMessage();
            throw new WinbooksException(WinbooksError.UNKNOWN_ERROR, lastErrorMessage);
        }
    }

    private void generateDataFiles(Path path, List<WbClientSupplier> wbClients, List<WbInternalOperation> wbInternalOperations) {
        //TODO
    }

    private List<WbWarning> getWarnings(_Import wbImport) {
        List<WbWarning> wbWarnings = new ArrayList<WbWarning>();

        _Warnings internalWarnings = wbImport.warnings();
        short warningCount = internalWarnings.count();
        for (short i = 0; i < warningCount; i++) {
            _Warning internalWarning = internalWarnings.item(i);
            WbWarning wbWarning = convertInternalWarning(internalWarning, wbImport);

            wbWarnings.add(wbWarning);
        }

        return wbWarnings;
    }

    private List<WbFatalError> getFatalErrors(_Import wbImport) {
        List<WbFatalError> wbFatalErrors = new ArrayList<WbFatalError>();

        _FatalErrors internalFatalErrors = wbImport.fatalErrors();
        short errorCount = internalFatalErrors.count();
        for (short i = 0; i < errorCount; i++) {
            _FatalError internalFatalError = internalFatalErrors.item(i);
            WbFatalError wbFatalError = convertInternalFatalError(internalFatalError, wbImport);
            wbFatalErrors.add(wbFatalError);
        }

        return wbFatalErrors;
    }

    private void mitigateWarnings(_Import wbImport, List<WbGenericWarningResolution> genericWarningResolutions, List<WbSpecificWarningResolution> specificWarningResolutions) {

        for (WbGenericWarningResolution genericWarningResolution : genericWarningResolutions) {
            String code = genericWarningResolution.getCode();
            TypeSolution typeSolution = genericWarningResolution.getTypeSolution();
            _ErrorCode internalErrorCode = wbImport.errorCodes(code);
            internalErrorCode.setResolution(typeSolution);
        }

        Map<WbWarning, TypeSolution> specificWarningResolutionMap = new HashMap<WbWarning, TypeSolution>();
        for (WbSpecificWarningResolution specificWarningResolution : specificWarningResolutions) {
            WbWarning wbWarning = specificWarningResolution.getWarning();
            TypeSolution typeSolution = specificWarningResolution.getTypeSolution();
            specificWarningResolutionMap.put(wbWarning, typeSolution);
        }

        _Warnings internalWarnings = wbImport.warnings();
        short warningCount = internalWarnings.count();
        for (short i = 0; i < warningCount; i++) {
            _Warning internalWarning = internalWarnings.item(i);
            WbWarning wbWarning = convertInternalWarning(internalWarning, wbImport);
            TypeSolution typeSolution = specificWarningResolutionMap.get(wbWarning);
            Holder<TypeSolution> typeSolutionHolder = new Holder<TypeSolution>(typeSolution);
            internalWarning.setResolution(typeSolutionHolder);
        }
    }

    private WbFatalError convertInternalFatalError(_FatalError internalFatalError, _Import wbImport) {
        String code = internalFatalError.code();
        String param = internalFatalError.param();
        _ErrorCode internalErrorCode = wbImport.errorCodes(code);
        String description = internalErrorCode.description();
        WbFatalError wbFatalError = new WbFatalError();
        wbFatalError.setCode(code);
        wbFatalError.setTarget(param);
        wbFatalError.setDescription(description);
        return wbFatalError;
    }

    private WbWarning convertInternalWarning(_Warning internalWarning, _Import wbImport) {
        String code = internalWarning.code();
        String param = internalWarning.param();
        _ErrorCode internalErrorCode = wbImport.errorCodes(code);
        String concatenedAllowableAction = internalErrorCode.allowableActions();
        String[] allowableActionStrArray = concatenedAllowableAction.split(" ");
        Set<TypeSolution> typeSolutions = EnumSet.noneOf(TypeSolution.class);
        for (String allowableActionStr : allowableActionStrArray) {
            TypeSolution typeSolution = TypeSolution.valueOf(allowableActionStr);
            typeSolutions.add(typeSolution);
        }
        String description = internalErrorCode.description();
        WbWarning wbWarning = new WbWarning();
        wbWarning.setCode(code);
        wbWarning.setTarget(param);
        wbWarning.setTypeSolutions(typeSolutions);
        wbWarning.setDescription(description);
        return wbWarning;
    }

    public String getInternalVatCode(BigDecimal vatRate, WbClientSupplierType wbClientSupplierType, WbLanguage wbLanguage) {
        _Param param = winbooksCom.param();
        String vatRateStr = vatRate.toPlainString();
        String clientSupplierTypeStr = wbClientSupplierType.getValue();
        String langStr = wbLanguage.getValue();
        Holder<String> langHolder = new Holder<String>(langStr);
        String internalVatCode = param.vatInternalCode(vatRateStr, clientSupplierTypeStr, langHolder);
        return internalVatCode;
    }

    public String getDllVersion() {
        String dllVersion = winbooksCom.getDllVersion();
        return dllVersion;
    }

    public String getDllCompilDate() {
        String dllCompilDate = winbooksCom.getDllCompilDate();
        return dllCompilDate;
    }

    public List<WbBookYear> getBookYears() {
        List<WbBookYear> wbBookYears = new ArrayList<WbBookYear>();

        _BookYears bookYears = winbooksCom.bookYear();
        short bookYearCount = bookYears.count();
        for (short yearIndex = 1; yearIndex <= bookYearCount; yearIndex++) {
            _BookYear bookYear = bookYears.item(yearIndex);
            String shortName = bookYear.shortName();
            String longName = bookYear.longName();

            WbBookYear wbBookYear = new WbBookYear();
            wbBookYear.setShortName(shortName);
            wbBookYear.setLongName(longName);

            wbBookYears.add(wbBookYear);
        }

        return wbBookYears;
    }

    public List<WbPeriod> getBookYearPeriods(WbBookYear bookYearIndex) {
        List<WbPeriod> wbPeriods = new ArrayList<>();

        int index = bookYearIndex.getIndex();
        _BookYear internalBookYear = winbooksCom.bookYear((short) index);
        _Periods periods = internalBookYear.periods();
        short periodCount = periods.count();
        for (short periodIndex = 1; periodIndex <= periodCount; periodIndex++) {
            _Period period = internalBookYear.periods(periodIndex);
            String periodName = period.name();
            String numOfPeriod = period.numOfPeriod(periodName);

            WbPeriod wbPeriod = new WbPeriod();
            wbPeriod.setName(periodName);
            wbPeriod.setNum(numOfPeriod);

        }

        return wbPeriods;
    }

    public void doStuff() {
        _Comptes comptes = winbooksCom.accounts();
        comptes.calcSum();
        System.out.println(comptes);

        _Dossiers dossiers = winbooksCom.companies();
        if (dossiers != null) {
            String[] allCompanies = dossiers.getAllCompanies();
            System.out.println("companies:" + allCompanies);
        }

        _Comptes customers = winbooksCom.customers();
        Compte___v0 compte = winbooksCom.customers("ALPHA1");
        System.out.println("compte: " + compte);
        System.out.println("name: " + compte.fieldvalue("NAME1"));

        _BookYears bookYears = winbooksCom.bookYear();
        short bookYearCount = bookYears.count();
        for (short yearIndex = 1; yearIndex <= bookYearCount; yearIndex++) {
            _BookYear bookYear = bookYears.item(yearIndex);
            String shortName = bookYear.shortName();
            String longName = bookYear.longName();
            WbBookYear wbBookYear = new WbBookYear();
            wbBookYear.setShortName(shortName);
            wbBookYear.setLongName(longName);

            System.out.println("bookyear: " + shortName + ": " + longName);
            _Periods periods = bookYear.periods();
            short periodCount = periods.count();
            for (short periodIndex = 1; periodIndex <= periodCount; periodIndex++) {
                _Period period = bookYear.periods(periodIndex);
                String periodName = period.name();
                String numOfPeriod = period.numOfPeriod(periodName);
                System.out.println("period " + numOfPeriod + ": " + periodName);
            }
        }

        String dllVersion = getDllVersion();
        System.out.println(dllVersion);
        String dllCompilDate = getDllCompilDate();
        System.out.println(dllCompilDate);

        _Tables tables = winbooksCom.tables();
        _TablesUser catCustomer = tables.zipCode();
        _Fields fields = catCustomer.fields();
        short fieldCount = fields.count();
        for (short fieldIndex = 0; fieldIndex < fieldCount; fieldIndex++) {
            _Field field = fields.item(Short.toString(fieldIndex));
            String name = field.name();
            System.out.println("field: " + name);
        }
    }

    public static void main(String... args) {
        Winbooks winbooks = new Winbooks();
        winbooks.login("SYSTEM", "", WbLanguage.FRENCH);
        winbooks.openDossier("PARFILUX");
        winbooks.openBookYear("Ex. 2003");
        winbooks.doStuff();

        WbClientSupplier wbClientSupplier1 = new WbClientSupplier();
        wbClientSupplier1.setName1("Jean");
        wbClientSupplier1.setName2("Dupont");
        wbClientSupplier1.setBnkAccnt("063-9818425-23");
        wbClientSupplier1.setZipCode("1348");
        wbClientSupplier1.setCity("Louvain-la-Neuve");
        wbClientSupplier1.setCivName1("Mr");
        wbClientSupplier1.setCountry("BE");
        wbClientSupplier1.setTelNumber("0498707213");
        wbClientSupplier1.setNumber("DUPONT");
        //importData(...)

        List<WbClientSupplier> wbClientSupplierList = new ArrayList<WbClientSupplier>();
        wbClientSupplierList.add(wbClientSupplier1);

        WbImport wbImport = new WbImport();
        wbImport.setWbClientSupplierList(wbClientSupplierList);
        wbImport.setWbInternalOperationList(null);

    }
}

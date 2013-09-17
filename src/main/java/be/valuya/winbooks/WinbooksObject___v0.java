package be.valuya.winbooks  ;

import com4j.*;

@IID("{113AF565-A3D5-4A16-BD15-049123BD0567}")
public interface WinbooksObject___v0 extends Com4jObject {
  // Methods:
  /**
   * <p>
   * Getter method for the COM property "Customers"
   * </p>
   * @return  Returns a value of type be.valuya.winbooks._Comptes
   */

  @DISPID(1073938432) //= 0x40030000. The runtime will prefer the VTID if present
  @VTID(7)
  be.valuya.winbooks._Comptes customers();


  @VTID(7)
  @ReturnValue(defaultPropertyThrough={be.valuya.winbooks._Comptes.class})
  be.valuya.winbooks.Compte___v0 customers(
    java.lang.String range);

  /**
   * <p>
   * Setter method for the COM property "Customers"
   * </p>
   * @param customers Mandatory be.valuya.winbooks._Comptes parameter.
   */

  @DISPID(1073938432) //= 0x40030000. The runtime will prefer the VTID if present
  @VTID(9)
  void customers(
    be.valuya.winbooks._Comptes customers);


  /**
   * <p>
   * Getter method for the COM property "Suppliers"
   * </p>
   * @return  Returns a value of type be.valuya.winbooks._Comptes
   */

  @DISPID(1073938433) //= 0x40030001. The runtime will prefer the VTID if present
  @VTID(10)
  be.valuya.winbooks._Comptes suppliers();


  @VTID(10)
  @ReturnValue(defaultPropertyThrough={be.valuya.winbooks._Comptes.class})
  be.valuya.winbooks.Compte___v0 suppliers(
    java.lang.String range);

  /**
   * <p>
   * Setter method for the COM property "Suppliers"
   * </p>
   * @param suppliers Mandatory be.valuya.winbooks._Comptes parameter.
   */

  @DISPID(1073938433) //= 0x40030001. The runtime will prefer the VTID if present
  @VTID(12)
  void suppliers(
    be.valuya.winbooks._Comptes suppliers);


  /**
   * <p>
   * Getter method for the COM property "Accounts"
   * </p>
   * @return  Returns a value of type be.valuya.winbooks._Comptes
   */

  @DISPID(1073938434) //= 0x40030002. The runtime will prefer the VTID if present
  @VTID(13)
  be.valuya.winbooks._Comptes accounts();


  @VTID(13)
  @ReturnValue(defaultPropertyThrough={be.valuya.winbooks._Comptes.class})
  be.valuya.winbooks.Compte___v0 accounts(
    java.lang.String range);

  /**
   * <p>
   * Setter method for the COM property "Accounts"
   * </p>
   * @param accounts Mandatory be.valuya.winbooks._Comptes parameter.
   */

  @DISPID(1073938434) //= 0x40030002. The runtime will prefer the VTID if present
  @VTID(15)
  void accounts(
    be.valuya.winbooks._Comptes accounts);


  /**
   * <p>
   * Getter method for the COM property "Analytical"
   * </p>
   * @return  Returns a value of type be.valuya.winbooks._SocAnalyticalParam
   */

  @DISPID(1073938435) //= 0x40030003. The runtime will prefer the VTID if present
  @VTID(16)
  be.valuya.winbooks._SocAnalyticalParam analytical();


  /**
   * <p>
   * Setter method for the COM property "Analytical"
   * </p>
   * @param analytical Mandatory be.valuya.winbooks._SocAnalyticalParam parameter.
   */

  @DISPID(1073938435) //= 0x40030003. The runtime will prefer the VTID if present
  @VTID(18)
  void analytical(
    be.valuya.winbooks._SocAnalyticalParam analytical);


  /**
   * <p>
   * Getter method for the COM property "CustomerTrans"
   * </p>
   * @return  Returns a value of type be.valuya.winbooks._Transactions
   */

  @DISPID(1073938436) //= 0x40030004. The runtime will prefer the VTID if present
  @VTID(19)
  be.valuya.winbooks._Transactions customerTrans();


  @VTID(19)
  @ReturnValue(defaultPropertyThrough={be.valuya.winbooks._Transactions.class})
  be.valuya.winbooks.Transaction___v0 customerTrans(
    java.lang.String range);

  /**
   * <p>
   * Setter method for the COM property "CustomerTrans"
   * </p>
   * @param customerTrans Mandatory be.valuya.winbooks._Transactions parameter.
   */

  @DISPID(1073938436) //= 0x40030004. The runtime will prefer the VTID if present
  @VTID(21)
  void customerTrans(
    be.valuya.winbooks._Transactions customerTrans);


  /**
   * <p>
   * Getter method for the COM property "CustTrans"
   * </p>
   * @return  Returns a value of type be.valuya.winbooks.Transaction___v0
   */

  @DISPID(1073938437) //= 0x40030005. The runtime will prefer the VTID if present
  @VTID(22)
  be.valuya.winbooks.Transaction___v0 custTrans();


  /**
   * <p>
   * Setter method for the COM property "CustTrans"
   * </p>
   * @param custTrans Mandatory be.valuya.winbooks.Transaction___v0 parameter.
   */

  @DISPID(1073938437) //= 0x40030005. The runtime will prefer the VTID if present
  @VTID(24)
  void custTrans(
    be.valuya.winbooks.Transaction___v0 custTrans);


  /**
   * <p>
   * Getter method for the COM property "SupplierTrans"
   * </p>
   * @return  Returns a value of type be.valuya.winbooks._Transactions
   */

  @DISPID(1073938438) //= 0x40030006. The runtime will prefer the VTID if present
  @VTID(25)
  be.valuya.winbooks._Transactions supplierTrans();


  @VTID(25)
  @ReturnValue(defaultPropertyThrough={be.valuya.winbooks._Transactions.class})
  be.valuya.winbooks.Transaction___v0 supplierTrans(
    java.lang.String range);

  /**
   * <p>
   * Setter method for the COM property "SupplierTrans"
   * </p>
   * @param supplierTrans Mandatory be.valuya.winbooks._Transactions parameter.
   */

  @DISPID(1073938438) //= 0x40030006. The runtime will prefer the VTID if present
  @VTID(27)
  void supplierTrans(
    be.valuya.winbooks._Transactions supplierTrans);


  /**
   * <p>
   * Getter method for the COM property "AccountTrans"
   * </p>
   * @return  Returns a value of type be.valuya.winbooks._Transactions
   */

  @DISPID(1073938439) //= 0x40030007. The runtime will prefer the VTID if present
  @VTID(28)
  be.valuya.winbooks._Transactions accountTrans();


  @VTID(28)
  @ReturnValue(defaultPropertyThrough={be.valuya.winbooks._Transactions.class})
  be.valuya.winbooks.Transaction___v0 accountTrans(
    java.lang.String range);

  /**
   * <p>
   * Setter method for the COM property "AccountTrans"
   * </p>
   * @param accountTrans Mandatory be.valuya.winbooks._Transactions parameter.
   */

  @DISPID(1073938439) //= 0x40030007. The runtime will prefer the VTID if present
  @VTID(30)
  void accountTrans(
    be.valuya.winbooks._Transactions accountTrans);


  /**
   * <p>
   * Getter method for the COM property "AnalyticalTrans"
   * </p>
   * @return  Returns a value of type be.valuya.winbooks.Analytiques___v0
   */

  @DISPID(1073938440) //= 0x40030008. The runtime will prefer the VTID if present
  @VTID(31)
  be.valuya.winbooks.Analytiques___v0 analyticalTrans();


  /**
   * <p>
   * Setter method for the COM property "AnalyticalTrans"
   * </p>
   * @param analyticalTrans Mandatory be.valuya.winbooks.Analytiques___v0 parameter.
   */

  @DISPID(1073938440) //= 0x40030008. The runtime will prefer the VTID if present
  @VTID(33)
  void analyticalTrans(
    be.valuya.winbooks.Analytiques___v0 analyticalTrans);


  /**
   * <p>
   * Getter method for the COM property "Tables"
   * </p>
   * @return  Returns a value of type be.valuya.winbooks._Tables
   */

  @DISPID(1073938441) //= 0x40030009. The runtime will prefer the VTID if present
  @VTID(34)
  be.valuya.winbooks._Tables tables();


  /**
   * <p>
   * Setter method for the COM property "Tables"
   * </p>
   * @param tables Mandatory be.valuya.winbooks._Tables parameter.
   */

  @DISPID(1073938441) //= 0x40030009. The runtime will prefer the VTID if present
  @VTID(36)
  void tables(
    be.valuya.winbooks._Tables tables);


  /**
   * <p>
   * Getter method for the COM property "Import"
   * </p>
   * @return  Returns a value of type be.valuya.winbooks._Import
   */

  @DISPID(1073938442) //= 0x4003000a. The runtime will prefer the VTID if present
  @VTID(37)
  be.valuya.winbooks._Import _import();


  /**
   * <p>
   * Setter method for the COM property "Import"
   * </p>
   * @param _import Mandatory be.valuya.winbooks._Import parameter.
   */

  @DISPID(1073938442) //= 0x4003000a. The runtime will prefer the VTID if present
  @VTID(39)
  void _import(
    be.valuya.winbooks._Import _import);


  /**
   * <p>
   * Getter method for the COM property "BookYear"
   * </p>
   * @return  Returns a value of type be.valuya.winbooks._BookYears
   */

  @DISPID(1073938443) //= 0x4003000b. The runtime will prefer the VTID if present
  @VTID(40)
  be.valuya.winbooks._BookYears bookYear();


  @VTID(40)
  @ReturnValue(defaultPropertyThrough={be.valuya.winbooks._BookYears.class})
  be.valuya.winbooks._BookYear bookYear(
    short index);

  /**
   * <p>
   * Setter method for the COM property "BookYear"
   * </p>
   * @param bookYear Mandatory be.valuya.winbooks._BookYears parameter.
   */

  @DISPID(1073938443) //= 0x4003000b. The runtime will prefer the VTID if present
  @VTID(42)
  void bookYear(
    be.valuya.winbooks._BookYears bookYear);


  /**
   * <p>
   * Getter method for the COM property "Options"
   * </p>
   * @return  Returns a value of type be.valuya.winbooks._Options
   */

  @DISPID(1073938444) //= 0x4003000c. The runtime will prefer the VTID if present
  @VTID(43)
  be.valuya.winbooks._Options options();


  /**
   * <p>
   * Setter method for the COM property "Options"
   * </p>
   * @param options Mandatory be.valuya.winbooks._Options parameter.
   */

  @DISPID(1073938444) //= 0x4003000c. The runtime will prefer the VTID if present
  @VTID(45)
  void options(
    be.valuya.winbooks._Options options);


  /**
   * <p>
   * Getter method for the COM property "Sql"
   * </p>
   * @return  Returns a value of type be.valuya.winbooks.SqlClass___v0
   */

  @DISPID(1073938445) //= 0x4003000d. The runtime will prefer the VTID if present
  @VTID(46)
  be.valuya.winbooks.SqlClass___v0 sql();


  /**
   * <p>
   * Setter method for the COM property "Sql"
   * </p>
   * @param sql Mandatory be.valuya.winbooks.SqlClass___v0 parameter.
   */

  @DISPID(1073938445) //= 0x4003000d. The runtime will prefer the VTID if present
  @VTID(48)
  void sql(
    be.valuya.winbooks.SqlClass___v0 sql);


  /**
   * <p>
   * Getter method for the COM property "Param"
   * </p>
   * @return  Returns a value of type be.valuya.winbooks._Param
   */

  @DISPID(1073938446) //= 0x4003000e. The runtime will prefer the VTID if present
  @VTID(49)
  be.valuya.winbooks._Param param();


  /**
   * <p>
   * Setter method for the COM property "Param"
   * </p>
   * @param param Mandatory be.valuya.winbooks._Param parameter.
   */

  @DISPID(1073938446) //= 0x4003000e. The runtime will prefer the VTID if present
  @VTID(51)
  void param(
    be.valuya.winbooks._Param param);


  /**
   * <p>
   * Getter method for the COM property "Utilities"
   * </p>
   * @return  Returns a value of type be.valuya.winbooks._WbApiUtilities
   */

  @DISPID(1073938447) //= 0x4003000f. The runtime will prefer the VTID if present
  @VTID(52)
  be.valuya.winbooks._WbApiUtilities utilities();


  /**
   * <p>
   * Setter method for the COM property "Utilities"
   * </p>
   * @param utilities Mandatory be.valuya.winbooks._WbApiUtilities parameter.
   */

  @DISPID(1073938447) //= 0x4003000f. The runtime will prefer the VTID if present
  @VTID(54)
  void utilities(
    be.valuya.winbooks._WbApiUtilities utilities);


  /**
   * <p>
   * Getter method for the COM property "Companies"
   * </p>
   * @return  Returns a value of type be.valuya.winbooks._Dossiers
   */

  @DISPID(1073938448) //= 0x40030010. The runtime will prefer the VTID if present
  @VTID(55)
  be.valuya.winbooks._Dossiers companies();


  @VTID(55)
  @ReturnValue(defaultPropertyThrough={be.valuya.winbooks._Dossiers.class})
  be.valuya.winbooks._Dossier companies(
    short index);

  /**
   * <p>
   * Setter method for the COM property "Companies"
   * </p>
   * @param companies Mandatory be.valuya.winbooks._Dossiers parameter.
   */

  @DISPID(1073938448) //= 0x40030010. The runtime will prefer the VTID if present
  @VTID(57)
  void companies(
    be.valuya.winbooks._Dossiers companies);


  /**
   * <p>
   * Getter method for the COM property "logSkipRecord"
   * </p>
   * @return  Returns a value of type java.lang.String
   */

  @DISPID(1073938458) //= 0x4003001a. The runtime will prefer the VTID if present
  @VTID(78)
  java.lang.String logSkipRecord();


  /**
   * <p>
   * Setter method for the COM property "logSkipRecord"
   * </p>
   * @param logSkipRecord Mandatory java.lang.String parameter.
   */

  @DISPID(1073938458) //= 0x4003001a. The runtime will prefer the VTID if present
  @VTID(79)
  void logSkipRecord(
    java.lang.String logSkipRecord);


  /**
   * <p>
   * Getter method for the COM property "LastErrorMessage"
   * </p>
   * @return  Returns a value of type java.lang.String
   */

  @DISPID(1745027077) //= 0x68030005. The runtime will prefer the VTID if present
  @VTID(58)
  java.lang.String lastErrorMessage();


  /**
   * <p>
   * Setter method for the COM property "ActiveLanguage"
   * </p>
   * @param rhs Mandatory be.valuya.winbooks.LanguageDll parameter.
   */

  @DISPID(1745027076) //= 0x68030004. The runtime will prefer the VTID if present
  @VTID(59)
  void activeLanguage(
    be.valuya.winbooks.LanguageDll rhs);


  /**
   * @return  Returns a value of type java.lang.String
   */

  @DISPID(1610809350) //= 0x60030006. The runtime will prefer the VTID if present
  @VTID(60)
  java.lang.String returnLanguage();


  /**
   * @return  Returns a value of type short
   */

  @DISPID(1610809351) //= 0x60030007. The runtime will prefer the VTID if present
  @VTID(61)
  short init();


  /**
   * @return  Returns a value of type short
   */

  @DISPID(1610809352) //= 0x60030008. The runtime will prefer the VTID if present
  @VTID(62)
  short initLocal();


  /**
   * @param userName Mandatory java.lang.String parameter.
   * @param userPassword Mandatory java.lang.String parameter.
   * @param userLanguage Optional parameter. Default value is ""
   * @return  Returns a value of type short
   */

  @DISPID(1610809353) //= 0x60030009. The runtime will prefer the VTID if present
  @VTID(63)
  short login(
    java.lang.String userName,
    java.lang.String userPassword,
    @Optional java.lang.String userLanguage);


  /**
   * @param shortName Mandatory java.lang.String parameter.
   * @return  Returns a value of type short
   */

  @DISPID(1610809354) //= 0x6003000a. The runtime will prefer the VTID if present
  @VTID(64)
  short openDossier(
    java.lang.String shortName);


  /**
   */

  @DISPID(1610809356) //= 0x6003000c. The runtime will prefer the VTID if present
  @VTID(65)
  void loadFieldDefs();


  /**
   * @param bookYearShortName Mandatory java.lang.String parameter.
   * @return  Returns a value of type short
   */

  @DISPID(1610809358) //= 0x6003000e. The runtime will prefer the VTID if present
  @VTID(66)
  short openBookYear(
    java.lang.String bookYearShortName);


  /**
   * <p>
   * Setter method for the COM property "SetPath"
   * </p>
   * @param rhs Mandatory java.lang.String parameter.
   */

  @DISPID(1745027075) //= 0x68030003. The runtime will prefer the VTID if present
  @VTID(67)
  void setPath(
    java.lang.String rhs);


  /**
   */

  @DISPID(1610809362) //= 0x60030012. The runtime will prefer the VTID if present
  @VTID(68)
  void closeDossier();


  /**
   * @param shortName Mandatory java.lang.String parameter.
   * @return  Returns a value of type java.lang.String
   */

  @DISPID(1610809363) //= 0x60030013. The runtime will prefer the VTID if present
  @VTID(69)
  java.lang.String getDossierPath(
    java.lang.String shortName);


  /**
   */

  @DISPID(1610809364) //= 0x60030014. The runtime will prefer the VTID if present
  @VTID(70)
  void freeClipBoard();


  /**
   * @param str_buffer1 Mandatory java.lang.String parameter.
   */

  @DISPID(1610809365) //= 0x60030015. The runtime will prefer the VTID if present
  @VTID(71)
  void putInClipBoard(
    java.lang.String str_buffer1);


  /**
   * <p>
   * Getter method for the COM property "Version"
   * </p>
   * @return  Returns a value of type java.lang.String
   */

  @DISPID(1745027074) //= 0x68030002. The runtime will prefer the VTID if present
  @VTID(72)
  java.lang.String version();


  /**
   * <p>
   * Return version number
   * </p>
   * @return  Returns a value of type java.lang.String
   */

  @DISPID(1610809367) //= 0x60030017. The runtime will prefer the VTID if present
  @VTID(73)
  java.lang.String getDllVersion();


  /**
   * <p>
   * return compil date format DD/MM/YYYY
   * </p>
   * @return  Returns a value of type java.lang.String
   */

  @DISPID(1610809368) //= 0x60030018. The runtime will prefer the VTID if present
  @VTID(74)
  java.lang.String getDllCompilDate();


  /**
   * @return  Returns a value of type java.lang.String
   */

  @DISPID(1610809369) //= 0x60030019. The runtime will prefer the VTID if present
  @VTID(75)
  java.lang.String getDllInstallationPath();


  /**
   * <p>
   * Setter method for the COM property "GetEncryptionPwd"
   * </p>
   * @param rhs Mandatory java.lang.String parameter.
   */

  @DISPID(1745027073) //= 0x68030001. The runtime will prefer the VTID if present
  @VTID(76)
  void getEncryptionPwd(
    java.lang.String rhs);


  /**
   * <p>
   * Getter method for the COM property "IsFullAccess"
   * </p>
   * @return  Returns a value of type boolean
   */

  @DISPID(1745027072) //= 0x68030000. The runtime will prefer the VTID if present
  @VTID(77)
  boolean isFullAccess();


  /**
   * <p>
   * Setter method for the COM property "SkipRecordLocked"
   * </p>
   * @param rhs Mandatory boolean parameter.
   */

  @DISPID(1745027098) //= 0x6803001a. The runtime will prefer the VTID if present
  @VTID(80)
  void skipRecordLocked(
    boolean rhs);


  /**
   * <p>
   * Getter method for the COM property "SkipRecordLocked"
   * </p>
   * @return  Returns a value of type boolean
   */

  @DISPID(1745027098) //= 0x6803001a. The runtime will prefer the VTID if present
  @VTID(81)
  boolean skipRecordLocked();


  // Properties:
}

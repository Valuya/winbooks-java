package be.valuya.winbooks  ;

import com4j.*;

@IID("{4D934226-A202-4A64-9578-B96F1038C2A3}")
public interface _Param extends Com4jObject {
  // Methods:
  /**
   * <p>
   * Getter method for the COM property "BookYears"
   * </p>
   * @return  Returns a value of type be.valuya.winbooks._BookYears
   */

  @DISPID(1073938432) //= 0x40030000. The runtime will prefer the VTID if present
  @VTID(7)
  be.valuya.winbooks._BookYears bookYears();


  @VTID(7)
  @ReturnValue(defaultPropertyThrough={be.valuya.winbooks._BookYears.class})
  be.valuya.winbooks._BookYear bookYears(
    short index);

  /**
   * <p>
   * Setter method for the COM property "BookYears"
   * </p>
   * @param bookYears Mandatory be.valuya.winbooks._BookYears parameter.
   */

  @DISPID(1073938432) //= 0x40030000. The runtime will prefer the VTID if present
  @VTID(9)
  void bookYears(
    be.valuya.winbooks._BookYears bookYears);


  /**
   * <p>
   * Getter method for the COM property "ShortName"
   * </p>
   * @return  Returns a value of type java.lang.String
   */

  @DISPID(1745027077) //= 0x68030005. The runtime will prefer the VTID if present
  @VTID(10)
  java.lang.String shortName();


  /**
   * <p>
   * Getter method for the COM property "PathName"
   * </p>
   * @return  Returns a value of type java.lang.String
   */

  @DISPID(1745027076) //= 0x68030004. The runtime will prefer the VTID if present
  @VTID(11)
  java.lang.String pathName();


  /**
   * <p>
   * Getter method for the COM property "PathExcel"
   * </p>
   * @return  Returns a value of type java.lang.String
   */

  @DISPID(1745027075) //= 0x68030003. The runtime will prefer the VTID if present
  @VTID(12)
  java.lang.String pathExcel();


  /**
   * <p>
   * Getter method for the COM property "CurrentBookYearNumber"
   * </p>
   * @return  Returns a value of type short
   */

  @DISPID(1745027074) //= 0x68030002. The runtime will prefer the VTID if present
  @VTID(13)
  short currentBookYearNumber();


  /**
   * <p>
   * Getter method for the COM property "CurrentBookYearNumBis"
   * </p>
   * @param exName Mandatory java.lang.String parameter.
   * @return  Returns a value of type short
   */

  @DISPID(1745027073) //= 0x68030001. The runtime will prefer the VTID if present
  @VTID(14)
  short currentBookYearNumBis(
    java.lang.String exName);


  /**
   * <p>
   * Getter method for the COM property "Value"
   * </p>
   * @param paramName Mandatory java.lang.String parameter.
   * @return  Returns a value of type java.lang.String
   */

  @DISPID(1745027072) //= 0x68030000. The runtime will prefer the VTID if present
  @VTID(15)
  java.lang.String value(
    java.lang.String paramName);


  /**
   * @param strID Mandatory java.lang.String parameter.
   * @return  Returns a value of type java.lang.String
   */

  @DISPID(1610809350) //= 0x60030006. The runtime will prefer the VTID if present
  @VTID(16)
  java.lang.String getParamValue(
    java.lang.String strID);


  /**
   * @param codeUser Mandatory java.lang.String parameter.
   * @param cliFou Mandatory java.lang.String parameter.
   * @param langue Optional parameter. Default value is ""
   * @return  Returns a value of type java.lang.String
   */

  @DISPID(1610809351) //= 0x60030007. The runtime will prefer the VTID if present
  @VTID(17)
  java.lang.String vatInternalCode(
    java.lang.String codeUser,
    java.lang.String cliFou,
    @Optional Holder<java.lang.String> langue);


  /**
   * @param ccDate Mandatory java.util.Date parameter.
   * @return  Returns a value of type java.lang.String
   */

  @DISPID(1610809352) //= 0x60030008. The runtime will prefer the VTID if present
  @VTID(18)
  java.lang.String periodInternalCode(
    java.util.Date ccDate);


  /**
   * @return  Returns a value of type boolean
   */

  @DISPID(1610809353) //= 0x60030009. The runtime will prefer the VTID if present
  @VTID(19)
  boolean isFolderOpen();


  /**
   * @return  Returns a value of type java.lang.String
   */

  @DISPID(1610809354) //= 0x6003000a. The runtime will prefer the VTID if present
  @VTID(20)
  java.lang.String isNetwork();


  /**
   * <p>
   * Return TRUE if asset has been installed
   * </p>
   * @return  Returns a value of type boolean
   */

  @DISPID(1610809355) //= 0x6003000b. The runtime will prefer the VTID if present
  @VTID(21)
  boolean isAsset();


  /**
   * @param internCodeVat Mandatory java.lang.String parameter.
   * @return  Returns a value of type java.lang.Object
   */

  @DISPID(1610809356) //= 0x6003000c. The runtime will prefer the VTID if present
  @VTID(22)
  @ReturnValue(type=NativeType.VARIANT)
  java.lang.Object vatRate(
    java.lang.String internCodeVat);


  /**
   * @param internCodeVat Mandatory java.lang.String parameter.
   * @return  Returns a value of type java.lang.String
   */

  @DISPID(1610809357) //= 0x6003000d. The runtime will prefer the VTID if present
  @VTID(23)
  java.lang.String vatAcc1(
    java.lang.String internCodeVat);


  /**
   * @param internCodeVat Mandatory java.lang.String parameter.
   * @return  Returns a value of type java.lang.String
   */

  @DISPID(1610809358) //= 0x6003000e. The runtime will prefer the VTID if present
  @VTID(24)
  java.lang.String vatAcc2(
    java.lang.String internCodeVat);


  /**
   * @param internalCode Mandatory java.lang.String parameter.
   * @param lLanguage Optional parameter. Default value is 0
   * @return  Returns a value of type java.lang.Object
   */

  @DISPID(1610809359) //= 0x6003000f. The runtime will prefer the VTID if present
  @VTID(25)
  @ReturnValue(type=NativeType.VARIANT)
  java.lang.Object vatExternalCode(
    java.lang.String internalCode,
    @Optional @DefaultValue("0") be.valuya.winbooks.LangueforVat lLanguage);


  // Properties:
}

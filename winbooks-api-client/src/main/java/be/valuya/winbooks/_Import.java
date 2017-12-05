package be.valuya.winbooks  ;

import com4j.*;

@IID("{069CCD5D-A337-4E34-9E51-8EC49D42A646}")
public interface _Import extends Com4jObject {
  // Methods:
  /**
   * <p>
   * Getter method for the COM property "ErrorCodes"
   * </p>
   * @return  Returns a value of type be.valuya.winbooks._ErrorCodes
   */

  @DISPID(1073938432) //= 0x40030000. The runtime will prefer the VTID if present
  @VTID(7)
  be.valuya.winbooks._ErrorCodes errorCodes();


  @VTID(7)
  @ReturnValue(defaultPropertyThrough={be.valuya.winbooks._ErrorCodes.class})
  be.valuya.winbooks._ErrorCode errorCodes(
    java.lang.String the_Error);

  /**
   * <p>
   * Setter method for the COM property "ErrorCodes"
   * </p>
   * @param errorCodes Mandatory be.valuya.winbooks._ErrorCodes parameter.
   */

  @DISPID(1073938432) //= 0x40030000. The runtime will prefer the VTID if present
  @VTID(9)
  void errorCodes(
    be.valuya.winbooks._ErrorCodes errorCodes);


  /**
   * <p>
   * Getter method for the COM property "Warnings"
   * </p>
   * @return  Returns a value of type be.valuya.winbooks._Warnings
   */

  @DISPID(1073938433) //= 0x40030001. The runtime will prefer the VTID if present
  @VTID(10)
  be.valuya.winbooks._Warnings warnings();


  @VTID(10)
  @ReturnValue(defaultPropertyThrough={be.valuya.winbooks._Warnings.class})
  be.valuya.winbooks._Warning warnings(
    short range);

  /**
   * <p>
   * Setter method for the COM property "Warnings"
   * </p>
   * @param warnings Mandatory be.valuya.winbooks._Warnings parameter.
   */

  @DISPID(1073938433) //= 0x40030001. The runtime will prefer the VTID if present
  @VTID(12)
  void warnings(
    be.valuya.winbooks._Warnings warnings);


  /**
   * <p>
   * Getter method for the COM property "FatalErrors"
   * </p>
   * @return  Returns a value of type be.valuya.winbooks._FatalErrors
   */

  @DISPID(1073938434) //= 0x40030002. The runtime will prefer the VTID if present
  @VTID(13)
  be.valuya.winbooks._FatalErrors fatalErrors();


  @VTID(13)
  @ReturnValue(defaultPropertyThrough={be.valuya.winbooks._FatalErrors.class})
  be.valuya.winbooks._FatalError fatalErrors(
    short range);

  /**
   * <p>
   * Setter method for the COM property "FatalErrors"
   * </p>
   * @param fatalErrors Mandatory be.valuya.winbooks._FatalErrors parameter.
   */

  @DISPID(1073938434) //= 0x40030002. The runtime will prefer the VTID if present
  @VTID(15)
  void fatalErrors(
    be.valuya.winbooks._FatalErrors fatalErrors);


  /**
   * <p>
   * Setter method for the COM property "LinkFormat"
   * </p>
   * @param rhs Mandatory Holder<be.valuya.winbooks.FormatLink> parameter.
   */

  @DISPID(1745027072) //= 0x68030000. The runtime will prefer the VTID if present
  @VTID(16)
  void linkFormat(
    Holder<be.valuya.winbooks.FormatLink> rhs);


  /**
   * <p>
   * Getter method for the COM property "LinkFormat"
   * </p>
   * @return  Returns a value of type be.valuya.winbooks.FormatLink
   */

  @DISPID(1745027072) //= 0x68030000. The runtime will prefer the VTID if present
  @VTID(17)
  be.valuya.winbooks.FormatLink linkFormat();


  /**
   * @param sNewPeriod Mandatory java.lang.String parameter.
   * @return  Returns a value of type boolean
   */

  @DISPID(1610809345) //= 0x60030001. The runtime will prefer the VTID if present
  @VTID(18)
  boolean setDefaultPeriod(
    java.lang.String sNewPeriod);


  /**
   * @param fileType Mandatory java.lang.String parameter.
   * @return  Returns a value of type boolean
   */

  @DISPID(1610809346) //= 0x60030002. The runtime will prefer the VTID if present
  @VTID(19)
  boolean fileFormat(
    java.lang.String fileType);


  /**
   * @param vNewValue Mandatory java.lang.String parameter.
   * @return  Returns a value of type boolean
   */

  @DISPID(1610809347) //= 0x60030003. The runtime will prefer the VTID if present
  @VTID(20)
  boolean directory(
    java.lang.String vNewValue);


  /**
   * @param wayForBackup Optional parameter. Default value is ""
   * @param eraseExisting Optional parameter. Default value is false
   * @return  Returns a value of type boolean
   */

  @DISPID(1610809348) //= 0x60030004. The runtime will prefer the VTID if present
  @VTID(21)
  boolean backup(
    @Optional Holder<java.lang.String> wayForBackup,
    @Optional @DefaultValue("-1") Holder<Boolean> eraseExisting);


  /**
   * @param sigFieldString Mandatory java.lang.String parameter.
   * @return  Returns a value of type boolean
   */

  @DISPID(1610809349) //= 0x60030005. The runtime will prefer the VTID if present
  @VTID(22)
  boolean sigFieldToExclude(
    java.lang.String sigFieldString);


  /**
   * @param sigFieldString Mandatory java.lang.String parameter.
   * @return  Returns a value of type boolean
   */

  @DISPID(1610809350) //= 0x60030006. The runtime will prefer the VTID if present
  @VTID(23)
  boolean sigParamFieldLink(
    java.lang.String sigFieldString);


  /**
   * @return  Returns a value of type short
   */

  @DISPID(1610809351) //= 0x60030007. The runtime will prefer the VTID if present
  @VTID(24)
  short execute();


  /**
   * @return  Returns a value of type boolean
   */

  @DISPID(1610809352) //= 0x60030008. The runtime will prefer the VTID if present
  @VTID(25)
  boolean test();


  // Properties:
}

package be.valuya.winbooks  ;

import com4j.*;

@IID("{0AE3AC25-0DAD-4530-ABC0-6C96CCDECA80}")
public interface _Analytiques extends Com4jObject {
  // Methods:
  /**
   * <p>
   * Getter method for the COM property "Fields"
   * </p>
   * @return  Returns a value of type be.valuya.winbooks._Fields
   */

  @DISPID(1073938434) //= 0x40030002. The runtime will prefer the VTID if present
  @VTID(7)
  be.valuya.winbooks._Fields fields();


  @VTID(7)
  @ReturnValue(defaultPropertyThrough={be.valuya.winbooks._Fields.class})
  be.valuya.winbooks._Field fields(
    java.lang.String fieldNameorFieldNum);

  /**
   * <p>
   * Setter method for the COM property "Fields"
   * </p>
   * @param fields Mandatory be.valuya.winbooks._Fields parameter.
   */

  @DISPID(1073938434) //= 0x40030002. The runtime will prefer the VTID if present
  @VTID(9)
  void fields(
    be.valuya.winbooks._Fields fields);


  /**
   * @param range Mandatory java.lang.String parameter.
   * @return  Returns a value of type be.valuya.winbooks._AnaLytique
   */

  @DISPID(1610809349) //= 0x60030005. The runtime will prefer the VTID if present
  @VTID(10)
  be.valuya.winbooks._AnaLytique item(
    java.lang.String range);


  /**
   * <p>
   * Getter method for the COM property "SortOrder"
   * </p>
   * @return  Returns a value of type short
   */

  @DISPID(1745027076) //= 0x68030004. The runtime will prefer the VTID if present
  @VTID(11)
  short sortOrder();


  /**
   * <p>
   * Setter method for the COM property "SortOrder"
   * </p>
   * @param rhs Mandatory short parameter.
   */

  @DISPID(1745027076) //= 0x68030004. The runtime will prefer the VTID if present
  @VTID(12)
  void sortOrder(
    short rhs);


  /**
   * <p>
   * Getter method for the COM property "OnlyBooked"
   * </p>
   * @return  Returns a value of type boolean
   */

  @DISPID(1745027073) //= 0x68030001. The runtime will prefer the VTID if present
  @VTID(13)
  boolean onlyBooked();


  /**
   * <p>
   * Setter method for the COM property "OnlyBooked"
   * </p>
   * @param rhs Mandatory boolean parameter.
   */

  @DISPID(1745027073) //= 0x68030001. The runtime will prefer the VTID if present
  @VTID(14)
  void onlyBooked(
    boolean rhs);


  /**
   * @param strindex Mandatory short parameter.
   * @return  Returns a value of type be.valuya.winbooks._AnaLytique
   */

  @DISPID(1610809350) //= 0x60030006. The runtime will prefer the VTID if present
  @VTID(15)
  be.valuya.winbooks._AnaLytique wichplan(
    short strindex);


  /**
   * @param section Mandatory java.lang.String parameter.
   * @param plan Mandatory java.lang.String parameter.
   * @return  Returns a value of type be.valuya.winbooks._AnaLytique
   */

  @DISPID(1610809351) //= 0x60030007. The runtime will prefer the VTID if present
  @VTID(16)
  be.valuya.winbooks._AnaLytique wichSectionPlan(
    java.lang.String section,
    java.lang.String plan);


  /**
   * @param tipe Mandatory java.lang.String parameter.
   * @return  Returns a value of type java.lang.Object
   */

  @DISPID(1610809352) //= 0x60030008. The runtime will prefer the VTID if present
  @VTID(17)
  @ReturnValue(type=NativeType.VARIANT)
  java.lang.Object fromWichNumber(
    java.lang.String tipe);


  /**
   * @return  Returns a value of type java.lang.Object
   */

  @DISPID(1610809353) //= 0x60030009. The runtime will prefer the VTID if present
  @VTID(18)
  @ReturnValue(type=NativeType.VARIANT)
  java.lang.Object fromWichAccount();


  /**
   * @return  Returns a value of type java.lang.Object
   */

  @DISPID(1610809354) //= 0x6003000a. The runtime will prefer the VTID if present
  @VTID(19)
  @ReturnValue(type=NativeType.VARIANT)
  java.lang.Object enumCategory();


  /**
   * <p>
   * Setter method for the COM property "PeriodRange"
   * </p>
   * @param rhs Mandatory java.lang.String parameter.
   */

  @DISPID(1745027072) //= 0x68030000. The runtime will prefer the VTID if present
  @VTID(20)
  void periodRange(
    java.lang.String rhs);


  /**
   * @return  Returns a value of type java.lang.Object
   */

  @DISPID(1610809355) //= 0x6003000b. The runtime will prefer the VTID if present
  @VTID(21)
  @ReturnValue(type=NativeType.VARIANT)
  java.lang.Object fromPeriods();


  /**
   * @param planAna Mandatory Holder<java.lang.String> parameter.
   * @param periode Mandatory Holder<java.lang.String> parameter.
   * @param startAccount Optional parameter. Default value is ""
   * @param endAccount Optional parameter. Default value is ""
   * @param startaccountG Optional parameter. Default value is ""
   * @param endAccountG Optional parameter. Default value is ""
   * @param typeOfbal Optional parameter. Default value is (byte) 0
   * @return  Returns a value of type java.math.BigDecimal
   */

  @DISPID(1610809356) //= 0x6003000c. The runtime will prefer the VTID if present
  @VTID(22)
  @ReturnValue(type=NativeType.Currency)
  java.math.BigDecimal balanceCum(
    Holder<java.lang.String> planAna,
    Holder<java.lang.String> periode,
    @Optional Holder<java.lang.String> startAccount,
    @Optional Holder<java.lang.String> endAccount,
    @Optional Holder<java.lang.String> startaccountG,
    @Optional Holder<java.lang.String> endAccountG,
    @Optional Holder<Byte> typeOfbal);


  /**
   * @param planAna Mandatory Holder<java.lang.String> parameter.
   * @param periode Mandatory Holder<java.lang.String> parameter.
   * @param startAccount Optional parameter. Default value is ""
   * @param endAccount Optional parameter. Default value is ""
   * @param startaccountG Optional parameter. Default value is ""
   * @param endAccountG Optional parameter. Default value is ""
   * @param planAna2 Optional parameter. Default value is ""
   * @param startAccount2 Optional parameter. Default value is ""
   * @param endAccount2 Optional parameter. Default value is ""
   * @param typeOfbal Optional parameter. Default value is (byte) 0
   * @return  Returns a value of type java.math.BigDecimal
   */

  @DISPID(1610809357) //= 0x6003000d. The runtime will prefer the VTID if present
  @VTID(23)
  @ReturnValue(type=NativeType.Currency)
  java.math.BigDecimal balancePerEx(
    Holder<java.lang.String> planAna,
    Holder<java.lang.String> periode,
    @Optional Holder<java.lang.String> startAccount,
    @Optional Holder<java.lang.String> endAccount,
    @Optional Holder<java.lang.String> startaccountG,
    @Optional Holder<java.lang.String> endAccountG,
    @Optional Holder<java.lang.String> planAna2,
    @Optional Holder<java.lang.String> startAccount2,
    @Optional Holder<java.lang.String> endAccount2,
    @Optional Holder<Byte> typeOfbal);


  /**
   * @param sectionAna Mandatory short parameter.
   * @param startAccount Optional parameter. Default value is ""
   * @param endAccount Optional parameter. Default value is ""
   * @param category Optional parameter. Default value is ""
   */

  @DISPID(1610809359) //= 0x6003000f. The runtime will prefer the VTID if present
  @VTID(24)
  void exportListeOfAnalyticalPlan(
    short sectionAna,
    @Optional Holder<java.lang.String> startAccount,
    @Optional Holder<java.lang.String> endAccount,
    @Optional Holder<java.lang.String> category);


  // Properties:
}

package be.valuya.winbooks  ;

import com4j.*;

@IID("{56456B72-E8C8-4582-90C6-257A18BA746F}")
public interface Transaction___v0 extends Com4jObject {
  // Methods:
  /**
   * <p>
   * Getter method for the COM property "ListCount"
   * </p>
   * @return  Returns a value of type int
   */

  @DISPID(1745027074) //= 0x68030002. The runtime will prefer the VTID if present
  @VTID(7)
  int listCount();


  /**
   * <p>
   * Getter method for the COM property "fieldvalue"
   * </p>
   * @param fieldNameorNum Mandatory java.lang.String parameter.
   * @return  Returns a value of type java.lang.Object
   */

  @DISPID(1745027073) //= 0x68030001. The runtime will prefer the VTID if present
  @VTID(8)
  @ReturnValue(type=NativeType.VARIANT)
  java.lang.Object fieldvalue(
    java.lang.String fieldNameorNum);


  /**
   */

  @DISPID(1610809350) //= 0x60030006. The runtime will prefer the VTID if present
  @VTID(9)
  void initializeCancel();


  /**
   */

  @DISPID(1610809351) //= 0x60030007. The runtime will prefer the VTID if present
  @VTID(10)
  void cancelOperation();


    /**
     */

    @DISPID(1610809353) //= 0x60030009. The runtime will prefer the VTID if present
    @VTID(12)
    void exportXL();


    /**
     * @param docname Mandatory java.lang.String parameter.
     */

    @DISPID(1610809371) //= 0x6003001b. The runtime will prefer the VTID if present
    @VTID(20)
    void exportXL2(
      java.lang.String docname);


    /**
     */

    @DISPID(1610809355) //= 0x6003000b. The runtime will prefer the VTID if present
    @VTID(13)
    void exportExcelWithReport();


    /**
     * @param docname Mandatory java.lang.String parameter.
     */

    @DISPID(1610809373) //= 0x6003001d. The runtime will prefer the VTID if present
    @VTID(21)
    void exportExcelWithReport2(
      java.lang.String docname);


    /**
     * @param total Optional parameter. Default value is ""
     * @return  Returns a value of type java.lang.Object
     */

    @DISPID(1610809358) //= 0x6003000e. The runtime will prefer the VTID if present
    @VTID(14)
    @ReturnValue(type=NativeType.VARIANT)
    java.lang.Object fastBalance(
      @Optional @DefaultValue("") Holder<java.lang.String> total);


    /**
     * @param total Optional parameter. Default value is ""
     * @return  Returns a value of type java.lang.Object
     */

    @DISPID(1610809359) //= 0x6003000f. The runtime will prefer the VTID if present
    @VTID(15)
    @ReturnValue(type=NativeType.VARIANT)
    java.lang.Object balance(
      @Optional @DefaultValue("") Holder<java.lang.String> total);


    /**
     * @return  Returns a value of type java.lang.Object
     */

    @DISPID(1610809360) //= 0x60030010. The runtime will prefer the VTID if present
    @VTID(16)
    @ReturnValue(type=NativeType.VARIANT)
    java.lang.Object turnover();


    /**
     * @return  Returns a value of type java.lang.Object
     */

    @DISPID(1610809361) //= 0x60030011. The runtime will prefer the VTID if present
    @VTID(17)
    @ReturnValue(type=NativeType.VARIANT)
    java.lang.Object turnoverAcc();


    /**
     * @param total Optional parameter. Default value is ""
     * @return  Returns a value of type java.lang.Object
     */

    @DISPID(1610809365) //= 0x60030015. The runtime will prefer the VTID if present
    @VTID(18)
    @ReturnValue(type=NativeType.VARIANT)
    java.lang.Object balanceCur(
      @Optional @DefaultValue("") Holder<java.lang.String> total);


    /**
     * @return  Returns a value of type java.math.BigDecimal
     */

    @DISPID(1610809366) //= 0x60030016. The runtime will prefer the VTID if present
    @VTID(19)
    @ReturnValue(type=NativeType.Currency)
    java.math.BigDecimal cliFouBalCur();


    // Properties:
  }

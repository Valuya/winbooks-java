package be.valuya.winbooks  ;

import com4j.*;

@IID("{D6D48C73-1EA6-4549-808D-1DAB01E26A94}")
public interface _Transaction extends Com4jObject {
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
   * Getter method for the COM property "FieldValue"
   * </p>
   * @param fieldNameorNum Mandatory java.lang.String parameter.
   * @return  Returns a value of type java.lang.Object
   */

  @DISPID(1745027073) //= 0x68030001. The runtime will prefer the VTID if present
  @VTID(8)
  @ReturnValue(type=NativeType.VARIANT)
  java.lang.Object fieldValue(
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
     */

    @DISPID(1610809355) //= 0x6003000b. The runtime will prefer the VTID if present
    @VTID(13)
    void exportExcelWithReport();


    /**
     * @return  Returns a value of type java.lang.Object
     */

    @DISPID(1610809358) //= 0x6003000e. The runtime will prefer the VTID if present
    @VTID(14)
    @ReturnValue(type=NativeType.VARIANT)
    java.lang.Object fastBalance();


    /**
     * @return  Returns a value of type java.lang.Object
     */

    @DISPID(1610809359) //= 0x6003000f. The runtime will prefer the VTID if present
    @VTID(15)
    @ReturnValue(type=NativeType.VARIANT)
    java.lang.Object balance();


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
     * @return  Returns a value of type java.lang.Object
     */

    @DISPID(1610809365) //= 0x60030015. The runtime will prefer the VTID if present
    @VTID(18)
    @ReturnValue(type=NativeType.VARIANT)
    java.lang.Object balanceCur();


    /**
     * @return  Returns a value of type java.math.BigDecimal
     */

    @DISPID(1610809366) //= 0x60030016. The runtime will prefer the VTID if present
    @VTID(19)
    @ReturnValue(type=NativeType.Currency)
    java.math.BigDecimal cliFouBalCur();


    // Properties:
  }

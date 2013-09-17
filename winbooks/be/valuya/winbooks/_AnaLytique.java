package be.valuya.winbooks  ;

import com4j.*;

@IID("{0E04D0C9-2716-4C10-82F1-0AD3BA98FF9C}")
public interface _AnaLytique extends Com4jObject {
  // Methods:
  /**
   * <p>
   * Setter method for the COM property "DeterminePlan"
   * </p>
   * @param rhs Mandatory Holder<Short> parameter.
   */

  @DISPID(1745027077) //= 0x68030005. The runtime will prefer the VTID if present
  @VTID(7)
  void determinePlan(
    Holder<Short> rhs);


  /**
   * <p>
   * Setter method for the COM property "PeriodStart"
   * </p>
   * @param rhs Mandatory java.lang.String parameter.
   */

  @DISPID(1745027076) //= 0x68030004. The runtime will prefer the VTID if present
  @VTID(8)
  void periodStart(
    java.lang.String rhs);


  /**
   * <p>
   * Setter method for the COM property "PeriodEnd"
   * </p>
   * @param rhs Mandatory java.lang.String parameter.
   */

  @DISPID(1745027075) //= 0x68030003. The runtime will prefer the VTID if present
  @VTID(9)
  void periodEnd(
    java.lang.String rhs);


  /**
   * <p>
   * Setter method for the COM property "AllBookyear"
   * </p>
   * @param rhs Mandatory boolean parameter.
   */

  @DISPID(1745027074) //= 0x68030002. The runtime will prefer the VTID if present
  @VTID(10)
  void allBookyear(
    boolean rhs);


  /**
   */

  @DISPID(1610809353) //= 0x60030009. The runtime will prefer the VTID if present
  @VTID(11)
  void initializeCancel();


  /**
   */

  @DISPID(1610809354) //= 0x6003000a. The runtime will prefer the VTID if present
  @VTID(12)
  void cancelOperation();


    /**
     */

    @DISPID(1610809356) //= 0x6003000c. The runtime will prefer the VTID if present
    @VTID(14)
    void exportXL();


    /**
     * @param deb_per Mandatory java.lang.String parameter.
     * @param end_per Mandatory java.lang.String parameter.
     */

    @DISPID(1610809357) //= 0x6003000d. The runtime will prefer the VTID if present
    @VTID(15)
    void exportBalAna(
      java.lang.String deb_per,
      java.lang.String end_per);


    /**
     * <p>
     * Getter method for the COM property "FieldValue"
     * </p>
     * @param fieldNameorNum Mandatory java.lang.String parameter.
     * @return  Returns a value of type java.lang.Object
     */

    @DISPID(1745027072) //= 0x68030000. The runtime will prefer the VTID if present
    @VTID(16)
    @ReturnValue(type=NativeType.VARIANT)
    java.lang.Object fieldValue(
      java.lang.String fieldNameorNum);


    // Properties:
  }

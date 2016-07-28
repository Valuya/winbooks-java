package be.valuya.winbooks  ;

import com4j.*;

@IID("{AC59215A-A103-49F7-84C9-5213D4977B8F}")
public interface Compte___v0 extends Com4jObject {
  // Methods:
  /**
   * <p>
   * Getter method for the COM property "ListCount"
   * </p>
   * @return  Returns a value of type int
   */

  @DISPID(1745027073) //= 0x68030001. The runtime will prefer the VTID if present
  @VTID(7)
  int listCount();


  /**
   * <p>
   * Getter method for the COM property "fieldvalue"
   * </p>
   * @param fieldNameorNum Mandatory java.lang.String parameter.
   * @return  Returns a value of type java.lang.Object
   */

  @DISPID(1745027072) //= 0x68030000. The runtime will prefer the VTID if present
  @VTID(8)
  @ReturnValue(type=NativeType.VARIANT)
  java.lang.Object fieldvalue(
    java.lang.String fieldNameorNum);


  /**
   */

  @DISPID(1610809349) //= 0x60030005. The runtime will prefer the VTID if present
  @VTID(9)
  void initializeCancel();


  /**
   */

  @DISPID(1610809350) //= 0x60030006. The runtime will prefer the VTID if present
  @VTID(10)
  void cancelOperation();


    /**
     */

    @DISPID(1610809352) //= 0x60030008. The runtime will prefer the VTID if present
    @VTID(12)
    void exportXL();


    /**
     * @param docname Mandatory java.lang.String parameter.
     */

    @DISPID(1610809357) //= 0x6003000d. The runtime will prefer the VTID if present
    @VTID(14)
    void exportXL2(
      java.lang.String docname);


    /**
     * @return  Returns a value of type double
     */

    @DISPID(1610809353) //= 0x60030009. The runtime will prefer the VTID if present
    @VTID(13)
    double balance();


    // Properties:
  }

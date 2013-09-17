package be.valuya.winbooks  ;

import com4j.*;

@IID("{1EC61783-2911-4177-B7C2-45AEE77C81A5}")
public interface _Compte extends Com4jObject {
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
   * Getter method for the COM property "FieldValue"
   * </p>
   * @param fieldNameorNum Mandatory java.lang.String parameter.
   * @return  Returns a value of type java.lang.Object
   */

  @DISPID(1745027072) //= 0x68030000. The runtime will prefer the VTID if present
  @VTID(8)
  @ReturnValue(type=NativeType.VARIANT)
  java.lang.Object fieldValue(
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
     * @return  Returns a value of type double
     */

    @DISPID(1610809353) //= 0x60030009. The runtime will prefer the VTID if present
    @VTID(13)
    double balance();


    // Properties:
  }

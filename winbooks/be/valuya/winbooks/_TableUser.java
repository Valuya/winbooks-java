package be.valuya.winbooks  ;

import com4j.*;

@IID("{ED402427-E6AE-4099-A625-6BADBA6EC776}")
public interface _TableUser extends Com4jObject {
  // Methods:
  /**
   * <p>
   * Getter method for the COM property "Number"
   * </p>
   * @return  Returns a value of type java.lang.String
   */

  @DISPID(1745027074) //= 0x68030002. The runtime will prefer the VTID if present
  @VTID(7)
  java.lang.String number();


  /**
   * <p>
   * Getter method for the COM property "SortOrder"
   * </p>
   * @return  Returns a value of type short
   */

  @DISPID(1745027073) //= 0x68030001. The runtime will prefer the VTID if present
  @VTID(8)
  short sortOrder();


  /**
   * <p>
   * Getter method for the COM property "FieldValue"
   * </p>
   * @param fieldNameorNum Mandatory java.lang.String parameter.
   * @return  Returns a value of type java.lang.Object
   */

  @DISPID(1745027072) //= 0x68030000. The runtime will prefer the VTID if present
  @VTID(9)
  @ReturnValue(type=NativeType.VARIANT)
  java.lang.Object fieldValue(
    java.lang.String fieldNameorNum);


  /**
   */

  @DISPID(1610809349) //= 0x60030005. The runtime will prefer the VTID if present
  @VTID(10)
  void exportXL();


  // Properties:
}

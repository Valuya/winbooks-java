package be.valuya.winbooks  ;

import com4j.*;

@IID("{80432D94-FFA2-410D-9EE6-8ADAEEC853E7}")
public interface _ErrorCode extends Com4jObject {
  // Methods:
  /**
   * <p>
   * Setter method for the COM property "SetResolution"
   * </p>
   * @param rhs Mandatory be.valuya.winbooks.TypeSolution parameter.
   */

  @DISPID(1745027075) //= 0x68030003. The runtime will prefer the VTID if present
  @VTID(7)
  void setResolution(
    be.valuya.winbooks.TypeSolution rhs);


  /**
   * <p>
   * Getter method for the COM property "GetResolution"
   * </p>
   * @return  Returns a value of type short
   */

  @DISPID(1745027074) //= 0x68030002. The runtime will prefer the VTID if present
  @VTID(8)
  short getResolution();


  /**
   * <p>
   * Getter method for the COM property "Description"
   * </p>
   * @return  Returns a value of type java.lang.String
   */

  @DISPID(1745027073) //= 0x68030001. The runtime will prefer the VTID if present
  @VTID(9)
  java.lang.String description();


  /**
   * <p>
   * Getter method for the COM property "AllowableActions"
   * </p>
   * @return  Returns a value of type java.lang.String
   */

  @DISPID(1745027072) //= 0x68030000. The runtime will prefer the VTID if present
  @VTID(10)
  java.lang.String allowableActions();


  // Properties:
}

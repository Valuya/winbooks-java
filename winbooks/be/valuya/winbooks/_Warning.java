package be.valuya.winbooks  ;

import com4j.*;

@IID("{DB7075A3-05BB-4E11-BA6B-4FEE30C60F85}")
public interface _Warning extends Com4jObject {
  // Methods:
  /**
   * <p>
   * Getter method for the COM property "Param"
   * </p>
   * @return  Returns a value of type java.lang.String
   */

  @DISPID(1745027075) //= 0x68030003. The runtime will prefer the VTID if present
  @VTID(7)
  java.lang.String param();


  /**
   * <p>
   * Getter method for the COM property "Code"
   * </p>
   * @return  Returns a value of type java.lang.String
   */

  @DISPID(1745027076) //= 0x68030004. The runtime will prefer the VTID if present
  @VTID(8)
  java.lang.String code();


  /**
   * <p>
   * Setter method for the COM property "SetResolution"
   * </p>
   * @param rhs Mandatory Holder<be.valuya.winbooks.TypeSolution> parameter.
   */

  @DISPID(1745027073) //= 0x68030001. The runtime will prefer the VTID if present
  @VTID(9)
  void setResolution(
    Holder<be.valuya.winbooks.TypeSolution> rhs);


  /**
   * <p>
   * Getter method for the COM property "GetResolution"
   * </p>
   * @return  Returns a value of type short
   */

  @DISPID(1745027072) //= 0x68030000. The runtime will prefer the VTID if present
  @VTID(10)
  short getResolution();


  // Properties:
}

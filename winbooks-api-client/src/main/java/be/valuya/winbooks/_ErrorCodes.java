package be.valuya.winbooks  ;

import com4j.*;

@IID("{C5DB9EF2-02CB-4C0D-9268-44CE8C3C1441}")
public interface _ErrorCodes extends Com4jObject {
  // Methods:
  /**
   * @param the_Error Mandatory java.lang.String parameter.
   * @return  Returns a value of type be.valuya.winbooks._ErrorCode
   */

  @DISPID(0) //= 0x0. The runtime will prefer the VTID if present
  @VTID(7)
  @DefaultMethod
  be.valuya.winbooks._ErrorCode item(
    java.lang.String the_Error);


  /**
   * @return  Returns a value of type short
   */

  @DISPID(1610809344) //= 0x60030000. The runtime will prefer the VTID if present
  @VTID(8)
  short count();


  // Properties:
}

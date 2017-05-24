package be.valuya.winbooks  ;

import com4j.*;

@IID("{509A01F8-9F80-488D-8790-9DCA2021FB8F}")
public interface _Warnings extends Com4jObject {
  // Methods:
  /**
   * @param range Mandatory short parameter.
   * @return  Returns a value of type be.valuya.winbooks._Warning
   */

  @DISPID(0) //= 0x0. The runtime will prefer the VTID if present
  @VTID(7)
  @DefaultMethod
  be.valuya.winbooks._Warning item(
    short range);


  /**
   * @return  Returns a value of type short
   */

  @DISPID(1610809344) //= 0x60030000. The runtime will prefer the VTID if present
  @VTID(8)
  short count();


  // Properties:
}

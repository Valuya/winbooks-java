package be.valuya.winbooks  ;

import com4j.*;

@IID("{BA6044C3-429E-419A-9994-01B31C973326}")
public interface _FatalErrors extends Com4jObject {
  // Methods:
  /**
   * @param range Mandatory short parameter.
   * @return  Returns a value of type be.valuya.winbooks._FatalError
   */

  @DISPID(0) //= 0x0. The runtime will prefer the VTID if present
  @VTID(7)
  @DefaultMethod
  be.valuya.winbooks._FatalError item(
    short range);


  /**
   * @return  Returns a value of type short
   */

  @DISPID(1610809344) //= 0x60030000. The runtime will prefer the VTID if present
  @VTID(8)
  short count();


  // Properties:
}

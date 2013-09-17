package be.valuya.winbooks  ;

import com4j.*;

@IID("{BAF30D64-3C47-4735-9FE2-039708CA2EBD}")
public interface _Periods extends Com4jObject {
  // Methods:
  /**
   * <p>
   * Getter method for the COM property "Count"
   * </p>
   * @return  Returns a value of type short
   */

  @DISPID(1745027073) //= 0x68030001. The runtime will prefer the VTID if present
  @VTID(7)
  short count();


  /**
   * @param index Mandatory short parameter.
   * @return  Returns a value of type be.valuya.winbooks._Period
   */

  @DISPID(0) //= 0x0. The runtime will prefer the VTID if present
  @VTID(8)
  @DefaultMethod
  be.valuya.winbooks._Period item(
    short index);


  // Properties:
}

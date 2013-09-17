package be.valuya.winbooks  ;

import com4j.*;

@IID("{58D0B480-46E4-4EC7-9237-9E600230AF89}")
public interface _BookYears extends Com4jObject {
  // Methods:
  /**
   * <p>
   * Getter method for the COM property "Count"
   * </p>
   * @return  Returns a value of type short
   */

  @DISPID(1745027072) //= 0x68030000. The runtime will prefer the VTID if present
  @VTID(7)
  short count();


  /**
   * @param index Mandatory short parameter.
   * @return  Returns a value of type be.valuya.winbooks._BookYear
   */

  @DISPID(0) //= 0x0. The runtime will prefer the VTID if present
  @VTID(8)
  @DefaultMethod
  be.valuya.winbooks._BookYear item(
    short index);


  // Properties:
}

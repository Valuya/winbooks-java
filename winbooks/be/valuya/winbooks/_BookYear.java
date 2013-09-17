package be.valuya.winbooks  ;

import com4j.*;

@IID("{676E0D1F-3AFF-45C8-AE36-983996A7A027}")
public interface _BookYear extends Com4jObject {
  // Methods:
  /**
   * <p>
   * Getter method for the COM property "Periods"
   * </p>
   * @return  Returns a value of type be.valuya.winbooks._Periods
   */

  @DISPID(1073938433) //= 0x40030001. The runtime will prefer the VTID if present
  @VTID(7)
  be.valuya.winbooks._Periods periods();


  @VTID(7)
  @ReturnValue(defaultPropertyThrough={be.valuya.winbooks._Periods.class})
  be.valuya.winbooks._Period periods(
    short index);

  /**
   * <p>
   * Setter method for the COM property "Periods"
   * </p>
   * @param periods Mandatory be.valuya.winbooks._Periods parameter.
   */

  @DISPID(1073938433) //= 0x40030001. The runtime will prefer the VTID if present
  @VTID(9)
  void periods(
    be.valuya.winbooks._Periods periods);


  /**
   * <p>
   * Getter method for the COM property "ShortName"
   * </p>
   * @return  Returns a value of type java.lang.String
   */

  @DISPID(1745027074) //= 0x68030002. The runtime will prefer the VTID if present
  @VTID(10)
  java.lang.String shortName();


  /**
   * <p>
   * Getter method for the COM property "LongName"
   * </p>
   * @return  Returns a value of type java.lang.String
   */

  @DISPID(1745027073) //= 0x68030001. The runtime will prefer the VTID if present
  @VTID(11)
  java.lang.String longName();


  /**
   * <p>
   * Getter method for the COM property "SigIndiceOfYear"
   * </p>
   * @param bookYearNum Optional parameter. Default value is (short) 0
   * @return  Returns a value of type java.lang.Object
   */

  @DISPID(1745027072) //= 0x68030000. The runtime will prefer the VTID if present
  @VTID(12)
  @ReturnValue(type=NativeType.VARIANT)
  java.lang.Object sigIndiceOfYear(
    @Optional Holder<Short> bookYearNum);


  // Properties:
}

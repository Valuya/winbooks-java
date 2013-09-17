package be.valuya.winbooks  ;

import com4j.*;

@IID("{099C39AF-17B0-4ECC-936C-4D47CE2BF8CA}")
public interface _Period extends Com4jObject {
  // Methods:
  /**
   * <p>
   * Getter method for the COM property "Name"
   * </p>
   * @return  Returns a value of type java.lang.String
   */

  @DISPID(1745027074) //= 0x68030002. The runtime will prefer the VTID if present
  @VTID(7)
  java.lang.String name();


  /**
   * @param namePeriod Mandatory java.lang.String parameter.
   * @return  Returns a value of type java.lang.String
   */

  @DISPID(1610809348) //= 0x60030004. The runtime will prefer the VTID if present
  @VTID(8)
  java.lang.String numOfPeriod(
    java.lang.String namePeriod);


  /**
   * <p>
   * Getter method for the COM property "ListOfPeriods"
   * </p>
   * @return  Returns a value of type java.lang.String
   */

  @DISPID(1745027072) //= 0x68030000. The runtime will prefer the VTID if present
  @VTID(9)
  java.lang.String listOfPeriods();


  // Properties:
}

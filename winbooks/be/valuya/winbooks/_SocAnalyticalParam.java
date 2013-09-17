package be.valuya.winbooks  ;

import com4j.*;

@IID("{A9BC5AE6-7191-49A3-86D1-05F84045B85B}")
public interface _SocAnalyticalParam extends Com4jObject {
  // Methods:
  /**
   * @param sectionNum Mandatory short parameter.
   * @return  Returns a value of type java.lang.String
   */

  @DISPID(1610809345) //= 0x60030001. The runtime will prefer the VTID if present
  @VTID(7)
  java.lang.String sectionName(
    short sectionNum);


  /**
   * @param sectionNum Mandatory short parameter.
   * @return  Returns a value of type java.lang.String
   */

  @DISPID(1610809346) //= 0x60030002. The runtime will prefer the VTID if present
  @VTID(8)
  java.lang.String sectionType(
    short sectionNum);


  /**
   * @param sectionNum Mandatory short parameter.
   * @return  Returns a value of type java.lang.String
   */

  @DISPID(1610809347) //= 0x60030003. The runtime will prefer the VTID if present
  @VTID(9)
  java.lang.String sectionLength(
    short sectionNum);


  /**
   * @return  Returns a value of type short
   */

  @DISPID(1610809348) //= 0x60030004. The runtime will prefer the VTID if present
  @VTID(10)
  short sectionsCount();


  /**
   * <p>
   * Getter method for the COM property "IsInstalled"
   * </p>
   * @return  Returns a value of type boolean
   */

  @DISPID(1745027072) //= 0x68030000. The runtime will prefer the VTID if present
  @VTID(11)
  boolean isInstalled();


  // Properties:
}

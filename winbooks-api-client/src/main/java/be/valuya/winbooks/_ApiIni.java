package be.valuya.winbooks  ;

import com4j.*;

@IID("{DC58C59A-D3DB-42A8-913B-36637908170B}")
public interface _ApiIni extends Com4jObject {
  // Methods:
  /**
   * @param section Mandatory Holder<java.lang.String> parameter.
   * @param key Mandatory Holder<java.lang.String> parameter.
   * @return  Returns a value of type java.lang.Object
   */

  @DISPID(1610809345) //= 0x60030001. The runtime will prefer the VTID if present
  @VTID(7)
  @ReturnValue(type=NativeType.VARIANT)
  java.lang.Object readINI(
    Holder<java.lang.String> section,
    Holder<java.lang.String> key);


  /**
   * @param section Mandatory Holder<java.lang.String> parameter.
   * @param key Mandatory Holder<java.lang.String> parameter.
   * @param text Mandatory Holder<java.lang.String> parameter.
   */

  @DISPID(1610809346) //= 0x60030002. The runtime will prefer the VTID if present
  @VTID(8)
  void writeINI(
    Holder<java.lang.String> section,
    Holder<java.lang.String> key,
    Holder<java.lang.String> text);


  /**
   * @param section Mandatory java.lang.String parameter.
   * @param key Mandatory java.lang.String parameter.
   * @param iniFileName Mandatory java.lang.String parameter.
   * @return  Returns a value of type boolean
   */

  @DISPID(1610809347) //= 0x60030003. The runtime will prefer the VTID if present
  @VTID(9)
  boolean isSpecifiedKeyExist(
    java.lang.String section,
    java.lang.String key,
    java.lang.String iniFileName);


  /**
   * <p>
   * Getter method for the COM property "INIfile"
   * </p>
   * @return  Returns a value of type java.lang.String
   */

  @DISPID(1745027072) //= 0x68030000. The runtime will prefer the VTID if present
  @VTID(10)
  java.lang.String inIfile();


  /**
   * <p>
   * Setter method for the COM property "INIfile"
   * </p>
   * @param rhs Mandatory java.lang.String parameter.
   */

  @DISPID(1745027072) //= 0x68030000. The runtime will prefer the VTID if present
  @VTID(11)
  void inIfile(
    java.lang.String rhs);


  /**
   * @return  Returns a value of type java.lang.String
   */

  @DISPID(1610809348) //= 0x60030004. The runtime will prefer the VTID if present
  @VTID(12)
  java.lang.String getUserDbfPath();


  // Properties:
}

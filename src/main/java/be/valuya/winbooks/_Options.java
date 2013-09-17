package be.valuya.winbooks  ;

import com4j.*;

@IID("{B4367929-275C-4A77-B306-FD08B1C760EB}")
public interface _Options extends Com4jObject {
  // Methods:
  /**
   * <p>
   * Setter method for the COM property "NewSheet"
   * </p>
   * @param rhs Mandatory boolean parameter.
   */

  @DISPID(0) //= 0x0. The runtime will prefer the VTID if present
  @VTID(7)
  @DefaultMethod
  void newSheet(
    boolean rhs);


  /**
   * <p>
   * Getter method for the COM property "NewSheet"
   * </p>
   * @return  Returns a value of type boolean
   */

  @DISPID(0) //= 0x0. The runtime will prefer the VTID if present
  @VTID(8)
  @DefaultMethod
  boolean newSheet();


  /**
   * <p>
   * Setter method for the COM property "ClearSheet"
   * </p>
   * @param rhs Mandatory boolean parameter.
   */

  @DISPID(1745027074) //= 0x68030002. The runtime will prefer the VTID if present
  @VTID(9)
  void clearSheet(
    boolean rhs);


  /**
   * <p>
   * Getter method for the COM property "ClearSheet"
   * </p>
   * @return  Returns a value of type boolean
   */

  @DISPID(1745027074) //= 0x68030002. The runtime will prefer the VTID if present
  @VTID(10)
  boolean clearSheet();


  /**
   * <p>
   * Setter method for the COM property "WithTitle"
   * </p>
   * @param rhs Mandatory boolean parameter.
   */

  @DISPID(1745027073) //= 0x68030001. The runtime will prefer the VTID if present
  @VTID(11)
  void withTitle(
    boolean rhs);


  /**
   * <p>
   * Getter method for the COM property "WithTitle"
   * </p>
   * @return  Returns a value of type boolean
   */

  @DISPID(1745027073) //= 0x68030001. The runtime will prefer the VTID if present
  @VTID(12)
  boolean withTitle();


  /**
   * <p>
   * Setter method for the COM property "Lang"
   * </p>
   * @param rhs Mandatory java.lang.String parameter.
   */

  @DISPID(1745027072) //= 0x68030000. The runtime will prefer the VTID if present
  @VTID(13)
  void lang(
    java.lang.String rhs);


  /**
   * <p>
   * Getter method for the COM property "Lang"
   * </p>
   * @return  Returns a value of type java.lang.String
   */

  @DISPID(1745027072) //= 0x68030000. The runtime will prefer the VTID if present
  @VTID(14)
  java.lang.String lang();


  /**
   */

  @DISPID(1610809347) //= 0x60030003. The runtime will prefer the VTID if present
  @VTID(15)
  void save();


  /**
   */

  @DISPID(1610809348) //= 0x60030004. The runtime will prefer the VTID if present
  @VTID(16)
  void loadDefault();


  // Properties:
}

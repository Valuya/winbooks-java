package be.valuya.winbooks  ;

import com4j.*;

@IID("{46326803-3FB9-475B-9398-78D0CED4797B}")
public interface _Comptes extends Com4jObject {
  // Methods:
  /**
   * <p>
   * Getter method for the COM property "Fields"
   * </p>
   * @return  Returns a value of type be.valuya.winbooks._Fields
   */

  @DISPID(1073938434) //= 0x40030002. The runtime will prefer the VTID if present
  @VTID(7)
  be.valuya.winbooks._Fields fields();


  @VTID(7)
  @ReturnValue(defaultPropertyThrough={be.valuya.winbooks._Fields.class})
  be.valuya.winbooks._Field fields(
    java.lang.String fieldNameorFieldNum);

  /**
   * <p>
   * Setter method for the COM property "Fields"
   * </p>
   * @param fields Mandatory be.valuya.winbooks._Fields parameter.
   */

  @DISPID(1073938434) //= 0x40030002. The runtime will prefer the VTID if present
  @VTID(9)
  void fields(
    be.valuya.winbooks._Fields fields);


  /**
   * @param range Mandatory java.lang.String parameter.
   * @return  Returns a value of type be.valuya.winbooks.Compte___v0
   */

  @DISPID(0) //= 0x0. The runtime will prefer the VTID if present
  @VTID(10)
  @DefaultMethod
  be.valuya.winbooks.Compte___v0 item(
    java.lang.String range);


  /**
   * <p>
   * Getter method for the COM property "OnlyBooked"
   * </p>
   * @return  Returns a value of type boolean
   */

  @DISPID(1745027074) //= 0x68030002. The runtime will prefer the VTID if present
  @VTID(11)
  boolean onlyBooked();


  /**
   * <p>
   * Setter method for the COM property "OnlyBooked"
   * </p>
   * @param rhs Mandatory boolean parameter.
   */

  @DISPID(1745027074) //= 0x68030002. The runtime will prefer the VTID if present
  @VTID(12)
  void onlyBooked(
    boolean rhs);


  /**
   * <p>
   * Getter method for the COM property "CalcSum"
   * </p>
   * @return  Returns a value of type boolean
   */

  @DISPID(1745027073) //= 0x68030001. The runtime will prefer the VTID if present
  @VTID(13)
  boolean calcSum();


  /**
   * <p>
   * Setter method for the COM property "CalcSum"
   * </p>
   * @param rhs Mandatory Holder<Boolean> parameter.
   */

  @DISPID(1745027073) //= 0x68030001. The runtime will prefer the VTID if present
  @VTID(14)
  void calcSum(
    Holder<Boolean> rhs);


  /**
   * <p>
   * Getter method for the COM property "SortOrder"
   * </p>
   * @return  Returns a value of type short
   */

  @DISPID(1745027072) //= 0x68030000. The runtime will prefer the VTID if present
  @VTID(15)
  short sortOrder();


  /**
   * <p>
   * Setter method for the COM property "SortOrder"
   * </p>
   * @param rhs Mandatory short parameter.
   */

  @DISPID(1745027072) //= 0x68030000. The runtime will prefer the VTID if present
  @VTID(16)
  void sortOrder(
    short rhs);


  // Properties:
}

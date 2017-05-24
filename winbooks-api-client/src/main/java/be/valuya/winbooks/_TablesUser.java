package be.valuya.winbooks  ;

import com4j.*;

@IID("{B537B733-0707-4ABD-8EFF-4356012F4150}")
public interface _TablesUser extends Com4jObject {
  // Methods:
  /**
   * <p>
   * Getter method for the COM property "Fields"
   * </p>
   * @return  Returns a value of type be.valuya.winbooks._Fields
   */

  @DISPID(1073938432) //= 0x40030000. The runtime will prefer the VTID if present
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

  @DISPID(1073938432) //= 0x40030000. The runtime will prefer the VTID if present
  @VTID(9)
  void fields(
    be.valuya.winbooks._Fields fields);


  /**
   * <p>
   * Setter method for the COM property "OnlyUsed"
   * </p>
   * @param rhs Mandatory boolean parameter.
   */

  @DISPID(1745027074) //= 0x68030002. The runtime will prefer the VTID if present
  @VTID(10)
  void onlyUsed(
    boolean rhs);


  /**
   * <p>
   * Getter method for the COM property "OnlyUsed"
   * </p>
   * @return  Returns a value of type boolean
   */

  @DISPID(1745027074) //= 0x68030002. The runtime will prefer the VTID if present
  @VTID(11)
  boolean onlyUsed();


  /**
   * @param range Mandatory java.lang.String parameter.
   * @return  Returns a value of type be.valuya.winbooks.TableUser___v0
   */

  @DISPID(0) //= 0x0. The runtime will prefer the VTID if present
  @VTID(12)
  @DefaultMethod
  be.valuya.winbooks.TableUser___v0 item(
    java.lang.String range);


  /**
   * <p>
   * Setter method for the COM property "SortOrder"
   * </p>
   * @param rhs Mandatory Holder<Short> parameter.
   */

  @DISPID(1745027072) //= 0x68030000. The runtime will prefer the VTID if present
  @VTID(13)
  void sortOrder(
    Holder<Short> rhs);


  // Properties:
}

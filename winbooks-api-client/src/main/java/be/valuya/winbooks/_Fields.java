package be.valuya.winbooks  ;

import com4j.*;

@IID("{68F66EF8-B2EE-4E33-81DB-61282C877D79}")
public interface _Fields extends Com4jObject {
  // Methods:
  /**
   * @param fieldNameorFieldNum Mandatory java.lang.String parameter.
   * @return  Returns a value of type be.valuya.winbooks._Field
   */

  @DISPID(0) //= 0x0. The runtime will prefer the VTID if present
  @VTID(7)
  @DefaultMethod
  be.valuya.winbooks._Field item(
    java.lang.String fieldNameorFieldNum);


  /**
   * <p>
   * Getter method for the COM property "Count"
   * </p>
   * @return  Returns a value of type short
   */

  @DISPID(1745027072) //= 0x68030000. The runtime will prefer the VTID if present
  @VTID(8)
  short count();


  /**
   * @param fieldName Mandatory java.lang.String parameter.
   * @return  Returns a value of type short
   */

  @DISPID(1610809347) //= 0x60030003. The runtime will prefer the VTID if present
  @VTID(9)
  short getIndiceInCollection(
    java.lang.String fieldName);


  // Properties:
}

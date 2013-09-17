package be.valuya.winbooks  ;

import com4j.*;

@IID("{7B31CD9A-123A-40D6-9353-2DF58ADA5820}")
public interface _Transactions extends Com4jObject {
  // Methods:
  /**
   * <p>
   * Getter method for the COM property "Fields"
   * </p>
   * @return  Returns a value of type be.valuya.winbooks._Fields
   */

  @DISPID(1073938435) //= 0x40030003. The runtime will prefer the VTID if present
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

  @DISPID(1073938435) //= 0x40030003. The runtime will prefer the VTID if present
  @VTID(9)
  void fields(
    be.valuya.winbooks._Fields fields);


  /**
   * @param range Mandatory java.lang.String parameter.
   * @return  Returns a value of type be.valuya.winbooks._Transaction
   */

  @DISPID(0) //= 0x0. The runtime will prefer the VTID if present
  @VTID(10)
  @DefaultMethod
  be.valuya.winbooks._Transaction item(
    java.lang.String range);


  /**
   * <p>
   * Getter method for the COM property "WithMatched"
   * </p>
   * @return  Returns a value of type boolean
   */

  @DISPID(1745027077) //= 0x68030005. The runtime will prefer the VTID if present
  @VTID(11)
  boolean withMatched();


  /**
   * <p>
   * Setter method for the COM property "WithMatched"
   * </p>
   * @param rhs Mandatory boolean parameter.
   */

  @DISPID(1745027077) //= 0x68030005. The runtime will prefer the VTID if present
  @VTID(12)
  void withMatched(
    boolean rhs);


  /**
   * <p>
   * Getter method for the COM property "CategoryFilter"
   * </p>
   * @return  Returns a value of type java.lang.String
   */

  @DISPID(1745027076) //= 0x68030004. The runtime will prefer the VTID if present
  @VTID(13)
  java.lang.String categoryFilter();


  /**
   * <p>
   * Setter method for the COM property "CategoryFilter"
   * </p>
   * @param rhs Mandatory java.lang.String parameter.
   */

  @DISPID(1745027076) //= 0x68030004. The runtime will prefer the VTID if present
  @VTID(14)
  void categoryFilter(
    java.lang.String rhs);


  /**
   * <p>
   * Getter method for the COM property "PeriodRange"
   * </p>
   * @return  Returns a value of type java.lang.String
   */

  @DISPID(1745027075) //= 0x68030003. The runtime will prefer the VTID if present
  @VTID(15)
  java.lang.String periodRange();


  /**
   * <p>
   * Setter method for the COM property "PeriodRange"
   * </p>
   * @param rhs Mandatory java.lang.String parameter.
   */

  @DISPID(1745027075) //= 0x68030003. The runtime will prefer the VTID if present
  @VTID(16)
  void periodRange(
    java.lang.String rhs);


  /**
   * @return  Returns a value of type java.lang.String
   */

  @DISPID(1610809353) //= 0x60030009. The runtime will prefer the VTID if present
  @VTID(17)
  java.lang.String getPathOfactFile();


  /**
   * @return  Returns a value of type java.lang.String
   */

  @DISPID(1610809354) //= 0x6003000a. The runtime will prefer the VTID if present
  @VTID(18)
  java.lang.String getThePathofExisintFile();


    /**
     * @return  Returns a value of type boolean
     */

    @DISPID(1610809356) //= 0x6003000c. The runtime will prefer the VTID if present
    @VTID(20)
    boolean isFileTotPerExist();


    /**
     * <p>
     * Setter method for the COM property "YearTransaction"
     * </p>
     * @param rhs Mandatory java.lang.String parameter.
     */

    @DISPID(1745027074) //= 0x68030002. The runtime will prefer the VTID if present
    @VTID(21)
    void yearTransaction(
      java.lang.String rhs);


    /**
     * <p>
     * Getter method for the COM property "CalcSum"
     * </p>
     * @return  Returns a value of type boolean
     */

    @DISPID(1745027073) //= 0x68030001. The runtime will prefer the VTID if present
    @VTID(22)
    boolean calcSum();


    /**
     * <p>
     * Setter method for the COM property "CalcSum"
     * </p>
     * @param rhs Mandatory Holder<Boolean> parameter.
     */

    @DISPID(1745027073) //= 0x68030001. The runtime will prefer the VTID if present
    @VTID(23)
    void calcSum(
      Holder<Boolean> rhs);


    /**
     * <p>
     * Getter method for the COM property "SortOrder"
     * </p>
     * @return  Returns a value of type short
     */

    @DISPID(1745027072) //= 0x68030000. The runtime will prefer the VTID if present
    @VTID(24)
    short sortOrder();


    /**
     * <p>
     * Setter method for the COM property "SortOrder"
     * </p>
     * @param rhs Mandatory Holder<Short> parameter.
     */

    @DISPID(1745027072) //= 0x68030000. The runtime will prefer the VTID if present
    @VTID(25)
    void sortOrder(
      Holder<Short> rhs);


    /**
     * @param account Mandatory java.lang.String parameter.
     * @param ty Mandatory Holder<Short> parameter.
     * @return  Returns a value of type be.valuya.winbooks._Transaction
     */

    @DISPID(1610809357) //= 0x6003000d. The runtime will prefer the VTID if present
    @VTID(26)
    be.valuya.winbooks._Transaction wichAccount(
      java.lang.String account,
      Holder<Short> ty);


    // Properties:
  }

package be.valuya.winbooks  ;

import com4j.*;

@IID("{33314AF5-FA6E-46EE-9E7A-7B7FA01CEA59}")
public interface SqlClass___v0 extends Com4jObject {
  // Methods:
  /**
   * <p>
   * Object error
   * </p>
   * <p>
   * Getter method for the COM property "Error"
   * </p>
   * @return  Returns a value of type be.valuya.winbooks._SqlError
   */

  @DISPID(1073938445) //= 0x4003000d. The runtime will prefer the VTID if present
  @VTID(7)
  be.valuya.winbooks._SqlError error();


  /**
   * <p>
   * Object error
   * </p>
   * <p>
   * Setter method for the COM property "Error"
   * </p>
   * @param error Mandatory be.valuya.winbooks._SqlError parameter.
   */

  @DISPID(1073938445) //= 0x4003000d. The runtime will prefer the VTID if present
  @VTID(9)
  void error(
    be.valuya.winbooks._SqlError error);


  /**
   * <p>
   * Export the results in a DBF file,Text File or Excel sheet
   * </p>
   * <p>
   * Setter method for the COM property "OutputFileFormat"
   * </p>
   * @param rhs Mandatory be.valuya.winbooks.FileType parameter.
   */

  @DISPID(1745027078) //= 0x68030006. The runtime will prefer the VTID if present
  @VTID(10)
  void outputFileFormat(
    be.valuya.winbooks.FileType rhs);


  /**
   * <p>
   * The name and the path of the ouput file
   * </p>
   * <p>
   * Setter method for the COM property "OutputFileName"
   * </p>
   * @param rhs Mandatory java.lang.String parameter.
   */

  @DISPID(1745027077) //= 0x68030005. The runtime will prefer the VTID if present
  @VTID(11)
  void outputFileName(
    java.lang.String rhs);


  /**
   * <p>
   * Append or overwrite the results in the output file
   * </p>
   * <p>
   * Setter method for the COM property "OutputFileMode"
   * </p>
   * @param rhs Mandatory be.valuya.winbooks.MODE parameter.
   */

  @DISPID(1745027076) //= 0x68030004. The runtime will prefer the VTID if present
  @VTID(12)
  void outputFileMode(
    be.valuya.winbooks.MODE rhs);


  /**
   * <p>
   * Return the time take by the execution of the statement
   * </p>
   * <p>
   * Getter method for the COM property "GetExecutionTime"
   * </p>
   * @return  Returns a value of type double
   */

  @DISPID(1745027075) //= 0x68030003. The runtime will prefer the VTID if present
  @VTID(13)
  double getExecutionTime();


  /**
   * <p>
   * Initialize all parameters of SQL object
   * </p>
   */

  @DISPID(1610809352) //= 0x60030008. The runtime will prefer the VTID if present
  @VTID(14)
  void initialize();


  /**
   * @param queryStatement Mandatory java.lang.String parameter.
   * @param withCallBack Optional parameter. Default value is false
   * @return  Returns a value of type java.lang.Object
   */

  @DISPID(1610809353) //= 0x60030009. The runtime will prefer the VTID if present
  @VTID(15)
  @ReturnValue(type=NativeType.VARIANT)
  java.lang.Object wbSqlFunction(
    java.lang.String queryStatement,
    @Optional @DefaultValue("0") Holder<Boolean> withCallBack);


  /**
   * @param queryStatement Mandatory java.lang.String parameter.
   * @param withCallBack Optional parameter. Default value is false
   * @param docname Optional parameter. Default value is ""
   * @return  Returns a value of type java.lang.Object
   */

  @DISPID(1610809364) //= 0x60030014. The runtime will prefer the VTID if present
  @VTID(20)
  @ReturnValue(type=NativeType.VARIANT)
  java.lang.Object wbSqlFunction2(
    java.lang.String queryStatement,
    @Optional @DefaultValue("0") Holder<Boolean> withCallBack,
    @Optional Holder<java.lang.String> docname);


  /**
   * <p>
   * Execute the statement and fill  Output file
   * </p>
   * @param queryStatement Mandatory java.lang.String parameter.
   * @param withCallBack Optional parameter. Default value is false
   * @param docname Optional parameter. Default value is ""
   * @return  Returns a value of type short
   */

  @DISPID(1610809365) //= 0x60030015. The runtime will prefer the VTID if present
  @VTID(21)
  short executeSqlQuery2(
    java.lang.String queryStatement,
    @Optional @DefaultValue("0") Holder<Boolean> withCallBack,
    @Optional @DefaultValue("") Holder<java.lang.String> docname);


  /**
   * @param queryStatement Mandatory java.lang.String parameter.
   * @param withCallBack Optional parameter. Default value is false
   * @return  Returns a value of type short
   */

  @DISPID(1610809354) //= 0x6003000a. The runtime will prefer the VTID if present
  @VTID(16)
  short executeSqlQuery(
    java.lang.String queryStatement,
    @Optional @DefaultValue("0") Holder<Boolean> withCallBack);


  /**
   * @return  Returns a value of type boolean
   */

  @DISPID(1610809355) //= 0x6003000b. The runtime will prefer the VTID if present
  @VTID(17)
  boolean wanTotCallBack();


  /**
   * <p>
   * Allow to verify is a statement is valid
   * </p>
   * @param sqlStatement Mandatory java.lang.Object parameter.
   * @return  Returns a value of type boolean
   */

  @DISPID(1610809360) //= 0x60030010. The runtime will prefer the VTID if present
  @VTID(18)
  boolean validateQueryStatement(
    @MarshalAs(NativeType.VARIANT) java.lang.Object sqlStatement);


  /**
   * <p>
   * Return the number of records found 
   * </p>
   * <p>
   * Getter method for the COM property "LastQueryRecCount"
   * </p>
   * @return  Returns a value of type int
   */

  @DISPID(1745027074) //= 0x68030002. The runtime will prefer the VTID if present
  @VTID(19)
  int lastQueryRecCount();


  // Properties:
}

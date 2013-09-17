package be.valuya.winbooks  ;

import com4j.*;

@IID("{4978356D-AE9A-4498-BFDF-8823457B880D}")
public interface _WbApiUtilities extends Com4jObject {
  // Methods:
    /**
     * @param id_query Mandatory java.lang.String parameter.
     * @param iD_statement Mandatory java.lang.String parameter.
     * @return  Returns a value of type boolean
     */

    @DISPID(1610809346) //= 0x60030002. The runtime will prefer the VTID if present
    @VTID(8)
    boolean saveNewQuery(
      java.lang.String id_query,
      java.lang.String iD_statement);


    /**
     * @param id_query Mandatory java.lang.String parameter.
     * @param iD_statement Mandatory java.lang.String parameter.
     * @return  Returns a value of type boolean
     */

    @DISPID(1610809349) //= 0x60030005. The runtime will prefer the VTID if present
    @VTID(9)
    boolean saveExistingQuery(
      java.lang.String id_query,
      java.lang.String iD_statement);


    /**
     * @param id_query Mandatory java.lang.String parameter.
     * @return  Returns a value of type boolean
     */

    @DISPID(1610809350) //= 0x60030006. The runtime will prefer the VTID if present
    @VTID(10)
    boolean deleteQuery(
      java.lang.String id_query);


    /**
     * @return  Returns a value of type double
     */

    @DISPID(1610809351) //= 0x60030007. The runtime will prefer the VTID if present
    @VTID(11)
    double initializeTime();


    /**
     * <p>
     * Getter method for the COM property "GetTime"
     * </p>
     * @return  Returns a value of type double
     */

    @DISPID(1745027072) //= 0x68030000. The runtime will prefer the VTID if present
    @VTID(12)
    double getTime();


        /**
         * @param id_query Mandatory java.lang.String parameter.
         * @return  Returns a value of type java.lang.String
         */

        @DISPID(1610809354) //= 0x6003000a. The runtime will prefer the VTID if present
        @VTID(15)
        java.lang.String seekSelectedQuery(
          java.lang.String id_query);


        /**
         * @param iD_statement Mandatory java.lang.String parameter.
         * @return  Returns a value of type java.lang.String
         */

        @DISPID(1610809355) //= 0x6003000b. The runtime will prefer the VTID if present
        @VTID(16)
        java.lang.String queryExist(
          java.lang.String iD_statement);


        // Properties:
      }

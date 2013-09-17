package be.valuya.winbooks  ;

import com4j.*;

@IID("{5AD66715-2DFC-4E55-A0CA-EDFA8E89586C}")
public interface _User extends Com4jObject {
  // Methods:
  /**
   * <p>
   * Getter method for the COM property "Name"
   * </p>
   * @return  Returns a value of type java.lang.String
   */

  @DISPID(1745027077) //= 0x68030005. The runtime will prefer the VTID if present
  @VTID(7)
  java.lang.String name();


  /**
   * <p>
   * Setter method for the COM property "Name"
   * </p>
   * @param rhs Mandatory java.lang.String parameter.
   */

  @DISPID(1745027077) //= 0x68030005. The runtime will prefer the VTID if present
  @VTID(8)
  void name(
    java.lang.String rhs);


  /**
   * @param socName Mandatory java.lang.String parameter.
   * @param socDirectory Mandatory java.lang.String parameter.
   * @param userName Mandatory java.lang.String parameter.
   * @return  Returns a value of type short
   */

  @DISPID(1610809350) //= 0x60030006. The runtime will prefer the VTID if present
  @VTID(9)
  short checkAccess(
    java.lang.String socName,
    java.lang.String socDirectory,
    java.lang.String userName);


  /**
   * @param socDirectory Mandatory java.lang.String parameter.
   * @param userName Mandatory java.lang.String parameter.
   * @param userLanguage Mandatory java.lang.String parameter.
   * @param userPassword Mandatory java.lang.String parameter.
   * @param userRights Mandatory java.lang.String parameter.
   */

  @DISPID(1610809351) //= 0x60030007. The runtime will prefer the VTID if present
  @VTID(10)
  void register(
    java.lang.String socDirectory,
    java.lang.String userName,
    java.lang.String userLanguage,
    java.lang.String userPassword,
    java.lang.String userRights);


  /**
   * <p>
   * Getter method for the COM property "Level"
   * </p>
   * @return  Returns a value of type java.lang.String
   */

  @DISPID(1745027076) //= 0x68030004. The runtime will prefer the VTID if present
  @VTID(11)
  java.lang.String level();


  /**
   * <p>
   * Getter method for the COM property "Language"
   * </p>
   * @return  Returns a value of type java.lang.String
   */

  @DISPID(1745027075) //= 0x68030003. The runtime will prefer the VTID if present
  @VTID(12)
  java.lang.String language();


  /**
   * <p>
   * Setter method for the COM property "Language"
   * </p>
   * @param rhs Mandatory java.lang.String parameter.
   */

  @DISPID(1745027075) //= 0x68030003. The runtime will prefer the VTID if present
  @VTID(13)
  void language(
    java.lang.String rhs);


  /**
   * <p>
   * Getter method for the COM property "Rights"
   * </p>
   * @return  Returns a value of type java.lang.String
   */

  @DISPID(1745027073) //= 0x68030001. The runtime will prefer the VTID if present
  @VTID(14)
  java.lang.String rights();


  /**
   * <p>
   * Setter method for the COM property "Rights"
   * </p>
   * @param rhs Mandatory java.lang.String parameter.
   */

  @DISPID(1745027073) //= 0x68030001. The runtime will prefer the VTID if present
  @VTID(15)
  void rights(
    java.lang.String rhs);


  /**
   */

  @DISPID(1610809352) //= 0x60030008. The runtime will prefer the VTID if present
  @VTID(16)
  void unRegister();


  /**
   * <p>
   * Getter method for the COM property "PrefsPath"
   * </p>
   * @return  Returns a value of type java.lang.String
   */

  @DISPID(1745027072) //= 0x68030000. The runtime will prefer the VTID if present
  @VTID(17)
  java.lang.String prefsPath();


  /**
   * @param userName Mandatory java.lang.String parameter.
   * @param socPathName Mandatory java.lang.String parameter.
   * @return  Returns a value of type short
   */

  @DISPID(1610809353) //= 0x60030009. The runtime will prefer the VTID if present
  @VTID(18)
  short isUserLoggedInDossier(
    java.lang.String userName,
    java.lang.String socPathName);


  /**
   * @return  Returns a value of type boolean
   */

  @DISPID(1610809354) //= 0x6003000a. The runtime will prefer the VTID if present
  @VTID(19)
  boolean isAdministrator();


  /**
   * @param moduleItemName Mandatory java.lang.String parameter.
   * @return  Returns a value of type boolean
   */

  @DISPID(1610809355) //= 0x6003000b. The runtime will prefer the VTID if present
  @VTID(20)
  boolean isAccess(
    java.lang.String moduleItemName);


  /**
   * @param userName Mandatory java.lang.String parameter.
   * @param forceRecalculation Optional parameter. Default value is false
   * @return  Returns a value of type java.lang.String
   */

  @DISPID(1610809356) //= 0x6003000c. The runtime will prefer the VTID if present
  @VTID(21)
  java.lang.String getAlternateUsernameAndDossier(
    java.lang.String userName,
    @Optional boolean forceRecalculation);


  /**
   * @param userName Mandatory java.lang.String parameter.
   * @param the_soc Mandatory java.lang.String parameter.
   * @return  Returns a value of type boolean
   */

  @DISPID(1610809357) //= 0x6003000d. The runtime will prefer the VTID if present
  @VTID(22)
  boolean checkPasswordOfUser(
    java.lang.String userName,
    java.lang.String the_soc);


  /**
   * @param userName Mandatory java.lang.String parameter.
   * @return  Returns a value of type java.lang.String
   */

  @DISPID(1610809359) //= 0x6003000f. The runtime will prefer the VTID if present
  @VTID(23)
  java.lang.String getLanguageOfCurrentUser(
    java.lang.String userName);


  // Properties:
}

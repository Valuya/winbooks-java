package be.valuya.winbooks  ;

import com4j.*;

@IID("{A111BDF0-F3BB-4AF7-8DD9-8246ED71139A}")
public interface _Dossiers extends Com4jObject {
  // Methods:
  /**
   * <p>
   * Getter method for the COM property "Count"
   * </p>
   * @return  Returns a value of type short
   */

  @DISPID(1745027073) //= 0x68030001. The runtime will prefer the VTID if present
  @VTID(7)
  short count();


  /**
   * <p>
   * Return an array who contains all companies 's name
   * </p>
   * @return  Returns a value of type java.lang.String[]
   */

  @DISPID(1610809346) //= 0x60030002. The runtime will prefer the VTID if present
  @VTID(8)
  java.lang.String[] getAllCompanies();


  /**
   * @return  Returns a value of type java.lang.Object
   */

  @DISPID(1610809347) //= 0x60030003. The runtime will prefer the VTID if present
  @VTID(9)
  @ReturnValue(type=NativeType.VARIANT)
  java.lang.Object getCompanyListSorted();


  /**
   * @param pathInUsersFile Mandatory java.lang.String parameter.
   * @param myShortName Mandatory java.lang.String parameter.
   * @return  Returns a value of type boolean
   */

  @DISPID(1610809349) //= 0x60030005. The runtime will prefer the VTID if present
  @VTID(10)
  boolean isLocalDossierExist(
    java.lang.String pathInUsersFile,
    java.lang.String myShortName);


  /**
   * @param index Mandatory short parameter.
   * @return  Returns a value of type be.valuya.winbooks._Dossier
   */

  @DISPID(0) //= 0x0. The runtime will prefer the VTID if present
  @VTID(11)
  @DefaultMethod
  be.valuya.winbooks._Dossier item(
    short index);


  /**
   * <p>
   * Getter method for the COM property "ShowArchivedBookyears"
   * </p>
   * @return  Returns a value of type boolean
   */

  @DISPID(1745027072) //= 0x68030000. The runtime will prefer the VTID if present
  @VTID(12)
  boolean showArchivedBookyears();


  /**
   * <p>
   * Setter method for the COM property "ShowArchivedBookyears"
   * </p>
   * @param rhs Mandatory boolean parameter.
   */

  @DISPID(1745027072) //= 0x68030000. The runtime will prefer the VTID if present
  @VTID(13)
  void showArchivedBookyears(
    boolean rhs);


  // Properties:
}

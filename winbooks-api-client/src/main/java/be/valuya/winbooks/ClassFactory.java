package be.valuya.winbooks  ;

import com4j.*;

/**
 * Defines methods to create COM objects
 */
public abstract class ClassFactory {
  private ClassFactory() {} // instanciation is not allowed


  public static be.valuya.winbooks._FieldDef createFieldDef() {
    return COM4J.createInstance( be.valuya.winbooks._FieldDef.class, "{A34930DA-384B-4069-B20D-6BC4C26271DB}" );
  }

  public static be.valuya.winbooks._Periods createPeriods() {
    return COM4J.createInstance( be.valuya.winbooks._Periods.class, "{0E6494B9-10AC-48C0-A288-45A6F75187C6}" );
  }

  public static be.valuya.winbooks._FatalErrors createFatalErrors() {
    return COM4J.createInstance( be.valuya.winbooks._FatalErrors.class, "{EBF5276A-68D3-48B0-96EF-3CDCB2D42C76}" );
  }

  public static be.valuya.winbooks._FatalError createFatalError() {
    return COM4J.createInstance( be.valuya.winbooks._FatalError.class, "{15C99B60-EFF0-417A-99A3-2C9ED67EA67C}" );
  }

  public static be.valuya.winbooks._Warnings createWarnings() {
    return COM4J.createInstance( be.valuya.winbooks._Warnings.class, "{520D4A52-D494-4E4D-A9A2-62E117E0252B}" );
  }

  public static be.valuya.winbooks._Queries createQueries() {
    return COM4J.createInstance( be.valuya.winbooks._Queries.class, "{E6F7158B-6BA4-4754-8441-804E463FB43D}" );
  }

  public static be.valuya.winbooks._TablesUser createTablesUser() {
    return COM4J.createInstance( be.valuya.winbooks._TablesUser.class, "{215B53FC-49C1-49E3-B4DF-F1E0A652CB3E}" );
  }

  public static be.valuya.winbooks._Tables createTables() {
    return COM4J.createInstance( be.valuya.winbooks._Tables.class, "{5DBE4BC9-7EE2-4279-9922-6DF7F36D8EC5}" );
  }

  public static be.valuya.winbooks._Warning createWarning() {
    return COM4J.createInstance( be.valuya.winbooks._Warning.class, "{2D7A39AA-CB4C-4A97-B4FC-CE9DBC8A4DD5}" );
  }

  public static be.valuya.winbooks._ErrorCodes createErrorCodes() {
    return COM4J.createInstance( be.valuya.winbooks._ErrorCodes.class, "{184F390B-1EA2-4CC0-B89D-4EA873B2F649}" );
  }

  public static be.valuya.winbooks._Fields createFields() {
    return COM4J.createInstance( be.valuya.winbooks._Fields.class, "{300F29DE-87B0-4E62-8B8C-CAF1CB264E0F}" );
  }

  public static be.valuya.winbooks._BookYears createBookYears() {
    return COM4J.createInstance( be.valuya.winbooks._BookYears.class, "{268F50EF-CC4B-4813-9E04-CAE3F19C6B75}" );
  }

  public static be.valuya.winbooks._BookYear createBookYear() {
    return COM4J.createInstance( be.valuya.winbooks._BookYear.class, "{DDF3767C-CB2F-429B-BC2E-A4622E20B748}" );
  }

  public static be.valuya.winbooks._Period createPeriod() {
    return COM4J.createInstance( be.valuya.winbooks._Period.class, "{7FEC6EF4-EC37-48E0-918B-78A4AA0E69C1}" );
  }

  public static be.valuya.winbooks._SocAnalyticalParam createSocAnalyticalParam() {
    return COM4J.createInstance( be.valuya.winbooks._SocAnalyticalParam.class, "{209411EA-5621-42FA-A99E-BCE0A06E0642}" );
  }

  public static be.valuya.winbooks._ErrorCode createErrorCode() {
    return COM4J.createInstance( be.valuya.winbooks._ErrorCode.class, "{5BC8AA2E-0E42-4784-8133-F6DFDAB20DA5}" );
  }

  public static be.valuya.winbooks._Import createImport() {
    return COM4J.createInstance( be.valuya.winbooks._Import.class, "{923C7ED7-E415-42CB-BDEA-6EB070C96BD9}" );
  }

  public static be.valuya.winbooks.Compte___v0 createCompte() {
    return COM4J.createInstance( be.valuya.winbooks.Compte___v0.class, "{48F3777C-6A93-49E9-8356-F0A6D20FB28A}" );
  }

  public static be.valuya.winbooks.TableUser___v0 createTableUser() {
    return COM4J.createInstance( be.valuya.winbooks.TableUser___v0.class, "{C2C9C138-51C3-4BB4-9F43-4C5006B23644}" );
  }

  public static be.valuya.winbooks._Comptes createComptes() {
    return COM4J.createInstance( be.valuya.winbooks._Comptes.class, "{DA36E857-BB5A-4534-A29B-147DA9891BA3}" );
  }

  public static be.valuya.winbooks._Dossier createDossier() {
    return COM4J.createInstance( be.valuya.winbooks._Dossier.class, "{7A5CABDC-4D00-4C7A-A9F3-0845AC81E465}" );
  }

  public static be.valuya.winbooks._Options createOptions() {
    return COM4J.createInstance( be.valuya.winbooks._Options.class, "{FD92BEAC-82DA-4AA8-BA93-EAA8C312512E}" );
  }

  public static be.valuya.winbooks._Field createField() {
    return COM4J.createInstance( be.valuya.winbooks._Field.class, "{FE3AC3CC-0C53-403F-8C8E-1A1FDDEE9F5B}" );
  }

  public static be.valuya.winbooks.AnaLytique___v0 createAnaLytique() {
    return COM4J.createInstance( be.valuya.winbooks.AnaLytique___v0.class, "{6A32FF54-31A4-4A29-BBAC-484B0E3F5D74}" );
  }

  public static be.valuya.winbooks._WbApiUtilities createWbApiUtilities() {
    return COM4J.createInstance( be.valuya.winbooks._WbApiUtilities.class, "{71979598-9C60-4F06-BC21-5827860DB27B}" );
  }

  public static be.valuya.winbooks._User createUser() {
    return COM4J.createInstance( be.valuya.winbooks._User.class, "{EA8B7735-B14B-40BD-A5ED-16239F6F9A80}" );
  }

  public static be.valuya.winbooks._Dossiers createDossiers() {
    return COM4J.createInstance( be.valuya.winbooks._Dossiers.class, "{02FD2AEF-1046-4C4A-97B3-6E4DFD79CD94}" );
  }

  public static be.valuya.winbooks._ApiIni createApiIni() {
    return COM4J.createInstance( be.valuya.winbooks._ApiIni.class, "{32E39920-91ED-4ECE-A14C-0F9E11BE4126}" );
  }

  public static be.valuya.winbooks.WinbooksObject___v0 createWinbooksObject() {
    return COM4J.createInstance( be.valuya.winbooks.WinbooksObject___v0.class, "{B67F206D-2887-4898-89D8-8AD5691B6DF0}" );
  }

  public static be.valuya.winbooks.SqlClass___v0 createSqlClass() {
    return COM4J.createInstance( be.valuya.winbooks.SqlClass___v0.class, "{CEA217D2-2E26-4E20-A7E4-094A55E3C421}" );
  }

  public static be.valuya.winbooks._Param createParam() {
    return COM4J.createInstance( be.valuya.winbooks._Param.class, "{BEEA7825-9F20-4A2F-B0D8-64ADC5E450CD}" );
  }

  public static be.valuya.winbooks._Transactions createTransactions() {
    return COM4J.createInstance( be.valuya.winbooks._Transactions.class, "{3EFFC1A5-6784-471E-B92B-72A2AD941286}" );
  }

  public static be.valuya.winbooks.Analytiques___v0 createAnalytiques() {
    return COM4J.createInstance( be.valuya.winbooks.Analytiques___v0.class, "{EDC584A4-AB62-4BAB-94CF-13BC7D350961}" );
  }

  public static be.valuya.winbooks.Transaction___v0 createTransaction() {
    return COM4J.createInstance( be.valuya.winbooks.Transaction___v0.class, "{DCCB2DE6-FA2A-430D-A79D-F79B258FF15A}" );
  }
}

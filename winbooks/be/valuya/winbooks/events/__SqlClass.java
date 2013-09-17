package be.valuya.winbooks.events;

import com4j.*;

@IID("{9101B8FB-9828-4118-B1ED-BE366BFCE890}")
public abstract class __SqlClass {
  // Methods:
  /**
   * <p>
   * Fire when the statement is execute
   * </p>
   * @param executionTime Mandatory double parameter.
   * @param cancel Mandatory Holder<Boolean> parameter.
   */

  @DISPID(1)
  public void onExecute(
    double executionTime,
    Holder<Boolean> cancel) {
        throw new UnsupportedOperationException();
  }


  /**
   * <p>
   * fire when the output file is fill
   * </p>
   * @param nbrRecOutput Mandatory int parameter.
   * @param nbrRecCount Mandatory int parameter.
   * @param cancel Mandatory Holder<Boolean> parameter.
   */

  @DISPID(2)
  public void onFillOutputFile(
    int nbrRecOutput,
    int nbrRecCount,
    Holder<Boolean> cancel) {
        throw new UnsupportedOperationException();
  }


  // Properties:
}

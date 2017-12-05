package be.valuya.winbooks.events;

import com4j.*;

@IID("{7F803670-0035-43C7-9B71-81FE1C65EE4B}")
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

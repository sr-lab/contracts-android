package contractstudy.usage.collectContracts.common.MethodVisitorToCollectInvocations;

import contractstudy.constants.constraint.ContractElement;
import contractstudy.model.ExtractionListener;
import contractstudy.usage.collectContracts.common.AbstractMethodVisitor.AbstractMethodVisitorKotlin;
import contractstudy.usage.collectContracts.common.StaticImportCollector.constants.StaticImportState;

import java.util.Collection;

public abstract class MethodVisitorToCollectInvocationsKotlin extends AbstractMethodVisitorKotlin {

  protected StaticImportState importState;
  protected Collection<String> staticallyImportedMethodNames;

  public MethodVisitorToCollectInvocationsKotlin(
    ExtractionListener<ContractElement> consumer,
    String programName,
    String version,
    String cuName,
    StaticImportState importState,
    Collection<String> staticallyImportedMethodNames
  ) {
    super(consumer, programName, version, cuName);
    this.importState = importState;
    this.staticallyImportedMethodNames = staticallyImportedMethodNames;
  }

  protected boolean checkImports(String methodName, String scope, String localClassName, String fullClassName) {
    if (this.importState == StaticImportState.CLASS && localClassName.equals(scope)) {
      return true;
    }
    if (this.importState == StaticImportState.ALL_STATIC) {
      return true;
    }
    return this.importState == StaticImportState.NONE && fullClassName.equals(scope);
  }
}

package contractstudy.usage.collectContracts.common.MethodVisitorToCollectInvocations;

import contractstudy.constants.constraint.ContractElement;
import contractstudy.model.ExtractionListener;
import contractstudy.usage.collectContracts.common.AbstractMethodVisitor.AbstractMethodVisitor;
import contractstudy.usage.collectContracts.common.StaticImportCollector.constants.StaticImportState;

import java.util.Collection;

public abstract class MethodVisitorToCollectInvocations extends AbstractMethodVisitor {

  protected StaticImportState importState;
  protected Collection<String> staticallyImportedMethodNames;

  public MethodVisitorToCollectInvocations(
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
    if (this.importState == StaticImportState.SOME_STATIC && this.staticallyImportedMethodNames.contains(methodName)) {
      return true;
    }
    return this.importState == StaticImportState.NONE && fullClassName.equals(scope);
  }
}

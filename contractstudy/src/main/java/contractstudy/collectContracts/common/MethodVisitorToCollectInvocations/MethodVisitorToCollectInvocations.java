package contractstudy.collectContracts.common.MethodVisitorToCollectInvocations;

import contractstudy.ExtractionListener;
import contractstudy.collectContracts.common.AbstractMethodVisitor.AbstractMethodVisitor;
import contractstudy.collectContracts.common.StaticImportCollector.constants.StaticImportState;
import contractstudy.constants.constraint.ContractElement;

import java.util.Collection;

/**
 * Abstract superclass for visitors for method nodes in the AST, used to collect invocations of API
 * methods
 *
 * @author jens dietrich
 */
@SuppressWarnings("rawtypes")
public abstract class MethodVisitorToCollectInvocations extends AbstractMethodVisitor {

  protected StaticImportState importState = null;
  protected Collection<String> staticallyImportedMethodNames = null;

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

  // TODO: This is duplicated from MethodVisitorToCollectInvocationsKotlin. Maybe only 1 method is needed.
  protected boolean checkImports(String methodName, String scope, String localClassName,
    String fullClassName) {
    if (this.importState == StaticImportState.CLASS && localClassName.equals(scope)) {
      return true;
    }
    if (this.importState == StaticImportState.ALL_STATIC) {
      return true;
    }
    if (this.importState == StaticImportState.SOME_STATIC
      && this.staticallyImportedMethodNames.contains(methodName)) {
      return true;
    }
    return this.importState == StaticImportState.NONE && fullClassName.equals(scope);
  }
}

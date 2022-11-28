package contractstudy.collectContracts.extractors.common.AbstractMethodVisitor;

import contractstudy.ExtractionListener;
import contractstudy.config.Preferences;
import contractstudy.constants.constraint.ContractElement;
import org.jetbrains.kotlin.psi.KtTreeVisitorVoid;

public abstract class AbstractMethodVisitorKotlin extends KtTreeVisitorVoid {

  private final boolean includePrivateMethods = Preferences.includePrivateMethods();
  private final boolean isAbstractMethod = false;
  private final boolean isInterface = false;
  private final boolean isDefaultMethod = false;
  protected ExtractionListener<ContractElement> consumer = null;
  protected String programName = null;
  protected String version = null;
  protected String cuName = null;
  protected String methodDeclaration = null;
  private String packageName;

  public AbstractMethodVisitorKotlin(
    ExtractionListener<ContractElement> consumer,
    String programName,
    String version,
    String cuName
  ) {
    super();
    this.consumer = consumer;
    this.programName = programName;
    this.version = version;
    this.cuName = cuName;
  }

  protected ContractElement initConstraint() {
    ContractElement p = new ContractElement();
    p.setMethodAbstract(computeAbstractMethod());
    p.setMethodDeclaration(methodDeclaration);

    return p;
  }

  protected boolean computeAbstractMethod() {
    return (isInterface && !isDefaultMethod) || isAbstractMethod;
  }

}

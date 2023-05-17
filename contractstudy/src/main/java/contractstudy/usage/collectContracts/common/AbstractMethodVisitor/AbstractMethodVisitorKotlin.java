package contractstudy.usage.collectContracts.common.AbstractMethodVisitor;

import contractstudy.config.Preferences;
import contractstudy.constants.constraint.ContractElement;
import contractstudy.model.ExtractionListener;
import contractstudy.utils.KotlinParserUtils;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.kotlin.com.intellij.psi.PsiElement;
import org.jetbrains.kotlin.psi.KtCallExpression;
import org.jetbrains.kotlin.psi.KtClassOrObject;
import org.jetbrains.kotlin.psi.KtElement;
import org.jetbrains.kotlin.psi.KtImportDirective;
import org.jetbrains.kotlin.psi.KtModifierList;
import org.jetbrains.kotlin.psi.KtNamedFunction;
import org.jetbrains.kotlin.psi.KtPackageDirective;
import org.jetbrains.kotlin.psi.KtParameter;
import org.jetbrains.kotlin.psi.KtTreeVisitorVoid;

import java.util.List;
import java.util.Objects;

@Getter
public abstract class AbstractMethodVisitorKotlin extends KtTreeVisitorVoid {

  private final boolean includePrivateMethods = Preferences.includePrivateMethods();
  private final boolean isAbstractMethod = false;
  protected ExtractionListener<ContractElement> consumer;
  protected String programName;
  protected String version;
  protected String cuName;
  protected String methodDeclaration = null;
  private boolean isInterface = false;
  private boolean isDefaultMethod = false;
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

  @Override
  public void visitPackageDirective(@NotNull KtPackageDirective directive) {
    packageName = directive.getName();
    super.visitPackageDirective(directive);
  }

  @Override
  public void visitClassOrObject(@NotNull KtClassOrObject classOrObject) {
    checkIfInterface(classOrObject);
    super.visitClassOrObject(classOrObject);
  }

  @Override
  public void visitNamedFunction(@NotNull KtNamedFunction function) {
    KtModifierList ktModifierList = function.getModifierList();
    String functionName = function.getName();
    List<KtParameter> functionParameters = function.getValueParameters();
    isDefaultMethod = false; //TODO: Do default methods exist in Kotlin?
    if (includePrivateMethods || isInterface || KotlinParserUtils.isMethodVisibilityAccepted(ktModifierList)) {
      this.methodDeclaration = KotlinParserUtils.getDeclaration(functionName, functionParameters);
    }
    super.visitNamedFunction(function);
  }

  private void checkIfInterface(KtClassOrObject classOrObject) {
    try {
      PsiElement declaration = classOrObject.getDeclarationKeyword();
      if (declaration != null) {
        String declarationKeyword = declaration.getText();
        this.isInterface = "interface".equals(declarationKeyword);
      }
    } catch (NullPointerException exception) {
      exception.printStackTrace();
    }
  }

  protected String findOwner(KtElement element) {
    KtElement parent = element;
    final String separator = ".";
    StringBuilder owner = new StringBuilder();

    while (parent != null) {
      if (parent instanceof KtClassOrObject) {
        String name = parent.getName();
        owner.insert(0, name + separator);
      }
      parent = (KtElement) parent.getParent();
    }

    if (owner.toString().endsWith(".")) {
      owner = new StringBuilder(owner.substring(0, owner.lastIndexOf(separator)));
    }

    return packageName + "." + owner;
  }

  protected String getImportedNameIdentified(KtImportDirective importDirective) throws NullPointerException {
    return importDirective.getImportedName().getIdentifier();
  }

  protected String getScope(KtCallExpression ktCallExpression) {
    try {
      String context = Objects.requireNonNull(ktCallExpression.getContext()).getText();
      if (context.startsWith("{") && context.endsWith("}")) {
        return "";
      }
      if (context.contains("(")) {
        context = context.substring(0, context.indexOf("("));
      }
      return ktCallExpression.getContext().getText().substring(0, context.lastIndexOf("."));
    } catch (NullPointerException exception) {
      return "";
    }
  }

}

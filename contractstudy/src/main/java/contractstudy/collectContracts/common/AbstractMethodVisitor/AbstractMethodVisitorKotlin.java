package contractstudy.collectContracts.common.AbstractMethodVisitor;

import contractstudy.ExtractionListener;
import contractstudy.config.Preferences;
import contractstudy.constants.VisibilityModifier;
import contractstudy.constants.constraint.ContractElement;
import contractstudy.utils.kotlinParser.KotlinParserUtils;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.kotlin.com.intellij.psi.PsiElement;
import org.jetbrains.kotlin.psi.KtClassOrObject;
import org.jetbrains.kotlin.psi.KtElement;
import org.jetbrains.kotlin.psi.KtModifierList;
import org.jetbrains.kotlin.psi.KtNamedFunction;
import org.jetbrains.kotlin.psi.KtPackageDirective;
import org.jetbrains.kotlin.psi.KtTreeVisitorVoid;

@Getter
public abstract class AbstractMethodVisitorKotlin extends KtTreeVisitorVoid {

  private final boolean includePrivateMethods = Preferences.includePrivateMethods();
  protected ExtractionListener<ContractElement> consumer = null;
  protected String programName = null;
  protected String version = null;
  protected String cuName = null;
  protected String methodDeclaration = null;
  private final boolean isAbstractMethod = false;
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
    VisibilityModifier visibility = KotlinParserUtils.getVisibilityModifier(ktModifierList);
    isDefaultMethod = false; //TODO: Do default methods exist in Kotlin?
    //TODO: Include internal methods?
    if (includePrivateMethods || isInterface || visibility == VisibilityModifier.PUBLIC
      || visibility == VisibilityModifier.PROTECTED) {
      this.methodDeclaration = function.getName() + "()";
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


}

package contractstudy.inheritance.SuperCallSiteExtractor.visitor;

import contractstudy.inheritance.model.SuperCallSite;
import contractstudy.model.ProgramVersion;
import contractstudy.utils.KotlinParserUtils;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.kotlin.psi.KtClassOrObject;
import org.jetbrains.kotlin.psi.KtModifierList;
import org.jetbrains.kotlin.psi.KtNamedFunction;
import org.jetbrains.kotlin.psi.KtSecondaryConstructor;
import org.jetbrains.kotlin.psi.KtSuperExpression;
import org.jetbrains.kotlin.psi.KtSuperTypeListEntry;
import org.jetbrains.kotlin.psi.KtTreeVisitorVoid;

import java.util.List;
import java.util.stream.Collectors;

import static contractstudy.utils.KotlinParserUtils.isMethodVisibilityAccepted;

/**
 * Stores in a list instances of "super.foo()" or "super()".
 */
@Setter
public class MethodVisitorToCollectSuperCallKotlin extends KtTreeVisitorVoid {

  private String cuName;
  private String methodDeclaration;
  private boolean isMethod = true;
  private ProgramVersion programVersion;
  private List<SuperCallSite> superCallSites;

  public MethodVisitorToCollectSuperCallKotlin(
    String cuName,
    ProgramVersion programVersion,
    List<SuperCallSite> superCallSites) {
    this.cuName = cuName;
    this.programVersion = programVersion;
    this.superCallSites = superCallSites;
  }

  @Override
  public void visitClassOrObject(@NotNull KtClassOrObject classOrObject) {
    KtModifierList modifiers = classOrObject.getModifierList();
    if (isMethodVisibilityAccepted(modifiers)) {
      this.methodDeclaration = KotlinParserUtils.getDeclaration(classOrObject.getName(),
        classOrObject.getPrimaryConstructorParameters());
      this.isMethod = false;
      checkForSuperConstructorCall(classOrObject);
    }
    super.visitClassOrObject(classOrObject);
  }

  private void checkForSuperConstructorCall(KtClassOrObject classOrObject) {
    List<KtSuperTypeListEntry> constructorSuperTypes = classOrObject.getSuperTypeListEntries();
    List<KtSuperTypeListEntry> parentConstructorCalls = constructorSuperTypes
      .stream().filter(t -> t.getText().endsWith(")")).collect(Collectors.toList());
    if (constructorSuperTypes.size() > 0 && parentConstructorCalls.size() > 0) {
      SuperCallSite callSite = new SuperCallSite(programVersion, cuName, methodDeclaration, isMethod);
      superCallSites.add(callSite);
    }
  }

  @Override
  public void visitSecondaryConstructor(@NotNull KtSecondaryConstructor constructor) {
    KtModifierList modifiers = constructor.getModifierList();
    if (isMethodVisibilityAccepted(modifiers)) {
      this.methodDeclaration = KotlinParserUtils.getDeclaration(constructor.getName(), constructor.getValueParameters());
      this.isMethod = false;
      checkForSuperConstructorCall(constructor);
    }
  }

  private void checkForSuperConstructorCall(KtSecondaryConstructor constructor) {
    if (!constructor.getDelegationCall().isCallToThis()) {
      String delegationCallName = constructor.getDelegationCall().getText();
      if (delegationCallName != null && delegationCallName.replace(" ", "").startsWith("super")) {
        SuperCallSite callSite = new SuperCallSite(programVersion, cuName, methodDeclaration, isMethod);
        superCallSites.add(callSite);
      }
    }
  }

  @Override
  public void visitNamedFunction(@NotNull KtNamedFunction function) {
    KtModifierList ktModifierList = function.getModifierList();
    if (isMethodVisibilityAccepted(ktModifierList)) {
      this.methodDeclaration = KotlinParserUtils.getDeclaration(function.getName(), function.getValueParameters());
      this.isMethod = true;
    }
    super.visitNamedFunction(function);
  }

  @Override
  public void visitSuperExpression(@NotNull KtSuperExpression expression) {
    if (isMethod) {
      SuperCallSite callSite = new SuperCallSite(programVersion, cuName, methodDeclaration, true);
      superCallSites.add(callSite);
    }
    super.visitSuperExpression(expression);
  }

}

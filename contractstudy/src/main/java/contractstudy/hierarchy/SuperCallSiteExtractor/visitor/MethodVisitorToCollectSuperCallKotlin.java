package contractstudy.hierarchy.SuperCallSiteExtractor.visitor;

import contractstudy.ProgramVersion;
import contractstudy.config.Preferences;
import contractstudy.constants.VisibilityModifier;
import contractstudy.hierarchy.model.SuperCallSite;
import contractstudy.utils.kotlinParser.KotlinParserUtils;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.kotlin.com.intellij.psi.PsiElement;
import org.jetbrains.kotlin.psi.KtConstructor;
import org.jetbrains.kotlin.psi.KtModifierList;
import org.jetbrains.kotlin.psi.KtNamedFunction;
import org.jetbrains.kotlin.psi.KtSuperExpression;
import org.jetbrains.kotlin.psi.KtTreeVisitorVoid;

import java.util.List;

/**
 * Stores in a list instances of "super.foo()" or "super()".
 */
@Setter
public class MethodVisitorToCollectSuperCallKotlin extends KtTreeVisitorVoid {

  private final boolean includePrivateMethods = Preferences.includePrivateMethods();
  private String cuName;
  private String methodDeclaration;
  private boolean isMethod = true; // false, if constructor
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
  public void visitElement(@NotNull PsiElement element) {
    if (element instanceof KtNamedFunction) {
      collectMethodDeclaration((KtNamedFunction) element);
    } else if (element instanceof KtConstructor) {
      collectConstructorDeclaration((KtConstructor) element);
    }
    super.visitElement(element);
  }

  private void collectMethodDeclaration(KtNamedFunction element) {
    KtModifierList ktModifierList = element.getModifierList();
    VisibilityModifier visibility = KotlinParserUtils.getVisibilityModifier(ktModifierList);
    //TODO: Should we include internal?
    if (visibility == VisibilityModifier.PUBLIC || visibility == VisibilityModifier.PROTECTED) {
      this.methodDeclaration = element.getName() + "()";
      this.isMethod = true;
    }
  }

  private void collectConstructorDeclaration(KtConstructor element) {
    KtModifierList ktModifierList = element.getModifierList();
    VisibilityModifier visibility = KotlinParserUtils.getVisibilityModifier(ktModifierList);
    //TODO: Should we include internal?
    if (visibility == VisibilityModifier.PUBLIC || visibility == VisibilityModifier.PROTECTED) {
      this.methodDeclaration = element.getText(); //TODO: FIXME. Extract only name of method following by empty "()".
      this.isMethod = false;
    }
  }

  // FIXME: It is not capturing constructors (super()).
  @Override
  public void visitSuperExpression(@NotNull KtSuperExpression expression) {
    SuperCallSite callSite = new SuperCallSite(programVersion, cuName, methodDeclaration,
      isMethod);
    superCallSites.add(callSite);
  }
}

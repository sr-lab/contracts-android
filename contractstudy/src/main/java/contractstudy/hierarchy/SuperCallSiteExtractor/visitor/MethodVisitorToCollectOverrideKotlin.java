package contractstudy.hierarchy.SuperCallSiteExtractor.visitor;

import com.github.javaparser.ast.AllFieldsConstructor;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.SuperExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import contractstudy.ProgramVersion;
import contractstudy.collectContracts.common.Utils;
import contractstudy.config.Preferences;
import contractstudy.constants.VisibilityModifier;
import contractstudy.hierarchy.model.SuperCallSite;
import contractstudy.kotlinParser.KotlinParserUtils;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.kotlin.com.intellij.psi.PsiElement;
import org.jetbrains.kotlin.psi.KtClassOrObject;
import org.jetbrains.kotlin.psi.KtConstructor;
import org.jetbrains.kotlin.psi.KtConstructorDelegationCall;
import org.jetbrains.kotlin.psi.KtModifierList;
import org.jetbrains.kotlin.psi.KtNamedFunction;
import org.jetbrains.kotlin.psi.KtSuperExpression;
import org.jetbrains.kotlin.psi.KtTreeVisitorVoid;

import java.util.List;

import static contractstudy.constants.SetStatsDataKeys.ALL_CONSTRUCTORS;
import static contractstudy.constants.SetStatsDataKeys.ALL_METHODS;
import static contractstudy.constants.SetStatsDataKeys.PUBLIC_CONSTRUCTORS;
import static contractstudy.constants.SetStatsDataKeys.PUBLIC_METHODS;

/**
 * Stores in a list instances of "super.foo()" or "super()".
 */
@Setter
public class MethodVisitorToCollectOverrideKotlin extends KtTreeVisitorVoid {

  private final boolean includePrivateMethods = Preferences.includePrivateMethods();
  private String cuName;
  private String methodDeclaration;
  private boolean isMethod = true; // false, if constructor
  private ProgramVersion programVersion;
  private List<SuperCallSite> superCallSites;

  public MethodVisitorToCollectOverrideKotlin(
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

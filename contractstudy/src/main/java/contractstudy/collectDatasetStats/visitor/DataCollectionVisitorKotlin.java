package contractstudy.collectDatasetStats.visitor;

import contractstudy.constants.VisibilityModifier;
import contractstudy.kotlinParser.KotlinParserUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.kotlin.com.intellij.psi.PsiElement;
import org.jetbrains.kotlin.psi.KtClassOrObject;
import org.jetbrains.kotlin.psi.KtConstructor;
import org.jetbrains.kotlin.psi.KtModifierList;
import org.jetbrains.kotlin.psi.KtNamedFunction;
import org.jetbrains.kotlin.psi.KtTreeVisitorVoid;

import java.util.Map;

import static contractstudy.constants.SetStatsDataKeys.ALL_CONSTRUCTORS;
import static contractstudy.constants.SetStatsDataKeys.ALL_METHODS;
import static contractstudy.constants.SetStatsDataKeys.CLASSES;
import static contractstudy.constants.SetStatsDataKeys.PUBLIC_CONSTRUCTORS;
import static contractstudy.constants.SetStatsDataKeys.PUBLIC_METHODS;

public class DataCollectionVisitorKotlin extends KtTreeVisitorVoid {

  private Map<String, Integer> data = null;

  public DataCollectionVisitorKotlin(Map<String, Integer> data) {
    super();
    this.data = data;
  }

  @Override
  public void visitElement(@NotNull PsiElement element) {
    if (element instanceof KtNamedFunction) {
      countPublicOrProtectedMethodDeclarations((KtNamedFunction) element);
    } else if (element instanceof KtConstructor) {
      countPublicOrProtectedConstructorDeclarations((KtConstructor) element);
    } else if (element instanceof KtClassOrObject) {
      countClassOrInterfaceOrObjectDeclaration((KtClassOrObject) element);
    }

    super.visitElement(element);
  }

  private void countPublicOrProtectedMethodDeclarations(KtNamedFunction element) {
    KtModifierList ktModifierList = element.getModifierList();
    VisibilityModifier visibility = KotlinParserUtils.getVisibilityModifier(ktModifierList);
    //TODO: Should we include internal?
    if (visibility == VisibilityModifier.PUBLIC || visibility == VisibilityModifier.PROTECTED) {
      data.compute(PUBLIC_METHODS.getKey(), (k, v) -> v == null ? 1 : v + 1);
    }
    data.compute(ALL_METHODS.getKey(), (k, v) -> v == null ? 1 : v + 1);
  }

  private void countPublicOrProtectedConstructorDeclarations(KtConstructor element) {
    KtModifierList ktModifierList = element.getModifierList();
    VisibilityModifier visibility = KotlinParserUtils.getVisibilityModifier(ktModifierList);
    //TODO: Should we include internal?
    if (visibility == VisibilityModifier.PUBLIC || visibility == VisibilityModifier.PROTECTED) {
      data.compute(PUBLIC_CONSTRUCTORS.getKey(), (k, v) -> v == null ? 1 : v + 1);
    }
    data.compute(ALL_CONSTRUCTORS.getKey(), (k, v) -> v == null ? 1 : v + 1);
  }


  private void countClassOrInterfaceOrObjectDeclaration(KtClassOrObject element) {
    data.compute(CLASSES.getKey(), (k, v) -> v == null ? 1 : v + 1);
  }

}

package contractstudy.usage.collectContracts.common.StaticImportCollector;

import contractstudy.constants.constraint.ConstraintCategory;
import contractstudy.usage.collectContracts.common.StaticImportCollector.constants.StaticImportState;
import contractstudy.utils.KotlinParserUtils;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.kotlin.psi.KtImportDirective;
import org.jetbrains.kotlin.psi.KtTreeVisitorVoid;

import java.util.ArrayList;
import java.util.List;

/**
 * From Imports list, finds type of import of targetQualifiedClassName (all static, some static, none, class).
 */
@Getter
@Setter
public class StaticImportCollectorKotlin extends KtTreeVisitorVoid {

  private String targetPackageName;
  private String targetQualifiedClassName;
  private ConstraintCategory constraintCategoryUnderInvestigation;
  private StaticImportState staticImportState = StaticImportState.NONE;
  private List<String> staticallyImportedMethodNames = new ArrayList<>();

  public StaticImportCollectorKotlin(
    String targetPackageName,
    String targetQualifiedClassName,
    ConstraintCategory constraintCategoryUnderInvestigation) {
    super();
    this.targetPackageName = targetPackageName;
    this.targetQualifiedClassName = targetQualifiedClassName;
    this.constraintCategoryUnderInvestigation = constraintCategoryUnderInvestigation;
  }

  public void visitImportDirective(@NotNull KtImportDirective importDirective) {
    switch (constraintCategoryUnderInvestigation) {
      case ANNOTATION:
        getImportStateForAnnotationInvestigation(importDirective);
      case API:
        getImportStateForAPIInvestigation(importDirective);
    }
    super.visitImportDirective(importDirective);
  }

  /**
   * When investigating annotations, we can only be certain that the annotation found belongs to the import directive when
   * the import contains the class. Therefore, if the import does not contain the class, we treat it as None.
   */
  private void getImportStateForAnnotationInvestigation(KtImportDirective importDirective) {
    boolean hasWildCard = KotlinParserUtils.doesImportDirectiveContainsWildCard(importDirective);
    String importedPath = importDirective.getImportPath().getPathStr();
    String importWithoutClass = importedPath.substring(0, importedPath.lastIndexOf("."));
    String className = importedPath.substring(importedPath.lastIndexOf(".") + 1);
    if (!hasWildCard && targetPackageName.equals(importWithoutClass) && !targetPackageName.contains(className)) {
      this.staticImportState = StaticImportState.CLASS;
    }
  }

  private void getImportStateForAPIInvestigation(KtImportDirective importDirective) {
    boolean hasWildCard = KotlinParserUtils.doesImportDirectiveContainsWildCard(importDirective);
    String importedPath = importDirective.getImportPath().getPathStr();
    String className = importedPath.substring(0, importedPath.lastIndexOf("."));
    String importedPathWithoutWildCard = importedPath;

    getImportStateForAnnotationInvestigation(importDirective);

    if (hasWildCard) {
      importedPathWithoutWildCard = importedPath.substring(0, importedPath.lastIndexOf(".*"));
    }

    if (hasWildCard && importedPathWithoutWildCard.equals(targetQualifiedClassName)) {
      this.staticImportState = StaticImportState.ALL_STATIC;
    } else if (!hasWildCard && targetPackageName.equals(className)) {
      this.staticImportState = StaticImportState.CLASS;
    }
  }

}

package contractstudy.usage.collectContracts.common.StaticImportCollector;

import contractstudy.usage.collectContracts.common.StaticImportCollector.constants.StaticImportState;
import contractstudy.utils.KotlinParserUtils;
import contractstudy.utils.StringUtils;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.kotlin.psi.KtImportDirective;
import org.jetbrains.kotlin.psi.KtTreeVisitorVoid;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
public class StaticImportCollectorKotlin extends KtTreeVisitorVoid {

  private String targetPackageName;
  private String targetQualifiedClassName;
  private StaticImportState staticImportState = StaticImportState.NONE;
  private List<String> staticallyImportedMethodNames = new ArrayList<>();

  public StaticImportCollectorKotlin(String targetPackageName, String targetQualifiedClassName) {
    super();
    this.targetPackageName = targetPackageName;
    this.targetQualifiedClassName = targetQualifiedClassName;
  }

  /**
   * From Imports list, finds type of imports. (all static, some static, none, class).
   */
  public void visitImportDirective(@NotNull KtImportDirective importDirective) {
    boolean hasWildCard = KotlinParserUtils.doesImportDirectiveContainsWildCard(importDirective);
    String importedPath = importDirective.getImportPath().getPathStr();
    String className = importedPath.substring(0, importedPath.lastIndexOf("."));
    String importedPathWithoutWildCard = importedPath;
    if (hasWildCard) {
      importedPathWithoutWildCard = importedPath.substring(0, importedPath.lastIndexOf(".*"));
    }

    if (hasWildCard && importedPathWithoutWildCard.equals(targetQualifiedClassName)) {
      this.staticImportState = StaticImportState.ALL_STATIC;
    } else if (!hasWildCard && (targetPackageName.equals(className) && targetQualifiedClassName.equals(importedPath))) {
      this.staticImportState = StaticImportState.CLASS;
    }

    super.visitImportDirective(importDirective);

  }

}

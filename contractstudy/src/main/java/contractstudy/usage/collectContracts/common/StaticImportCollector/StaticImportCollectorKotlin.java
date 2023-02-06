package contractstudy.usage.collectContracts.common.StaticImportCollector;

import contractstudy.usage.collectContracts.common.StaticImportCollector.constants.StaticImportState;
import contractstudy.utils.StringUtils;
import contractstudy.utils.kotlinParser.KotlinParserUtils;
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

  private String targetPackageName = null; // the target package, such as com.google.common.base
  private String targetQClassName = null; // the qualified class name, such as com.google.common.base.Preconditions
  // state used to return results
  private StaticImportState staticImportState = StaticImportState.NONE;
  private List<String> staticallyImportedMethodNames = new ArrayList<>();

  public StaticImportCollectorKotlin(String targetPackageName, String targetQClassName) {
    super();
    this.targetPackageName = targetPackageName;
    this.targetQClassName = targetQClassName;
  }

  /**
   * From Imports list, finds type of imports. (all static, some static, none, class).
   */
  public void visitImportDirective(@NotNull KtImportDirective importDirective) {
    String importedPath = importDirective.getImportPath().getPathStr();
    String importStatement = StringUtils.removeComments(importDirective.getText());

    boolean hasWildCard = KotlinParserUtils.doesImportDirectiveContainsWildCard(importDirective);

    if (isImportStatic(importStatement)) {
      setImportStateFromStatic(importDirective, hasWildCard, importedPath);
    } else {
      setImportStateFromNonStatic(hasWildCard, importedPath);
    }
  }

  private void setImportStateFromStatic(KtImportDirective importDirective, boolean hasWildCard,
    String importedPath) {
    String importClass = null;
    String importBasePath;

    try {
      importClass = Objects.requireNonNull(importDirective.getImportedName()).getIdentifier();
      importBasePath = getImportBasePath(importedPath, importClass);
    } catch (NullPointerException ignored) {
      importBasePath = importedPath;
    }

    if (hasWildCard && importedPath.equals(targetQClassName)) {
      this.staticImportState = StaticImportState.ALL_STATIC;
    } else {
      if (importBasePath.equals(targetQClassName)) {
        this.staticImportState = StaticImportState.SOME_STATIC;
        this.staticallyImportedMethodNames.add(importClass);
      }
    }
  }

  private void setImportStateFromNonStatic(boolean hasWildCard, String importedPath) {
    if (staticImportState == StaticImportState.NONE) {
      String pcgOnly = importedPath.replace(".*", "");
      if (!hasWildCard) {
        // suppose no inner classes are used
        pcgOnly = importedPath.substring(0, importedPath.lastIndexOf("."));
      }
      if (targetPackageName.equals(pcgOnly) || targetQClassName.equals(importedPath)) {
        this.staticImportState = StaticImportState.CLASS;
      }
    }
  }

  // TODO: Fixme
  private Boolean isImportStatic(String importStatement) {
    return false;
  }

  private String getImportBasePath(String importPath, String importLeaf) {
    return (importPath.split("." + importLeaf))[0];
  }

}

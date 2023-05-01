package contractstudy.usage.collectContracts.common.StaticImportCollector;

import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import contractstudy.usage.collectContracts.common.StaticImportCollector.constants.StaticImportState;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Visitor used to extract the state of static imports.
 * <p>
 * SOME_STATIC means that the static import does not contain a wild card.
 *
 * @author jens dietrich
 */
@SuppressWarnings("rawtypes")
@Getter
@Setter
public class StaticImportCollector extends VoidVisitorAdapter<Object> {

  private String targetPackageName;
  private String targetQualifiedClassName;
  private StaticImportState staticImportState = StaticImportState.NONE;
  private List<String> staticallyImportedMethodNames = new ArrayList<>();

  public StaticImportCollector(String targetPackageName, String targetQualifiedClassName) {
    super();
    this.targetPackageName = targetPackageName;
    this.targetQualifiedClassName = targetQualifiedClassName;
  }

  /**
   * From Imports list, finds type of imports. (all static, some static, none, class).
   */
  @Override
  public void visit(ImportDeclaration imp, Object arg) {
    String imported = imp.getName().toString();
    boolean hasWildCard = doesImportStatementContainsWildCard(imp);

    if (imp.isStatic()) {
      setImportStateFromStatic(imp, hasWildCard, imported);
    } else {
      setImportStateFromNonStatic(imp, hasWildCard, imported);
    }

    super.visit(imp, arg);
  }

  private boolean doesImportStatementContainsWildCard(ImportDeclaration imp) {
    return imp.removeComment().toString().contains(".*");
  }

  private void setImportStateFromStatic(
    ImportDeclaration imp,
    boolean hasWildCard,
    String imported
  ) {
    if (hasWildCard && imported.equals(targetQualifiedClassName)) {
      this.staticImportState = StaticImportState.ALL_STATIC;
    } else {
      String className = imp.getName().getChildNodes().get(0).toString();
      String methodName = imp.getName().getIdentifier();
      if (className.equals(targetQualifiedClassName)) {
        this.staticImportState = StaticImportState.SOME_STATIC;
        this.staticallyImportedMethodNames.add(methodName);
      }
    }
  }

  private void setImportStateFromNonStatic(
    ImportDeclaration imp,
    boolean hasWildCard,
    String imported
  ) {
    if (staticImportState == StaticImportState.NONE) {
      String importName = imp.getName().removeComment().toString();
      String pcgOnly = importName;

      if (!hasWildCard) {
        pcgOnly = importName.substring(0, importName.lastIndexOf("."));
      }

      if (targetPackageName.equals(pcgOnly) || targetQualifiedClassName.equals(imported)) {
        this.staticImportState = StaticImportState.CLASS;
      }
    }
  }

}

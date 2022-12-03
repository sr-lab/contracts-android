package contractstudy.collectContracts.common.StaticImportCollector;

import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import contractstudy.collectContracts.common.StaticImportCollector.constants.StaticImportState;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Visitor used to extract the state of static imports.
 *
 * @author jens dietrich
 */
@SuppressWarnings("rawtypes")
@Getter
@Setter
public class StaticImportCollector extends VoidVisitorAdapter<Object> {

  private String targetPackageName = null; // the target package, such as com.google.common.base
  private String targetQClassName = null; // the qualified class name, such as com.google.common.base.Preconditions
  // state used to return results
  private StaticImportState staticImportState = StaticImportState.NONE;
  private List<String> staticallyImportedMethodNames = new ArrayList<>();

  public StaticImportCollector(String targetPackageName, String targetQClassName) {
    super();
    this.targetPackageName = targetPackageName;
    this.targetQClassName = targetQClassName;
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
    //boolean hasWildcard = imp.toStringWithoutComments().contains(".*");
    return imp.removeComment().toString().contains(".*"); // JFF: FIXME?
  }

  private void setImportStateFromStatic(ImportDeclaration imp, boolean hasWildCard,
    String imported) {
    if (hasWildCard && imported.equals(targetQClassName)) {
      this.staticImportState = StaticImportState.ALL_STATIC;
    } else {
      //String clName = imp.getName().getChildrenNodes().get(0).toString();
      String clName = imp.getName().getChildNodes().get(0).toString(); // JFF: FIXME?
      //String mName = imp.getName().getName();
      String mName = imp.getName().getIdentifier(); // JFF: FIXME?
      if (clName.equals(targetQClassName)) {
        this.staticImportState = StaticImportState.SOME_STATIC;
        this.staticallyImportedMethodNames.add(mName);
      }
    }
  }

  private void setImportStateFromNonStatic(ImportDeclaration imp, boolean hasWildCard,
    String imported) {
    if (staticImportState == StaticImportState.NONE) {
      //String importName = imp.getName().toStringWithoutComments();
      String importName = imp.getName().removeComment().toString();
      String pcgOnly = importName;

      // import name contains the class name after the last dot,
      // or the whole package if wildcard is used.
      if (!hasWildCard) {
        pcgOnly = importName.substring(0,
          importName.lastIndexOf("."));  // suppose no inner classes are used
      }

      if (targetPackageName.equals(pcgOnly) || targetQClassName.equals(imported)) {
        this.staticImportState = StaticImportState.CLASS;
      }
    }
  }

}

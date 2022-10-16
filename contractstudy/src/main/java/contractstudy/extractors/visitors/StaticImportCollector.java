package contractstudy.extractors.visitors;

import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Visitor used to extract the state of static imports.
 *
 * @author jens dietrich
 */
@SuppressWarnings("rawtypes")
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

  public StaticImportState getStaticImportState() {
    return staticImportState;
  }

  public void setStaticImportState(StaticImportState staticImportState) {
    this.staticImportState = staticImportState;
  }

  public List<String> getStaticallyImportedMethodNames() {
    return staticallyImportedMethodNames;
  }

  public void setStaticallyImportedMethodNames(List<String> staticallyImportedMethodNames) {
    this.staticallyImportedMethodNames = staticallyImportedMethodNames;
  }

  @Override
  public void visit(ImportDeclaration imp, Object arg) {
    String imported = imp.getName().toString();

    //boolean hasWildcard = imp.toStringWithoutComments().contains(".*");
    boolean hasWildcard = imp.removeComment().toString().contains(".*"); // JFF: FIXME?
    if (imp.isStatic()) {
      if (hasWildcard && imported.equals(targetQClassName)) {
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
    } else {
      if (staticImportState == StaticImportState.NONE) {
        //String importName = imp.getName().toStringWithoutComments();
        String importName = imp.getName().removeComment().toString();
        String pcgOnly = importName;

        // import name contains the class name after the last dot,
        // or the whole package if wildcard is used.
        if (!hasWildcard) {
          pcgOnly = importName.substring(0,
            importName.lastIndexOf("."));  // suppose no inner classes are used
        }

        if (targetPackageName.equals(pcgOnly) || targetQClassName.equals(imported)) {
          this.staticImportState = StaticImportState.CLASS;
        }
      }
    }
    super.visit(imp, arg);
  }

}

package contractstudy.inheritance.SuperCallSiteExtractor.visitor;

import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.SuperExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import contractstudy.config.Preferences;
import contractstudy.inheritance.model.SuperCallSite;
import contractstudy.model.ProgramVersion;
import contractstudy.usage.collectContracts.common.Utils;
import lombok.Setter;

import java.util.List;

/**
 * Stores in a list instances of "super.foo()" or "super()".
 */
@Setter
public class MethodVisitorToCollectSuperCall extends VoidVisitorAdapter<Object> {

  private final boolean includePrivateMethods = Preferences.includePrivateMethods();
  private String cuName;
  private String methodDeclaration;
  private boolean isMethod = true; // false, if constructor
  private ProgramVersion programVersion;
  private List<SuperCallSite> superCallSites;

  public MethodVisitorToCollectSuperCall(
    String cuName,
    ProgramVersion programVersion,
    List<SuperCallSite> superCallSites) {
    this.cuName = cuName;
    this.programVersion = programVersion;
    this.superCallSites = superCallSites;
  }

  @Override
  public void visit(MethodDeclaration methodDeclr, Object arg) {
    NodeList<Modifier> modifiers = methodDeclr.getModifiers();
    if (includePrivateMethods || modifiers.contains(Modifier.publicModifier())
      || modifiers.contains(Modifier.protectedModifier())) {
      this.methodDeclaration = Utils.trimRetType(methodDeclr.getDeclarationAsString(false, false,
        false)); // TODO: FIXME. It can return a test3(int, where expected is test3().
      this.isMethod = true;
      super.visit(methodDeclr, arg);
    }
  }

  @Override
  public void visit(ConstructorDeclaration constructorDeclr, Object arg) {
    NodeList<Modifier> modifiers = constructorDeclr.getModifiers();
    if (includePrivateMethods || modifiers.contains(Modifier.publicModifier())
      || modifiers.contains(Modifier.protectedModifier())) {
      this.methodDeclaration = constructorDeclr.getDeclarationAsString(false, false, false);
      this.isMethod = false;
      super.visit(constructorDeclr, arg);
    }
  }

  // FIXME: It is not capturing constructors (super()).
  @Override
  public void visit(SuperExpr n, Object arg) {
    SuperCallSite callSite = new SuperCallSite(programVersion, cuName, methodDeclaration,
      isMethod);
    superCallSites.add(callSite);
    super.visit(n, arg);
  }
}

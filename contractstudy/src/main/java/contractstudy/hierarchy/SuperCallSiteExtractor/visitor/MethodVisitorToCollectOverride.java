package contractstudy.hierarchy.SuperCallSiteExtractor.visitor;

import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.SuperExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import contractstudy.ProgramVersion;
import contractstudy.collectContracts.common.Utils;
import contractstudy.config.Preferences;
import contractstudy.hierarchy.model.SuperCallSite;
import lombok.Setter;

import java.util.List;

@Setter
public class MethodVisitorToCollectOverride extends VoidVisitorAdapter<Object> {

  private final boolean includePrivateMethods = Preferences.includePrivateMethods();
  private String cuName = null;
  private String methodDeclaration = null;
  private boolean isMethod = true; // false = constructor
  private ProgramVersion programVersion = null;
  private List<SuperCallSite> superCallSites = null;

  public MethodVisitorToCollectOverride(
    String cuName,
    ProgramVersion programVersion,
    List<SuperCallSite> superCallSites) {
    this.cuName = cuName;
    this.programVersion = programVersion;
    this.superCallSites = superCallSites;
  }

  // control the methods being visited
  @Override
  public void visit(MethodDeclaration methodDeclr, Object arg) {
    //int modifiers = methodDeclr.getModifiers();
    NodeList<Modifier> modifiers = methodDeclr.getModifiers();
    //if (includePrivateMethods || ModifierSet.isPublic(modifiers) || ModifierSet.isProtected(modifiers)) {
    if (includePrivateMethods || modifiers.contains(Modifier.publicModifier())
      || modifiers.contains(Modifier.protectedModifier())) {
      this.methodDeclaration = Utils.trimRetType(methodDeclr.getDeclarationAsString(false, false,
        false)); // flags: incl modifiers , incl throws
      this.isMethod = true;
      super.visit(methodDeclr, arg);
    }
  }

  @Override
  public void visit(ConstructorDeclaration constructorDeclr, Object arg) {
    //int modifiers = constructorDeclr.getModifiers();
    NodeList<Modifier> modifiers = constructorDeclr.getModifiers();
    //if (includePrivateMethods || ModifierSet.isPublic(modifiers) || ModifierSet.isProtected(modifiers)) {
    if (includePrivateMethods || modifiers.contains(Modifier.publicModifier())
      || modifiers.contains(Modifier.protectedModifier())) {
      this.methodDeclaration = constructorDeclr.getDeclarationAsString(false, false, false);
      this.isMethod = false;
      super.visit(constructorDeclr, arg);
    }
  }

  // this captures both methods (super.foo()) and constructors (super())
  @Override
  public void visit(SuperExpr n, Object arg) {
    SuperCallSite callSite = new SuperCallSite(programVersion, cuName, methodDeclaration,
      isMethod);
    superCallSites.add(callSite);
    super.visit(n, arg);
  }
}

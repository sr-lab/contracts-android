package contractstudy.inheritance.SuperCallSiteExtractor.visitor;

import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.SuperExpr;
import com.github.javaparser.ast.stmt.ExplicitConstructorInvocationStmt;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import contractstudy.inheritance.model.SuperCallSite;
import contractstudy.model.ProgramVersion;
import contractstudy.usage.collectContracts.common.Utils;
import lombok.Setter;

import java.util.List;

import static contractstudy.utils.KotlinParserUtils.isJavaMethodVisibilityAccepted;

/**
 * Stores in a list instances of "super.foo()" or "super()".
 */
@Setter
public class MethodVisitorToCollectSuperCall extends VoidVisitorAdapter<Object> {

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
    if (cuName.contains("PhotoPropertiesChainReader")) {
      System.out.println("nice");
    }
    NodeList<Modifier> modifiers = methodDeclr.getModifiers();
    if (isJavaMethodVisibilityAccepted(modifiers)) {
      this.methodDeclaration = Utils.trimReturnType(methodDeclr.getDeclarationAsString(false, false, false));
      this.isMethod = true;
      super.visit(methodDeclr, arg);
    }
  }

  @Override
  public void visit(ConstructorDeclaration constructorDeclr, Object arg) {
    if (cuName.contains("PhotoPropertiesChainReader")) {
      System.out.println("nice");
    }
    NodeList<Modifier> modifiers = constructorDeclr.getModifiers();
    if (isJavaMethodVisibilityAccepted(modifiers)) {
      this.methodDeclaration = constructorDeclr.getDeclarationAsString(false, false, false);
      this.isMethod = false;
      super.visit(constructorDeclr, arg);
    }
  }

  @Override
  public void visit(SuperExpr n, Object arg) {
    if (cuName.contains("PhotoPropertiesChainReader")) {
      System.out.println("nice");
    }
    SuperCallSite callSite = new SuperCallSite(programVersion, cuName, methodDeclaration, isMethod);
    superCallSites.add(callSite);
    super.visit(n, arg);
  }

  @Override
  public void visit(ExplicitConstructorInvocationStmt n, Object arg) {
    if (cuName.contains("PhotoPropertiesChainReader")) {
      System.out.println("nice");
    }
    super.visit(n, arg);
    if (n.isThis()) {
      return;
    }
    if (n.toString().replace(" ", "").startsWith("super")) {
      SuperCallSite callSite = new SuperCallSite(programVersion, cuName, methodDeclaration, isMethod);
      superCallSites.add(callSite);
    }
  }
}

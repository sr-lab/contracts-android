package contractstudy.collectDatasetStats.visitor;

import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.util.Map;

import static contractstudy.constants.SetStatsDataKeys.ALL_CONSTRUCTORS;
import static contractstudy.constants.SetStatsDataKeys.ALL_METHODS;
import static contractstudy.constants.SetStatsDataKeys.CLASSES;
import static contractstudy.constants.SetStatsDataKeys.PUBLIC_CONSTRUCTORS;
import static contractstudy.constants.SetStatsDataKeys.PUBLIC_METHODS;

public class DataCollectionVisitor extends VoidVisitorAdapter<Object> {

  private final Map<String, Integer> data;

  public DataCollectionVisitor(Map<String, Integer> data) {
    super();
    this.data = data;
  }

  @Override
  public void visit(MethodDeclaration methodDeclr, Object arg) {
    if (checkIfPublicOrPrivate(methodDeclr.getModifiers())) {
      data.compute(PUBLIC_METHODS.getKey(), (k, v) -> v == null ? 1 : v + 1);
    }
    data.compute(ALL_METHODS.getKey(), (k, v) -> v == null ? 1 : v + 1);
    super.visit(methodDeclr, arg);
  }

  @Override
  public void visit(ConstructorDeclaration constructorDeclr, Object arg) {
    if (checkIfPublicOrPrivate(constructorDeclr.getModifiers())) {
      data.compute(PUBLIC_CONSTRUCTORS.getKey(), (k, v) -> v == null ? 1 : v + 1);
    }
    data.compute(ALL_CONSTRUCTORS.getKey(), (k, v) -> v == null ? 1 : v + 1);
    super.visit(constructorDeclr, arg);
  }

  //TODO: Can we always consider none to be public?
  public boolean checkIfPublicOrPrivate(NodeList<Modifier> modifiers) {
    return modifiers.contains(Modifier.publicModifier()) || modifiers.contains(
      Modifier.protectedModifier()) || modifiers.isEmpty();
  }

  @Override
  public void visit(ClassOrInterfaceDeclaration n, Object arg) {
    super.visit(n, arg);
    data.compute(CLASSES.getKey(), (k, v) -> v == null ? 1 : v + 1);
  }
}

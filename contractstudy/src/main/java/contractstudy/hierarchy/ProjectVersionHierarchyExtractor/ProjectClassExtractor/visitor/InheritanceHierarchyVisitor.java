package contractstudy.hierarchy.ProjectVersionHierarchyExtractor.ProjectClassExtractor.visitor;

import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.expr.SuperExpr;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import contractstudy.model.ClassAndVersion;
import contractstudy.hierarchy.model.ClassFinder;
import contractstudy.hierarchy.model.ClassCoordinates;
import contractstudy.hierarchy.model.ClassParents;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author Kamil Jezek [kamil.jezek@verifalabs.com]
 */
public class InheritanceHierarchyVisitor extends ClassDefinitionVisitor implements ClassParents,
  ClassCoordinates {

  private final ClassFinder classFinder;
  private final List<String> packages = new ArrayList<>();

  public InheritanceHierarchyVisitor(
    final String cuName,
    final ClassFinder classFinder) {
    super(cuName);
    this.classFinder = classFinder;
  }

  @Override
  public void visit(PackageDeclaration n, Object arg) {
    String packageName = n.getName().getIdentifier();
    addImplicitImports(packageName);
    super.visit(n, arg);
  }

  private void addImplicitImports(String packageName) {
    packages.add(packageName + ".*");
    packages.add("kotlin.*");
    packages.add("java.lang.*");
  }


  @Override
  public void visit(ImportDeclaration n, Object arg) {
    if (!n.isStatic()) {
      //String pcg = n.getName().toStringWithoutComments();
      String pcg = n.getName().getIdentifier(); // JFF: FIXME: without comments?
      if (n.isAsterisk()) {
        pcg += ".*";
      }
      packages.add(pcg);
    }
    super.visit(n, arg);
  }

  @Override
  public void visit(ClassOrInterfaceDeclaration n, Object arg) {
    super.visit(n, arg);
    List<ClassOrInterfaceType> superClasses = n.getExtendedTypes();
    List<ClassOrInterfaceType> superInterfaces = n.getImplementedTypes();
    findClasses(n, superClasses);
    findClasses(n, superInterfaces);
  }

  @Override
  public void visit(SuperExpr n, Object arg) {
    super.visit(n, arg);
  }

  private void findClasses(ClassOrInterfaceDeclaration n, List<ClassOrInterfaceType> types) {
    for (ClassOrInterfaceType type : types) {
      String typeName = type.getNameWithScope(); // type.getName().getIdentifier(); // JFF
      ClassAndVersion classAndOrigin = classFinder.findClass(typeName,
        packages.toArray(new String[0]));
      if (classAndOrigin != null) {
        getState(n).getParents().add(classAndOrigin);
      }
    }
  }


  @Override
  public Set<ClassAndVersion> getParents(String className) {
    return getInnerClassesState().get(className).getParents();
  }
}

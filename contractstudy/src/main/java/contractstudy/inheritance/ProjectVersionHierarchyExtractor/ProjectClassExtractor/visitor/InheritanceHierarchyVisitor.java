package contractstudy.inheritance.ProjectVersionHierarchyExtractor.ProjectClassExtractor.visitor;

import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import contractstudy.inheritance.model.ClassCoordinates;
import contractstudy.inheritance.model.ClassParents;
import contractstudy.inheritance.model.SourceClassFinder;
import contractstudy.model.ClassAndVersion;
import contractstudy.utils.GeneralUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author Kamil Jezek [kamil.jezek@verifalabs.com]
 */
public class InheritanceHierarchyVisitor extends ClassDefinitionVisitor implements ClassParents,
  ClassCoordinates {

  private final SourceClassFinder classFinder;
  private final List<String> packages = new ArrayList<>();

  public InheritanceHierarchyVisitor(
    final String cuName,
    final SourceClassFinder classFinder
  ) {
    super(cuName);
    this.classFinder = classFinder;
  }

  @Override
  public void visit(PackageDeclaration n, Object arg) {
    String packageName = n.getName().getIdentifier();
    packages.addAll(GeneralUtils.getImplicitImportsForPackageName(packageName));
    super.visit(n, arg);
  }

  @Override
  public void visit(ImportDeclaration n, Object arg) {
    if (!n.isStatic()) {
      String pcg = n.getName().getIdentifier();
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
    addParentToChildClassStateIfItBelongsToItsImports(n, superClasses);
    addParentToChildClassStateIfItBelongsToItsImports(n, superInterfaces);
  }

  private void addParentToChildClassStateIfItBelongsToItsImports(
    ClassOrInterfaceDeclaration n,
    List<ClassOrInterfaceType> superClassesAndInterfaces
  ) {
    for (ClassOrInterfaceType superClass : superClassesAndInterfaces) {
      try {
        String superClassName = superClass.getNameWithScope();
        ClassAndVersion potentialParent = classFinder.findClass(superClassName, packages.toArray(new String[0]));
        if (potentialParent != null) {
          getOwnerState(n).getParents().add(potentialParent);
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  @Override
  public Set<ClassAndVersion> getParents(String className) {
    return getInnerClassesState().get(className).getParents();
  }
}

package contractstudy.inheritance.ProjectVersionHierarchyExtractor.ProjectClassExtractor;

import com.github.javaparser.ast.CompilationUnit;
import contractstudy.inheritance.ProjectVersionHierarchyExtractor.ProjectClassExtractor.visitor.ClassDefinitionVisitor;
import contractstudy.inheritance.ProjectVersionHierarchyExtractor.ProjectClassExtractor.visitor.InheritanceHierarchyVisitor;
import contractstudy.inheritance.model.ClassCoordinates;
import contractstudy.inheritance.model.ClassParents;
import contractstudy.inheritance.model.SourceClassFinder;

/**
 * Extract information from single class.
 *
 * @author Kamil Jezek [kamil.jezek@verifalabs.com]
 */
public class ProjectClassExtractor {

  public ClassCoordinates readClass(
    final CompilationUnit cu,
    final String cuName) {
    ClassDefinitionVisitor visitor = new ClassDefinitionVisitor(cuName);
    visitor.visit(cu, null);
    return visitor;
  }

  public ClassParents readInheritance(
    final CompilationUnit cu,
    final String cuName,
    final SourceClassFinder classFinder) {
    InheritanceHierarchyVisitor visitor = new InheritanceHierarchyVisitor(cuName, classFinder);
    visitor.visit(cu, null);
    return visitor;
  }
}

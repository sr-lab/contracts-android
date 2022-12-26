package contractstudy.hierarchy.ProjectVersionHierarchyExtractor.ProjectClassExtractor;

import com.github.javaparser.ast.CompilationUnit;
import contractstudy.hierarchy.ProjectVersionHierarchyExtractor.ProjectClassExtractor.visitor.ClassDefinitionVisitor;
import contractstudy.hierarchy.ProjectVersionHierarchyExtractor.ProjectClassExtractor.visitor.InheritanceHierarchyVisitor;
import contractstudy.hierarchy.model.ClassCoordinates;
import contractstudy.hierarchy.model.ClassFinder;
import contractstudy.hierarchy.model.ClassParents;

/**
 * Extract information from single class.
 *
 * @author Kamil Jezek [kamil.jezek@verifalabs.com]
 */
public class ProjectClassExtractor {

  public ClassParents readInheritance(
    final CompilationUnit cu,
    final String cuName,
    final ClassFinder classFinder) throws Exception {
    InheritanceHierarchyVisitor visitor = new InheritanceHierarchyVisitor(cuName, classFinder);
    visitor.visit(cu, null);
    return visitor;
  }

  public ClassCoordinates readClass(
    final CompilationUnit cu,
    final String cuName) throws Exception {
    ClassDefinitionVisitor visitor = new ClassDefinitionVisitor(cuName);
    visitor.visit(cu, null);
    return visitor;
  }
}

package contractstudy.hierarchy.ProjectVersionHierarchyExtractor.ProjectClassExtractorKotlin;

import contractstudy.hierarchy.ProjectVersionHierarchyExtractor.ProjectClassExtractorKotlin.visitor.ClassDefinitionVisitorKotlin;
import contractstudy.hierarchy.ProjectVersionHierarchyExtractor.ProjectClassExtractorKotlin.visitor.InheritanceHierarchyVisitorKotlin;
import contractstudy.hierarchy.model.ClassCoordinates;
import contractstudy.hierarchy.model.ClassFinder;
import contractstudy.hierarchy.model.ClassParents;
import org.jetbrains.kotlin.com.intellij.psi.PsiFile;

/**
 * Extract information from single class.
 */
public class ProjectClassExtractorKotlin {

  public ClassParents readInheritance(
    final PsiFile cu,
    final String cuName,
    final ClassFinder classFinder) throws Exception {
    InheritanceHierarchyVisitorKotlin visitor = new InheritanceHierarchyVisitorKotlin(cuName,
      classFinder);
    cu.accept(visitor);
    return visitor;
  }

  public ClassCoordinates readClass(
    final PsiFile cu,
    final String cuName) throws Exception {
    ClassDefinitionVisitorKotlin visitor = new ClassDefinitionVisitorKotlin(cuName);
    cu.accept(visitor);
    return visitor;
  }
}

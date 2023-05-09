package contractstudy.inheritance.ProjectVersionHierarchyExtractor.ProjectClassExtractorKotlin;

import contractstudy.inheritance.ProjectVersionHierarchyExtractor.ProjectClassExtractorKotlin.visitor.ClassDefinitionVisitorKotlin;
import contractstudy.inheritance.ProjectVersionHierarchyExtractor.ProjectClassExtractorKotlin.visitor.InheritanceHierarchyVisitorKotlin;
import contractstudy.inheritance.model.ClassCoordinates;
import contractstudy.inheritance.model.ClassParents;
import contractstudy.inheritance.model.SourceClassFinder;
import org.jetbrains.kotlin.com.intellij.psi.PsiFile;

/**
 * Extract information from single class.
 */
public class ProjectClassExtractorKotlin {

  public ClassCoordinates readClass(
    final PsiFile cu,
    final String cuName) {
    ClassDefinitionVisitorKotlin visitor = new ClassDefinitionVisitorKotlin(cuName);
    cu.accept(visitor);
    return visitor;
  }

  public ClassParents readInheritance(
    final PsiFile cu,
    final String cuName,
    final SourceClassFinder classFinder) {
    InheritanceHierarchyVisitorKotlin visitor = new InheritanceHierarchyVisitorKotlin(cuName, classFinder);
    cu.accept(visitor);
    return visitor;
  }
}

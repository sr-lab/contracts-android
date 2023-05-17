package contractstudy.inheritance.ProjectVersionHierarchyExtractor.ProjectClassExtractorKotlin.visitor;

import contractstudy.inheritance.model.ClassCoordinates;
import contractstudy.inheritance.model.ClassParents;
import contractstudy.inheritance.model.SourceClassFinder;
import contractstudy.model.ClassAndVersion;
import contractstudy.utils.GeneralUtils;
import contractstudy.utils.KotlinParserUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.kotlin.psi.KtClassOrObject;
import org.jetbrains.kotlin.psi.KtImportDirective;
import org.jetbrains.kotlin.psi.KtPackageDirective;
import org.jetbrains.kotlin.psi.KtSuperTypeListEntry;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class InheritanceHierarchyVisitorKotlin extends ClassDefinitionVisitorKotlin implements
  ClassParents,
  ClassCoordinates {

  private final SourceClassFinder classFinder;
  private final List<String> packages = new ArrayList<>();

  public InheritanceHierarchyVisitorKotlin(
    final String cuName,
    final SourceClassFinder classFinder
  ) {
    super(cuName);
    this.classFinder = classFinder;
  }

  @Override
  public void visitPackageDirective(@NotNull KtPackageDirective directive) {
    String packageName = directive.getName();
    packages.addAll(GeneralUtils.getImplicitImportsForPackageName(packageName));
    super.visitPackageDirective(directive);
  }

  @Override
  public void visitImportDirective(@NotNull KtImportDirective importDirective) {
    String importedName = KotlinParserUtils.getImportedName(importDirective);
    packages.add(importedName);
    super.visitImportDirective(importDirective);
  }

  @Override
  public void visitClassOrObject(@NotNull KtClassOrObject classOrObject) {
    super.visitClassOrObject(classOrObject);
    List<KtSuperTypeListEntry> superClassesAndInterfaces = classOrObject.getSuperTypeListEntries();
    addParentToChildClassStateIfItBelongsToItsImports(classOrObject, superClassesAndInterfaces);
  }

  private void addParentToChildClassStateIfItBelongsToItsImports(
    KtClassOrObject n,
    List<KtSuperTypeListEntry> superClassesAndInterfaces
  ) {
    for (KtSuperTypeListEntry superClass : superClassesAndInterfaces) {
      String superClassName = superClass.getTypeReference().getText();
      ClassAndVersion potentialParent = classFinder.findClass(superClassName, packages.toArray(new String[0]));
      if (potentialParent != null) {
        getOwnerState(n).getParents().add(potentialParent);
      }
    }
  }

  @Override
  public Set<ClassAndVersion> getParents(String className) {
    return getInnerClassesState().get(className).getParents();
  }
}

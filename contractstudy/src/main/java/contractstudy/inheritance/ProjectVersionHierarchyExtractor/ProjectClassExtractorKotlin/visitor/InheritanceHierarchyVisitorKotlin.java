package contractstudy.inheritance.ProjectVersionHierarchyExtractor.ProjectClassExtractorKotlin.visitor;

import contractstudy.inheritance.model.ClassCoordinates;
import contractstudy.inheritance.model.ClassFinder;
import contractstudy.inheritance.model.ClassParents;
import contractstudy.model.ClassAndVersion;
import contractstudy.utils.kotlinParser.KotlinParserUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.kotlin.psi.KtClassOrObject;
import org.jetbrains.kotlin.psi.KtImportDirective;
import org.jetbrains.kotlin.psi.KtPackageDirective;
import org.jetbrains.kotlin.psi.KtSuperTypeListEntry;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class InheritanceHierarchyVisitorKotlin extends ClassDefinitionVisitorKotlin implements
  ClassParents,
  ClassCoordinates {

  private final ClassFinder classFinder;
  private final List<String> packages = new ArrayList<>();

  public InheritanceHierarchyVisitorKotlin(
    final String cuName,
    final ClassFinder classFinder) {
    super(cuName);
    this.classFinder = classFinder;
  }

  @Override
  public void visitPackageDirective(@NotNull KtPackageDirective directive) {
    String packageName = directive.getName();
    addImplicitImports(packageName);
    super.visitPackageDirective(directive);
  }

  private void addImplicitImports(String packageName) {
    packages.add(packageName + ".*");
    packages.add("kotlin.*");
    packages.add("java.lang.*");
  }

  @Override
  public void visitImportDirective(@NotNull KtImportDirective importDirective) {
    String importedName = "";
    try {
      importedName = Objects.requireNonNull(importDirective.getImportedName()).toString();
    } catch (NullPointerException e) {
      if (KotlinParserUtils.doesImportDirectiveContainsWildCard(importDirective)) {
        importedName += ".*";
      }
    }
    if (!Objects.equals(importedName, "")) {
      packages.add(importedName);
      super.visitImportDirective(importDirective);
    }
  }

  @Override
  public void visitClassOrObject(@NotNull KtClassOrObject classOrObject) {
    super.visitClassOrObject(classOrObject);
    List<KtSuperTypeListEntry> entries = classOrObject.getSuperTypeListEntries();
    findClasses(classOrObject, entries);
  }

  private void findClasses(KtClassOrObject n, List<KtSuperTypeListEntry> types) {
    for (KtSuperTypeListEntry entry : types) {
      String typeName = entry.getTypeReference().getText();
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

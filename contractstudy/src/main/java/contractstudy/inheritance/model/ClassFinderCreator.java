package contractstudy.inheritance.model;

import com.github.javaparser.ast.CompilationUnit;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Table;
import contractstudy.model.ProgramVersion;
import org.jetbrains.kotlin.com.intellij.psi.PsiFile;

/**
 * Simple data collector that at the and produces the class finder.
 *
 * @author Kamil Jezek [kamil.jezek@verifalabs.com]
 */
public class ClassFinderCreator {

  /**
   * Package
   */
  private Multimap<String, String> classPcgs = HashMultimap.create();

  /**
   * Class origin
   */
  private Multimap<String, ProgramVersion> classOrigin = HashMultimap.create();

  /**
   * Compilation unit
   */
  private Table<ClassCoordinates, ProgramVersion, CompilationUnit> cus = HashBasedTable.create();
  private Table<ClassCoordinates, ProgramVersion, PsiFile> psis = HashBasedTable.create();

  /**
   * Compilation unit name
   */
  private Table<String, ProgramVersion, String> cuNames = HashBasedTable.create();


  public ClassFinderCreator() {
  }

  public ClassFinderCreator(ClassFinderCreator globalCreator) {
    this.classOrigin = HashMultimap.create(globalCreator.classOrigin);
    this.classPcgs = HashMultimap.create(globalCreator.classPcgs);
    this.cus = HashBasedTable.create(globalCreator.cus);
    this.psis = HashBasedTable.create(globalCreator.psis);
    this.cuNames = HashBasedTable.create(globalCreator.cuNames);
  }

  public void add(
    final ProgramVersion programVersion,
    final ClassCoordinates classCoordinates,
    final CompilationUnit cu,
    final String cuName) {
    classPcgs.put(classCoordinates.getClassSimpleName(), classCoordinates.getPackageName());
    classOrigin.put(classCoordinates.getClassName(), programVersion);
    cus.put(classCoordinates, programVersion, cu);
    cuNames.put(classCoordinates.getClassName(), programVersion, cuName);
  }

  public void add(
    final ProgramVersion programVersion,
    final ClassCoordinates classCoordinates,
    final PsiFile psi,
    final String cuName) {
    classPcgs.put(classCoordinates.getClassSimpleName(), classCoordinates.getPackageName());
    classOrigin.put(classCoordinates.getClassName(), programVersion);
    psis.put(classCoordinates, programVersion, psi);
    cuNames.put(classCoordinates.getClassName(), programVersion, cuName);
  }

  public SourceClassFinder toFinder() {
    return new SourceClassFinder(classPcgs, classOrigin, cuNames);
  }

  public Table<ClassCoordinates, ProgramVersion, CompilationUnit> getCus() {
    return cus;
  }

  public Table<ClassCoordinates, ProgramVersion, PsiFile> getPsis() {
    return psis;
  }
}

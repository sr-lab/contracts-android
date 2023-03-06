package contractstudy.inheritance.model;

import com.google.common.collect.Multimap;
import com.google.common.collect.Table;
import contractstudy.model.ClassAndVersion;
import contractstudy.model.ProgramVersion;

import java.util.Collection;

public class SourceClassFinder {

  /**
   * key - simple class name, value - package
   */
  private final Multimap<String, String> classPackages;
  /**
   * key - class origin, value - full class name
   */
  private final Multimap<String, ProgramVersion> classOrigin;
  /**
   * key - class name, program version, value - CU
   */
  Table<String, ProgramVersion, String> cuNames;

  public SourceClassFinder(
    final Multimap<String, String> classPackages,
    final Multimap<String, ProgramVersion> classOrigin,
    final Table<String, ProgramVersion, String> cuNames
  ) {
    this.classPackages = classPackages;
    this.classOrigin = classOrigin;
    this.cuNames = cuNames;
  }

  public ClassAndVersion findClass(final String superClassName, final String[] imports) {
    Collection<String> parentClasses = classPackages.get(superClassName);
    ClassAndVersion r = null;
    if (!parentClasses.isEmpty()) {
      String fullClassName = doesImportClassNameCorrespondsWithImports(superClassName,
        parentClasses, imports);
      if (fullClassName != null) {
        r = createClassAndOrigin(fullClassName);
      }
    }
    return r;
  }


  private String doesImportClassNameCorrespondsWithImports(
    final String simpleClassName,
    final Collection<String> parentClasses,
    final String[] imports
  ) {
    String r = null;
    for (String importPcg : imports) {
      for (String parent : parentClasses) {
        if (importPcg.endsWith(".*")) {
          if (importPcg.substring(0, importPcg.lastIndexOf('.')).equals(parent)) {
            r = getClassName(parent, simpleClassName);
            break;
          }
        } else {
          String fullClassName = getClassName(parent, simpleClassName);
          if (importPcg.equals(fullClassName)) {
            r = importPcg;
            break;
          }
        }
      }
    }
    return r;
  }

  public String getClassName(String packageName, String classSimpleName) {
    String name = classSimpleName;
    if (packageName != null) {
      name = packageName + '.' + classSimpleName;
    }
    return name;
  }

  private ClassAndVersion createClassAndOrigin(final String className) {
    Collection<ProgramVersion> programVersions = classOrigin.get(className);
    ProgramVersion v = programVersions.toArray(new ProgramVersion[1])[0];
    String cuName = cuNames.column(v).get(className);
    return new ClassAndVersion(className, cuName, v);
  }
}

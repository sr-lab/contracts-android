package contractstudy.inheritance.ProjectVersionHierarchyExtractor;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import contractstudy.inheritance.ProjectVersionHierarchyExtractor.ProjectClassExtractor.ProjectClassExtractor;
import contractstudy.inheritance.ProjectVersionHierarchyExtractor.ProjectClassExtractorKotlin.ProjectClassExtractorKotlin;
import contractstudy.inheritance.model.ClassCoordinates;
import contractstudy.inheritance.model.ClassFinderCreator;
import contractstudy.inheritance.model.ClassParents;
import contractstudy.inheritance.model.InheritanceResolved;
import contractstudy.inheritance.model.SourceClassFinder;
import contractstudy.model.ClassAndVersion;
import contractstudy.model.ProgramVersion;
import contractstudy.utils.InputStreamToStringConversion;
import contractstudy.utils.LanguageUtils;
import contractstudy.utils.kotlinParser.KotlinParser;
import org.jetbrains.kotlin.com.intellij.psi.PsiFile;

import java.io.File;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static contractstudy.utils.LanguageUtils.Language.JAVA;
import static contractstudy.utils.LanguageUtils.Language.KOTLIN;
import static contractstudy.utils.LanguageUtils.getLanguageFromNameExtension;

/**
 * @author Kamil Jezek [kamil.jezek@verifalabs.com]
 */
public class ProjectVersionHierarchyExtractor {

  private final ProjectClassExtractor classExtractor = new ProjectClassExtractor();
  private final ProjectClassExtractorKotlin classExtractorKotlin = new ProjectClassExtractorKotlin();
  private final ClassFinderCreator globalCreator = new ClassFinderCreator();


  public void analyse(
    final ProgramVersion projectVersion,
    final InheritanceResolved notifier) throws Exception {
    ClassFinderCreator creator = new ClassFinderCreator(globalCreator);
    analyse(projectVersion, creator, notifier);
  }

  private void analyse(
    final ProgramVersion projectVersion,
    final ClassFinderCreator creator,
    final InheritanceResolved notifier) throws Exception {
    readClasses(projectVersion, creator, notifier);
    Set<String> parents = new HashSet<>();
    resolveInheritance(projectVersion, creator, notifier, parents);
  }

  private void readClasses(
    final ProgramVersion programVersion,
    final ClassFinderCreator creator,
    final InheritanceResolved notifier) throws Exception {
    File project = programVersion.getFile();
    if (!project.isDirectory()) {
      readClassesForNonDirectoryFile(project, programVersion, creator, notifier);
    } else {
      throw new Exception("Received directory project instead of a file.");
    }
  }

  private void readClassesForNonDirectoryFile(
    final File project,
    final ProgramVersion programVersion,
    final ClassFinderCreator creator,
    final InheritanceResolved notifier
  ) throws Exception {
    try (ZipFile zip = new ZipFile(project)) {
      Enumeration<? extends ZipEntry> en = zip.entries();
      while (en.hasMoreElements()) {
        ZipEntry e = en.nextElement();
        String sourceCodeFileName = e.getName();
        LanguageUtils.Language language = getLanguageFromNameExtension(sourceCodeFileName);
        if (language == KOTLIN || language == JAVA) {
          try (InputStream in = zip.getInputStream(e)) {
            if (language == JAVA) {
              readClassesForJavaInputStream(project, sourceCodeFileName, in, programVersion,
                creator, notifier);
            } else {
              readClassesForKotlinInputStream(project, sourceCodeFileName, in, programVersion,
                creator, notifier);
            }
          }
        }
      }
    }
  }

  private void readClassesForJavaInputStream(
    final File file,
    final String sourceCodeFileName,
    final InputStream inputStream,
    final ProgramVersion programVersion,
    final ClassFinderCreator creator,
    final InheritanceResolved notifier
  ) {
    CompilationUnit cu = StaticJavaParser.parse(inputStream);
    ClassCoordinates classCoordinates = classExtractor.readClass(cu, sourceCodeFileName);
    if (classCoordinates.getClassSimpleName() != null) {
      creator.add(programVersion, classCoordinates, cu, file.getName());
      notifier.notify(classCoordinates);
    }
  }

  private void readClassesForKotlinInputStream(
    final File file,
    final String sourceCodeFileName,
    final InputStream inputStream,
    final ProgramVersion programVersion,
    final ClassFinderCreator creator,
    final InheritanceResolved notifier
  ) throws Exception {
    String src = new InputStreamToStringConversion(inputStream).getResult();
    PsiFile psiFile = new KotlinParser().createKtFile(sourceCodeFileName, src);
    ClassCoordinates classCoordinates = classExtractorKotlin.readClass(psiFile, sourceCodeFileName);
    if (classCoordinates.getClassSimpleName() != null) {
      creator.add(programVersion, classCoordinates, psiFile, file.getName());
      notifier.notify(classCoordinates);
    }
  }

  private void resolveInheritance(
    final ProgramVersion programVersion,
    final ClassFinderCreator creator,
    final InheritanceResolved notifier,
    final Set<String> allParents) {
    SourceClassFinder classFinder = creator.toFinder();
    Map<ClassCoordinates, CompilationUnit> javaUnits = creator.getCus().column(programVersion);
    Map<ClassCoordinates, PsiFile> kotlinUnits = creator.getPsis().column(programVersion);
    resolveInheritanceForJava(javaUnits, classFinder, notifier, allParents);
    resolveInheritanceForKotlin(kotlinUnits, classFinder, notifier, allParents);
  }

  private void resolveInheritanceForJava(
    final Map<ClassCoordinates, CompilationUnit> javaUnits,
    SourceClassFinder classFinder,
    final InheritanceResolved notifier,
    final Set<String> allParents
  ) {
    for (ClassCoordinates classCoordinates : javaUnits.keySet()) {
      CompilationUnit cu = javaUnits.get(classCoordinates);
      ClassParents parents = classExtractor.readInheritance(cu, classCoordinates.getCuName(), classFinder);
      notifyParents(parents, notifier, allParents);
    }
  }

  private void resolveInheritanceForKotlin(
    final Map<ClassCoordinates, PsiFile> kotlinUnits,
    SourceClassFinder classFinder,
    final InheritanceResolved notifier,
    final Set<String> allParents
  ) {
    for (ClassCoordinates classCoordinates : kotlinUnits.keySet()) {
      PsiFile psiFile = kotlinUnits.get(classCoordinates);
      ClassParents parents = classExtractorKotlin.readInheritance(psiFile, classCoordinates.getCuName(), classFinder);
      notifyParents(parents, notifier, allParents);
    }
  }

  private void notifyParents(
    final ClassParents classParents,
    final InheritanceResolved notifier,
    final Set<String> allParents) {

    notifier.notify(classParents);

    String className = classParents.getClassName();
    Set<ClassAndVersion> parents = classParents.getParents(className);
    allParents.addAll(getParentsClassNameList(parents));

    for (ClassCoordinates innerClass : classParents.getInnerClasses()) {
      Set<ClassAndVersion> innerParents = classParents.getParents(innerClass.getClassName());
      allParents.addAll(getParentsClassNameList(innerParents));
    }
  }

  private Set<String> getParentsClassNameList(Set<ClassAndVersion> parents) {
    return parents.stream().map(ClassAndVersion::getClassName).collect(Collectors.toSet());
  }

}

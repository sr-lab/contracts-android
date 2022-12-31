package contractstudy.hierarchy.ProjectVersionHierarchyExtractor;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import contractstudy.hierarchy.ProjectVersionHierarchyExtractor.ProjectClassExtractor.ProjectClassExtractor;
import contractstudy.hierarchy.ProjectVersionHierarchyExtractor.ProjectClassExtractorKotlin.ProjectClassExtractorKotlin;
import contractstudy.hierarchy.model.ClassCoordinates;
import contractstudy.hierarchy.model.ClassFinder;
import contractstudy.hierarchy.model.ClassFinderCreator;
import contractstudy.hierarchy.model.ClassParents;
import contractstudy.hierarchy.model.InheritanceResolved;
import contractstudy.model.ClassAndVersion;
import contractstudy.model.ProgramVersion;
import contractstudy.utils.InputStreamToStringConversion;
import contractstudy.utils.LanguageUtils;
import contractstudy.utils.kotlinParser.KotlinParser;
import org.apache.commons.io.FileUtils;
import org.jetbrains.kotlin.com.intellij.psi.PsiFile;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Collection;
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
    // TODO: Can both situations happen? Or can I delete one of the conditions?
    if (project.isDirectory()) {
      readClassesForDirectoryFile(project, programVersion, creator, notifier);
    } else {
      readClassesForNonDirectoryFile(project, programVersion, creator, notifier);
    }
  }

  private void readClassesForDirectoryFile(
    final File project,
    final ProgramVersion programVersion,
    final ClassFinderCreator creator,
    final InheritanceResolved notifier
  ) {
    Collection<File> javaFiles = FileUtils.listFiles(project, new String[]{"java"}, true);
    Collection<File> kotlinFiles = FileUtils.listFiles(project, new String[]{"kt"}, true);
    readClassesForJavaDirectoryFile(project, javaFiles, programVersion, creator, notifier);
    readClassesForKotlinDirectoryFile(project, kotlinFiles, programVersion, creator, notifier);
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

  private void readClassesForJavaDirectoryFile(
    final File file,
    final Collection<File> sourceCodeFiles,
    final ProgramVersion programVersion,
    final ClassFinderCreator creator,
    final InheritanceResolved notifier
  ) {
    for (File singleFile : sourceCodeFiles) {
      try (InputStream in = Files.newInputStream(singleFile.toPath())) {
        readClassesForJavaInputStream(file, singleFile.getName(), in, programVersion, creator,
          notifier);
      } catch (Exception ignored) {
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
  ) throws Exception {
    CompilationUnit cu = StaticJavaParser.parse(inputStream);
    ClassCoordinates classCoordinates = classExtractor.readClass(cu, sourceCodeFileName);
    if (classCoordinates.getClassSimpleName() != null) {
      creator.add(programVersion, classCoordinates, cu, file.getName());
      notifier.notify(classCoordinates);
    }
  }

  private void readClassesForKotlinDirectoryFile(
    final File file,
    final Collection<File> sourceCodeFiles,
    final ProgramVersion programVersion,
    final ClassFinderCreator creator,
    final InheritanceResolved notifier
  ) {
    for (File singleFile : sourceCodeFiles) {
      try (InputStream in = Files.newInputStream(singleFile.toPath())) {
        readClassesForKotlinInputStream(file, singleFile.getName(), in, programVersion, creator,
          notifier);
      } catch (Exception ignored) {
      }
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
    final Set<String> allParents) throws Exception {
    ClassFinder classFinder = creator.toFinder();
    Map<ClassCoordinates, CompilationUnit> javaUnits = creator.getCus().column(programVersion);
    Map<ClassCoordinates, PsiFile> kotlinUnits = creator.getPsis().column(programVersion);
    resolveInheritanceForJava(javaUnits, classFinder, notifier, allParents);
    resolveInheritanceForKotlin(kotlinUnits, classFinder, notifier, allParents);
  }

  private void resolveInheritanceForJava(
    final Map<ClassCoordinates, CompilationUnit> javaUnits,
    ClassFinder classFinder,
    final InheritanceResolved notifier,
    final Set<String> allParents
  ) throws Exception {
    for (ClassCoordinates classCoordinates : javaUnits.keySet()) {
      CompilationUnit cu = javaUnits.get(classCoordinates);
      ClassParents parents = classExtractor.readInheritance(cu, classCoordinates.getCuName(),
        classFinder);
      notifyParents(parents, notifier, allParents);
    }
  }

  private void resolveInheritanceForKotlin(
    final Map<ClassCoordinates, PsiFile> kotlinUnits,
    ClassFinder classFinder,
    final InheritanceResolved notifier,
    final Set<String> allParents
  ) throws Exception {
    for (ClassCoordinates classCoordinates : kotlinUnits.keySet()) {
      PsiFile psiFile = kotlinUnits.get(classCoordinates);
      ClassParents parents = classExtractorKotlin.readInheritance(psiFile,
        classCoordinates.getCuName(),
        classFinder);
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

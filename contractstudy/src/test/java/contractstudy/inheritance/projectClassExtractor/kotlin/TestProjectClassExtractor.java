package contractstudy.inheritance.projectClassExtractor.kotlin;


import contractstudy.inheritance.ProjectVersionHierarchyExtractor.ProjectClassExtractorKotlin.ProjectClassExtractorKotlin;
import contractstudy.inheritance.model.ClassCoordinates;
import contractstudy.inheritance.model.ClassFinderCreator;
import contractstudy.inheritance.model.ClassParents;
import contractstudy.inheritance.model.SourceClassFinder;
import contractstudy.utils.InputStreamToStringConversion;
import contractstudy.utils.Utils;
import contractstudy.utils.kotlinParser.KotlinParser;
import org.jetbrains.kotlin.com.intellij.psi.PsiFile;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.File;
import java.io.InputStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class TestProjectClassExtractor {

  private static final File TEST_DATA_FOLDER = new File(
    Utils.getBasePathTestFolder() + "inheritance/projectClassExtractor/kotlin/testData");
  private final ClassFinderCreator globalCreator = new ClassFinderCreator();

  private static Stream<Arguments> generateFilesReadClass() {
    return Stream.of(
      Arguments.of("SubClass1.kt", 2, 1));
  }

  private static Stream<Arguments> generateFilesReadInheritance() {
    return Stream.of(
      Arguments.of("SubClass1.kt", 2, 1));
  }

  @ParameterizedTest
  @MethodSource("generateFilesReadClass")
  public void testReadClass(String fileName, int expectedMethods, int expectedConstructors)
    throws Exception {
    File file = new File(TEST_DATA_FOLDER, fileName);
    InputStream in = Utils.getInputStream(file);
    String src = new InputStreamToStringConversion(in).getResult();
    PsiFile psiFile = new KotlinParser().createKtFile("test", src);
    ProjectClassExtractorKotlin extractor = new ProjectClassExtractorKotlin();

    ClassCoordinates result = extractor.readClass(psiFile, psiFile.getName());

    assertNotNull(result);
  }

  @ParameterizedTest
  @MethodSource("generateFilesReadInheritance")
  public void testReadInheritance(String fileName, int expectedMethods, int expectedConstructors)
    throws Exception {
    File file = new File(TEST_DATA_FOLDER, fileName);
    InputStream in = Utils.getInputStream(file);
    String src = new InputStreamToStringConversion(in).getResult();
    PsiFile psiFile = new KotlinParser().createKtFile("test", src);
    ProjectClassExtractorKotlin extractor = new ProjectClassExtractorKotlin();
    ClassFinderCreator creator = new ClassFinderCreator(globalCreator);
    SourceClassFinder classFinder = creator.toFinder();

    ClassParents result = extractor.readInheritance(psiFile, psiFile.getName(), classFinder);

    assertNotNull(result);
  }
}

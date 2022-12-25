package contractstudy.hierarchy.projectClassExtractor.kotlin;


import contractstudy.hierarchy.model.ClassCoordinates;
import contractstudy.hierarchy.ProjectVersionHierarchyExtractor.ProjectClassExtractorKotlin.ProjectClassExtractorKotlin;
import contractstudy.hierarchy.model.ClassFinder;
import contractstudy.hierarchy.model.ClassFinderCreator;
import contractstudy.hierarchy.model.ClassParents;
import contractstudy.utils.kotlinParser.KotlinParser;
import contractstudy.utils.InputStreamToStringConversion;
import contractstudy.utils.Utils;
import org.jetbrains.kotlin.com.intellij.psi.PsiFile;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.File;
import java.io.InputStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class TestProjectClassExtractor {

  private static final File TEST_DATA_FOLDER = new File(
    Utils.getBasePathTestFolder() + "hierarchy/projectClassExtractor/kotlin/testData");
  private final ClassFinderCreator globalCreator = new ClassFinderCreator();

  private static Stream<Arguments> generateFilesReadClass() {
    return Stream.of(
      Arguments.of("SubClass1.kt", 2, 1));
  }

  @ParameterizedTest
  @MethodSource("generateFilesReadClass")
  public void testReadClass(String fileName, int expectedMethods, int expectedConstructors) throws Exception {
    File file = new File(TEST_DATA_FOLDER, fileName);
    InputStream in = Utils.getInputStream(file);
    String src = new InputStreamToStringConversion(in).getResult();
    PsiFile psiFile = new KotlinParser().createKtFile("test", src);
    ProjectClassExtractorKotlin extractor = new ProjectClassExtractorKotlin();

    ClassCoordinates result = extractor.readClass(psiFile, psiFile.getName());

    assertNotNull(result);
  }

  private static Stream<Arguments> generateFilesReadInheritance() {
    return Stream.of(
      Arguments.of("SubClass1.kt", 2, 1));
  }

  @ParameterizedTest
  @MethodSource("generateFilesReadInheritance")
  public void testReadInheritance(String fileName, int expectedMethods, int expectedConstructors) throws Exception {
    File file = new File(TEST_DATA_FOLDER, fileName);
    InputStream in = Utils.getInputStream(file);
    String src = new InputStreamToStringConversion(in).getResult();
    PsiFile psiFile = new KotlinParser().createKtFile("test", src);
    ProjectClassExtractorKotlin extractor = new ProjectClassExtractorKotlin();
    ClassFinderCreator creator = new ClassFinderCreator(globalCreator);
    ClassFinder classFinder = creator.toFinder();

    ClassParents result = extractor.readInheritance(psiFile, psiFile.getName(), classFinder);

    assertNotNull(result);
  }
}

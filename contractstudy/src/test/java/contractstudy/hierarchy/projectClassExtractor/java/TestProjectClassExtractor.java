package contractstudy.hierarchy.projectClassExtractor.java;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import contractstudy.hierarchy.ProjectVersionHierarchyExtractor.ProjectClassExtractor.ProjectClassExtractor;
import contractstudy.hierarchy.model.ClassCoordinates;
import contractstudy.hierarchy.model.ClassFinder;
import contractstudy.hierarchy.model.ClassFinderCreator;
import contractstudy.hierarchy.model.ClassParents;
import contractstudy.utils.Utils;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.File;
import java.io.InputStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class TestProjectClassExtractor {

  private static final File TEST_DATA_FOLDER = new File(
    Utils.getBasePathTestFolder() + "hierarchy/projectClassExtractor/java/testData");
  private final ClassFinderCreator globalCreator = new ClassFinderCreator();

  private static Stream<Arguments> generateFilesReadClass() {
    return Stream.of(
      Arguments.of("SubClass1.java"));
  }

  private static Stream<Arguments> generateFilesInheritance() {
    return Stream.of(
      Arguments.of("SubClass1.java"));
  }

  @ParameterizedTest
  @MethodSource("generateFilesReadClass")
  public void testReadClass(String fileName, String[] innerClassState, int[] innerClassMethodsCount,
    int[] innerClassParentsCount) throws Exception {
    File file = new File(TEST_DATA_FOLDER, fileName);
    InputStream in = Utils.getInputStream(file);
    CompilationUnit cu = StaticJavaParser.parse(in);
    ProjectClassExtractor extractor = new ProjectClassExtractor();

    ClassCoordinates result = extractor.readClass(cu, "name");

    assertNotNull(result);
  }

  @ParameterizedTest
  @MethodSource("generateFilesInheritance")
  public void readInheritance(String fileName) throws Exception {
    File file = new File(TEST_DATA_FOLDER, fileName);
    InputStream in = Utils.getInputStream(file);
    CompilationUnit cu = StaticJavaParser.parse(in);
    ProjectClassExtractor extractor = new ProjectClassExtractor();
    ClassFinderCreator creator = new ClassFinderCreator(globalCreator);
    ClassFinder classFinder = creator.toFinder();

    ClassParents result = extractor.readInheritance(cu, "name", classFinder);

    assertNotNull(result);
  }
}
package contractstudy.collectContracts.common.StaticImportCollector.java;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import contractstudy.usage.collectContracts.common.StaticImportCollector.StaticImportCollector;
import contractstudy.usage.collectContracts.common.StaticImportCollector.constants.StaticImportState;
import contractstudy.utils.Utils;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.File;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestStaticImportCollector {

  private static final File TEST_DATA_FOLDER = new File(
    Utils.getBasePathTestFolder() + "collectContracts/common/StaticImportCollector/java/testData");

  private static Stream<Arguments> generateParameters() {
    return Stream.of(
      Arguments.of(StaticImportState.NONE, "StaticImportNone.java", "none", "none"),
      Arguments.of(StaticImportState.CLASS, "StaticImportClass.java", "java.util.List",
        "java.util.List"),
      Arguments.of(StaticImportState.SOME_STATIC, "StaticImportAllStatic.java", "java.lang.Math",
        "java.lang.Math"),
      Arguments.of(StaticImportState.SOME_STATIC, "StaticImportAllStatic.java", "java.lang.System",
        "java.lang.System"),
      Arguments.of(StaticImportState.ALL_STATIC, "StaticImportWildCard.java",
        "java.lang.System", "java.lang.System"));
  }

  @ParameterizedTest
  @MethodSource("generateParameters")
  public void testStaticImportCollector_withDifferentFiles_expectDifferentStates(
    StaticImportState staticImportState,
    String fileName,
    String annotationPackageName,
    String targetQClassName
  ) throws Exception {
    //given
    File file = new File(TEST_DATA_FOLDER, fileName);
    CompilationUnit cu = StaticJavaParser.parse(Utils.getInputStream(file));

    //when
    StaticImportCollector importsCollector = new StaticImportCollector(annotationPackageName,
      targetQClassName);
    importsCollector.visit(cu, null);
    StaticImportState result = importsCollector.getStaticImportState();

    //assert
    assertEquals(staticImportState, result);
  }

}

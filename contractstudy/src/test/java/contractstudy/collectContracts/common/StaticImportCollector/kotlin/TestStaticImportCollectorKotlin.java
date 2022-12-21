package contractstudy.collectContracts.common.StaticImportCollector.kotlin;

import contractstudy.collectContracts.common.StaticImportCollector.StaticImportCollectorKotlin;
import contractstudy.collectContracts.common.StaticImportCollector.constants.StaticImportState;
import contractstudy.kotlinParser.KotlinParser;
import contractstudy.utils.InputStreamToStringConversion;
import org.jetbrains.kotlin.com.intellij.psi.PsiFile;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import contractstudy.utils.Utils;

import java.io.File;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestStaticImportCollectorKotlin {

  private static final File TEST_DATA_FOLDER = new File(
    Utils.getBasePathTestFolder()
      + "collectContracts/common/StaticImportCollector/kotlin/testData");

  private static Stream<Arguments> generateParameters() {
    return Stream.of(
      Arguments.of(StaticImportState.NONE, "StaticImportNone.kt", "none", "none"),
      Arguments.of(StaticImportState.CLASS, "StaticImportClass.kt", "java.util",
        "java.util.HashMap"),
      Arguments.of(StaticImportState.ALL_STATIC, "StaticImportAllStatic.kt", "java.lang.Math",
        "java.lang.Math.PI"),
      Arguments.of(StaticImportState.ALL_STATIC, "StaticImportAllStatic.kt", "java.lang.System",
        "java.lang.System"));
  }

  @ParameterizedTest
  @MethodSource("generateParameters")
  public void testStaticImportCollectorKotlin_withDifferentFiles_expectDifferentStates(
    StaticImportState staticImportState, String fileName, String annotationPackageName,
    String targetQClassName) throws Exception {
    //given
    File file = new File(TEST_DATA_FOLDER, fileName);
    String src = new InputStreamToStringConversion(Utils.getInputStream(file)).getResult();
    PsiFile psiFile = new KotlinParser().createKtFile("test", src);

    //when
    StaticImportCollectorKotlin importsCollector = new StaticImportCollectorKotlin(
      annotationPackageName, targetQClassName);
    psiFile.accept(importsCollector);
    StaticImportState result = importsCollector.getStaticImportState();

    //assert
    assertEquals(staticImportState, result);
  }

}

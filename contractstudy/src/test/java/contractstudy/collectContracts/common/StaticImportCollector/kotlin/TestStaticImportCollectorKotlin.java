package contractstudy.collectContracts.common.StaticImportCollector.kotlin;

import contractstudy.usage.collectContracts.common.StaticImportCollector.StaticImportCollectorKotlin;
import contractstudy.usage.collectContracts.common.StaticImportCollector.constants.StaticImportState;
import contractstudy.utils.InputStreamToStringConversion;
import contractstudy.utils.Utils;
import contractstudy.utils.kotlinParser.KotlinParser;
import org.jetbrains.kotlin.com.intellij.psi.PsiFile;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.File;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestStaticImportCollectorKotlin {

  private static final File TEST_DATA_FOLDER = new File(
    Utils.getBasePathTestFolder() + "collectContracts/common/StaticImportCollector/kotlin/testData");

  private static Stream<Arguments> generateParameters() {
    return Stream.of(
      Arguments.of(StaticImportState.NONE, "StaticImportNone.kt", "none", "none"),
      Arguments.of(StaticImportState.CLASS, "StaticImportClass.kt", "java.util", "java.util.List"),
      Arguments.of(StaticImportState.CLASS, "StaticImportClass.kt", "com.google.common.base",
        "com.google.common.base.Preconditions"),
      Arguments.of(StaticImportState.ALL_STATIC, "StaticImportWildCard.kt", "java.lang.System",
        "java.lang.System"));
  }

  @ParameterizedTest
  @MethodSource("generateParameters")
  public void testStaticImportCollectorKotlin_withDifferentFiles_expectDifferentStates(
    StaticImportState staticImportState, String fileName, String annotationPackageName,
    String targetQClassName) throws Exception {

    File file = new File(TEST_DATA_FOLDER, fileName);
    String src = new InputStreamToStringConversion(Utils.getInputStream(file)).getResult();
    PsiFile psiFile = new KotlinParser().createKtFile("test", src);

    StaticImportCollectorKotlin importsCollector = new StaticImportCollectorKotlin(
      annotationPackageName, targetQClassName);
    psiFile.accept(importsCollector);
    StaticImportState result = importsCollector.getStaticImportState();

    assertEquals(staticImportState, result);
  }

}

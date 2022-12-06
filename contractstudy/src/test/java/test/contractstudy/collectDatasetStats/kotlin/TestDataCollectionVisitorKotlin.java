package test.contractstudy.collectDatasetStats.kotlin;

import contractstudy.collectDatasetStats.visitor.DataCollectionVisitorKotlin;
import contractstudy.kotlinParser.KotlinParser;
import contractstudy.utils.InputStreamToStringConversion;
import org.jetbrains.kotlin.com.intellij.psi.PsiFile;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import test.contractstudy.utils.Utils;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class TestDataCollectionVisitorKotlin {


  private static final File TEST_DATA_FOLDER = new File(
    Utils.getBasePathTestFolder() + "collectDatasetStats/kotlin/testData");

  private static Stream<Arguments> generateFiles() {
    return Stream.of(
      Arguments.of("DummyKotlinFile.kt"));
  }

  @ParameterizedTest
  @MethodSource("generateFiles")
  public void testDataCollectionVisitorKotlin(String fileName) throws Exception {
    //given
    File file = new File(TEST_DATA_FOLDER, fileName);
    String src = new InputStreamToStringConversion(Utils.getInputStream(file)).getResult();
    PsiFile psiFile = new KotlinParser().createKtFile("cuName", src);

    Map<String, Integer> data = new HashMap<>();
    DataCollectionVisitorKotlin visitor = new DataCollectionVisitorKotlin(data);
    psiFile.accept(visitor);

    assertNotEquals(0, data.size());
  }

}

package contractstudy.collectDatasetStats.java;

import contractstudy.constants.SetStatsDataKeys;
import contractstudy.usage.collectDatasetStats.DataCollectionExtractor;
import contractstudy.utils.Utils;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class TestDataCollectionExtractor {

  private static final File TEST_DATA_FOLDER = new File(
    Utils.getBasePathTestFolder() + "collectDatasetStats/java/testData");

  private static Stream<Arguments> generateFiles() {
    return Stream.of(
      Arguments.of("DummyJavaFile.java", 2, 2, 1, 5, 4, 2, 31));
  }

  @ParameterizedTest
  @MethodSource("generateFiles")
  public void testDataCollectionVisitorExtractor(String fileName, Integer constructors,
    Integer constructorsPublic, Integer cu, Integer methods, Integer methodsPublic, Integer classes,
    Integer loc) throws Exception {
    //given
    File file = new File(TEST_DATA_FOLDER, fileName);
    Map<String, Integer> dataJava = new HashMap<>();
    Map<String, Integer> dataKotlin = new HashMap<>();
    DataCollectionExtractor extractor = new DataCollectionExtractor();
    //when
    extractor.analyse(file.getName(), Utils.getInputStream(file), dataJava, dataKotlin);
    //assert
    assertNotEquals(0, dataJava.size());
    assertEquals(constructors, dataJava.get(SetStatsDataKeys.ALL_CONSTRUCTORS.getKey()));
    assertEquals(constructorsPublic, dataJava.get(SetStatsDataKeys.PUBLIC_CONSTRUCTORS.getKey()));
    assertEquals(cu, dataJava.get(SetStatsDataKeys.COMPILATION_UNITS.getKey()));
    assertEquals(methods, dataJava.get(SetStatsDataKeys.ALL_METHODS.getKey()));
    assertEquals(methodsPublic, dataJava.get(SetStatsDataKeys.PUBLIC_METHODS.getKey()));
    assertEquals(classes, dataJava.get(SetStatsDataKeys.CLASSES.getKey()));
    assertEquals(loc, dataJava.get(SetStatsDataKeys.LOC.getKey()));
  }
}

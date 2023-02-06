package contractstudy.collectDatasetStats.kotlin;

import contractstudy.usage.collectDatasetStats.DataCollectionExtractor;
import contractstudy.constants.SetStatsDataKeys;
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
    Utils.getBasePathTestFolder() + "collectDatasetStats/kotlin/testData");

  private static Stream<Arguments> generateFiles() {
    return Stream.of(
      Arguments.of("DummyKotlinFile.kt", 2, 2, 1, 5, 4, 2, 28));
  }

  @ParameterizedTest
  @MethodSource("generateFiles")
  public void testDataCollectionVisitorExtractor(String fileName, Integer constructors,
    Integer constructorsPublic, Integer cu, Integer methods, Integer methodsPublic, Integer classes,
    Integer loc) throws Exception {
    //given
    File file = new File(TEST_DATA_FOLDER, fileName);
    Map<String, Integer> data = new HashMap<>();
    DataCollectionExtractor extractor = new DataCollectionExtractor();
    //when
    extractor.analyse(file.getName(), Utils.getInputStream(file), data);
    //assert
    assertNotEquals(0, data.size());
    assertEquals(constructors, data.get(SetStatsDataKeys.ALL_CONSTRUCTORS.getKey()));
    assertEquals(constructorsPublic, data.get(SetStatsDataKeys.PUBLIC_CONSTRUCTORS.getKey()));
    assertEquals(cu, data.get(SetStatsDataKeys.COMPILATION_UNITS.getKey()));
    assertEquals(methods, data.get(SetStatsDataKeys.ALL_METHODS.getKey()));
    assertEquals(methodsPublic, data.get(SetStatsDataKeys.PUBLIC_METHODS.getKey()));
    assertEquals(classes, data.get(SetStatsDataKeys.CLASSES.getKey()));
    assertEquals(loc, data.get(SetStatsDataKeys.LOC.getKey()));
  }

}

package contractstudy.collectContracts.annotation.kotlin;

import contractstudy.constants.constraint.ConstraintCollector;
import contractstudy.constants.constraint.ConstraintType;
import contractstudy.constants.constraint.ContractElement;
import contractstudy.usage.collectContracts.annotation.JSR303Extractor;
import contractstudy.utils.Utils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class TestAbstractAnnotationExtractorKotlin {

  private static final File TEST_DATA_FOLDER = new File(
    Utils.getBasePathTestFolder() + "collectContracts/annotation/kotlin/testData");

  private static Stream<Arguments> generatorJSR305Extractor() {
    return Stream.of(
      Arguments.of(ConstraintType.JSR303Min, "AnnotationsMultiple.kt", 1),
      Arguments.of(ConstraintType.JSR303Max, "AnnotationsMultiple.kt", 1),
      Arguments.of(ConstraintType.JSR303NotNull, "AnnotationsMultiple.kt", 2),
      Arguments.of(ConstraintType.JSR303Size, "AnnotationsMultiple.kt", 1));
  }

  private static Stream<Arguments> generatorJSR305ExtractorWithWildCardImport() {
    return Stream.of(
      Arguments.of(ConstraintType.JSR303Min, "AnnotationsWildCard.kt", 1),
      Arguments.of(ConstraintType.JSR303Max, "AnnotationsWildCard.kt", 1),
      Arguments.of(ConstraintType.JSR303NotNull, "AnnotationsWildCard.kt", 2),
      Arguments.of(ConstraintType.JSR303Size, "AnnotationsWildCard.kt", 1));
  }

  @ParameterizedTest
  @MethodSource("generatorJSR305Extractor")
  public void testJSR305Extractor_whenAnnotationExists_expectListOfAnnotations(
    ConstraintType constraintType, String fileName, int count) throws Exception {

    //given
    File file = new File(TEST_DATA_FOLDER, fileName);
    ConstraintCollector collector = new ConstraintCollector();
    JSR303Extractor jSR303Extractor = new JSR303Extractor();

    //when
    jSR303Extractor.analyse(Utils.getInputStream(file), "test", "<no version>", file.getName(),
      collector);

    //assert
    List<ContractElement> contractsFound = collector
      .getContractElements()
      .stream()
      .filter(c -> c.getKind().equals(constraintType))
      .collect(Collectors.toList());

    assertNotNull(contractsFound);
    assertEquals(count, contractsFound.size());
  }

  @ParameterizedTest
  @MethodSource("generatorJSR305ExtractorWithWildCardImport")
  public void testJSR305Extractor_whenAnnotationExistsWithWildCardImport_expectListOfAnnotations(
    ConstraintType constraintType, String fileName, int count) throws Exception {

    //given
    File file = new File(TEST_DATA_FOLDER, fileName);
    ConstraintCollector collector = new ConstraintCollector();
    JSR303Extractor jSR303Extractor = new JSR303Extractor();

    //when
    jSR303Extractor.analyse(Utils.getInputStream(file), "test", "<no version>", file.getName(),
      collector);

    //assert
    List<ContractElement> contractsFound = collector
      .getContractElements()
      .stream()
      .filter(c -> c.getKind().equals(constraintType))
      .collect(Collectors.toList());

    assertNotNull(contractsFound);
    assertNotEquals(0, contractsFound.size());
    assertEquals(count, contractsFound.size());
  }

  @Test
  public void testJSR305Extractor_whenAnnotationNotExists_expectEmptyList() throws Exception {

    //given
    File file = new File(TEST_DATA_FOLDER, "AnnotationsMultiple.kt");
    ConstraintCollector collector = new ConstraintCollector();
    JSR303Extractor jSR303Extractor = new JSR303Extractor();

    //when
    jSR303Extractor.analyse(Utils.getInputStream(file), "test", "<no version>", file.getName(),
      collector);

    //assert
    List<ContractElement> contractsFound = collector
      .getContractElements()
      .stream()
      .filter(c -> c.getKind().equals(ConstraintType.JSR303Null))
      .collect(Collectors.toList());

    assertNotNull(contractsFound);
    assertEquals(0, contractsFound.size());
  }

}

package test.contractstudy.extractor.api.SpringAssertExtractor.kotlin;

import contractstudy.collectContracts.api.SpringAssert.SpringAssertExtractor;
import contractstudy.constants.constraint.ConstraintCollector;
import contractstudy.constants.constraint.ConstraintType;
import contractstudy.constants.constraint.ContractElement;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import test.contractstudy.utils.Utils;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class TestSpringAssertExtractor {

  private static final File TEST_DATA_FOLDER = new File(
    Utils.getBasePathTestFolder() + "extractor/api/SpringAssertExtractor/kotlin/testData");

  private static Stream<Arguments> generateSpringAsserts() {
    return Stream.of(
      Arguments.of(ConstraintType.SpringAssertDoesNotContain, "MultipleSpringAssert.kt", 1),
      Arguments.of(ConstraintType.SpringAssertHasLength, "MultipleSpringAssert.kt", 1),
      Arguments.of(ConstraintType.SpringAssertNotNull, "MultipleSpringAssert.kt", 2),
      Arguments.of(ConstraintType.SpringAssertIsTrue, "MultipleSpringAssert.kt", 1),
      Arguments.of(ConstraintType.SpringAssertState, "MultipleSpringAssert.kt", 1),
      Arguments.of(ConstraintType.SpringAssertNotNull, "MultipleSpringAssert.kt", 2));
  }

  @ParameterizedTest
  @MethodSource("generateSpringAsserts")
  public void testSpringAssertExtractor_whenMultipleUses_expectListOfUses(
    ConstraintType constraintType, String fileName, int count) throws Exception {
    //given
    File file = new File(TEST_DATA_FOLDER, fileName);
    ConstraintCollector collector = new ConstraintCollector();
    SpringAssertExtractor springAssertExtractor = new SpringAssertExtractor();

    //when
    springAssertExtractor.analyse(Utils.getInputStream(file), "test", "<no version>",
      file.getName(), collector);

    //assert
    List<ContractElement> contractsFound = collector
      .getContractElements()
      .stream()
      .filter(c -> c.getKind().equals(constraintType))
      .collect(Collectors.toList());

    assertNotNull(contractsFound);
    assertEquals(count, contractsFound.size());
  }
}

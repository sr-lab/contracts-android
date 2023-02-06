package contractstudy.collectContracts.api.SpringAssertExtractor.java;

import contractstudy.usage.collectContracts.api.SpringAssert.SpringAssertExtractor;
import contractstudy.constants.constraint.ConstraintCollector;
import contractstudy.constants.constraint.ConstraintType;
import contractstudy.constants.constraint.ContractElement;
import contractstudy.utils.Utils;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class TestSpringAssertExtractor {

  private static final File TEST_DATA_FOLDER = new File(
    Utils.getBasePathTestFolder() + "collectContracts/api/SpringAssertExtractor/java/testData");

  private static Stream<Arguments> generateSpringAsserts() {
    return Stream.of(
      Arguments.of(ConstraintType.SpringAssertDoesNotContain, "MultipleSpringAssert.java", 1),
      Arguments.of(ConstraintType.SpringAssertHasLength, "MultipleSpringAssert.java", 1),
      Arguments.of(ConstraintType.SpringAssertNotNull, "MultipleSpringAssert.java", 2),
      Arguments.of(ConstraintType.SpringAssertIsTrue, "MultipleSpringAssert.java", 1),
      Arguments.of(ConstraintType.SpringAssertState, "MultipleSpringAssert.java", 1),
      Arguments.of(ConstraintType.SpringAssertNotNull, "MultipleSpringAssert.java", 2));
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

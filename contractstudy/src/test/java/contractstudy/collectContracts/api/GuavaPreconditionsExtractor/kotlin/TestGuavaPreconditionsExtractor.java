package contractstudy.collectContracts.api.GuavaPreconditionsExtractor.kotlin;

import contractstudy.collectContracts.api.Guava.GuavaPreconditionsExtractor;
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

public class TestGuavaPreconditionsExtractor {

  private static final File TEST_DATA_FOLDER = new File(
    Utils.getBasePathTestFolder()
      + "collectContracts/api/GuavaPreconditionsExtractor/kotlin/testData");

  private static Stream<Arguments> generateMultipleGuavaPreconditions() {
    return Stream.of(
      Arguments.of(ConstraintType.GuavaPreconditionCheckArgument, "MultipleGuava.kt", 1),
      Arguments.of(ConstraintType.GuavaPreconditionCheckState, "MultipleGuava.kt", 2),
      Arguments.of(ConstraintType.GuavaPreconditionElementIndex, "MultipleGuava.kt", 1),
      Arguments.of(ConstraintType.GuavaPreconditionNotNull, "MultipleGuava.kt", 1),
      Arguments.of(ConstraintType.GuavaPreconditionPositionIndex, "MultipleGuava.kt", 1),
      Arguments.of(ConstraintType.GuavaPreconditionPositionIndexes, "MultipleGuava.kt", 1));
  }

  private static Stream<Arguments> generateWildCardImportGuavaPreconditions() {
    return Stream.of(
      Arguments.of(ConstraintType.GuavaPreconditionCheckArgument,
        "MultipleGuavaImportWithWildCard.kt", 1),
      Arguments.of(ConstraintType.GuavaPreconditionCheckState, "MultipleGuavaImportWithWildCard.kt",
        2),
      Arguments.of(ConstraintType.GuavaPreconditionElementIndex,
        "MultipleGuavaImportWithWildCard.kt", 1),
      Arguments.of(ConstraintType.GuavaPreconditionNotNull, "MultipleGuavaImportWithWildCard.kt",
        1),
      Arguments.of(ConstraintType.GuavaPreconditionPositionIndex,
        "MultipleGuavaImportWithWildCard.kt", 1),
      Arguments.of(ConstraintType.GuavaPreconditionPositionIndexes,
        "MultipleGuavaImportWithWildCard.kt", 1));
  }

  @ParameterizedTest
  @MethodSource("generateMultipleGuavaPreconditions")
  public void testGuavaPreconditionsExtractor_whenMultipleUses_expectListOfUses(
    ConstraintType constraintType, String fileName, int count) throws Exception {
    //given
    File file = new File(TEST_DATA_FOLDER, fileName);
    ConstraintCollector collector = new ConstraintCollector();
    GuavaPreconditionsExtractor guavaPreconditionsExtractor = new GuavaPreconditionsExtractor();

    //when
    guavaPreconditionsExtractor.analyse(Utils.getInputStream(file), "test", "<no version>",
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

  @ParameterizedTest
  @MethodSource("generateWildCardImportGuavaPreconditions")
  public void testGuavaPreconditionsExtractor_whenMultipleUsesWildCardImport_expectListOfUses(
    ConstraintType constraintType, String fileName, int count) throws Exception {
    //given
    File file = new File(TEST_DATA_FOLDER, fileName);
    ConstraintCollector collector = new ConstraintCollector();
    GuavaPreconditionsExtractor guavaPreconditionsExtractor = new GuavaPreconditionsExtractor();

    //when
    guavaPreconditionsExtractor.analyse(Utils.getInputStream(file), "test", "<no version>",
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

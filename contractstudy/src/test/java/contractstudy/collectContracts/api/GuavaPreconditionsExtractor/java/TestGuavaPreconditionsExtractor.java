package contractstudy.collectContracts.api.GuavaPreconditionsExtractor.java;

import contractstudy.usage.collectContracts.api.Guava.GuavaPreconditionsExtractor;
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
      + "collectContracts/api/GuavaPreconditionsExtractor/java/testData");

  private static Stream<Arguments> generateMultipleGuavaPreconditions() {
    return Stream.of(
      Arguments.of(ConstraintType.GuavaPreconditionCheckArgument, "MultipleGuava.java", 1),
      Arguments.of(ConstraintType.GuavaPreconditionCheckState, "MultipleGuava.java", 2),
      Arguments.of(ConstraintType.GuavaPreconditionElementIndex, "MultipleGuava.java", 1),
      Arguments.of(ConstraintType.GuavaPreconditionNotNull, "MultipleGuava.java", 1),
      Arguments.of(ConstraintType.GuavaPreconditionPositionIndex, "MultipleGuava.java", 1),
      Arguments.of(ConstraintType.GuavaPreconditionPositionIndexes, "MultipleGuava.java", 1));
  }

  private static Stream<Arguments> generateStaticImportGuavaPreconditions() {
    return Stream.of(
      Arguments.of(ConstraintType.GuavaPreconditionCheckArgument, "MultipleGuavaStaticImport.java",
        1),
      Arguments.of(ConstraintType.GuavaPreconditionCheckState, "MultipleGuavaStaticImport.java", 2),
      Arguments.of(ConstraintType.GuavaPreconditionElementIndex, "MultipleGuavaStaticImport.java",
        1),
      Arguments.of(ConstraintType.GuavaPreconditionNotNull, "MultipleGuavaStaticImport.java", 1),
      Arguments.of(ConstraintType.GuavaPreconditionPositionIndex, "MultipleGuavaStaticImport.java",
        1),
      Arguments.of(ConstraintType.GuavaPreconditionPositionIndexes,
        "MultipleGuavaStaticImport.java", 1));
  }

  private static Stream<Arguments> generateStaticImportWildCardGuavaPreconditions() {
    return Stream.of(
      Arguments.of(ConstraintType.GuavaPreconditionCheckArgument,
        "MultipleGuavaStaticImportWithWildCard.java", 1),
      Arguments.of(ConstraintType.GuavaPreconditionCheckState,
        "MultipleGuavaStaticImportWithWildCard.java", 2),
      Arguments.of(ConstraintType.GuavaPreconditionElementIndex,
        "MultipleGuavaStaticImportWithWildCard.java", 1),
      Arguments.of(ConstraintType.GuavaPreconditionNotNull,
        "MultipleGuavaStaticImportWithWildCard.java", 1),
      Arguments.of(ConstraintType.GuavaPreconditionPositionIndex,
        "MultipleGuavaStaticImportWithWildCard.java", 1),
      Arguments.of(ConstraintType.GuavaPreconditionPositionIndexes,
        "MultipleGuavaStaticImportWithWildCard.java", 1));
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
  @MethodSource("generateStaticImportGuavaPreconditions")
  public void testGuavaPreconditionsExtractor_whenMultipleUsesWithStaticImport_expectListOfUses(
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
  @MethodSource("generateStaticImportWildCardGuavaPreconditions")
  public void testGuavaPreconditionsExtractor_whenMultipleUsesWithStaticImportWithWildCard_expectListOfUses(
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

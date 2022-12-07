package test.contractstudy.collectContracts.api.CommonsValidateExtractor.CommonsValidate2Extractor.kotlin;

import contractstudy.collectContracts.api.CommonsValidate.CommonsValidate2.CommonsValidate2Extractor;
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

public class TestCommonsValidate2EnumExtractor {

  private static final File TEST_DATA_FOLDER = new File(Utils.getBasePathTestFolder()
    + "collectContracts/api/CommonsValidateExtractor/CommonsValidate2Extractor/kotlin/testData");

  private static Stream<Arguments> generatorCommonsValidate2() {
    return Stream.of(
      Arguments.of(ConstraintType.CommonsLangNotNull, "MultipleCommonsValidate2.kt", 2),
      Arguments.of(ConstraintType.CommonsLangNotEmpty, "MultipleCommonsValidate2.kt", 1),
      Arguments.of(ConstraintType.CommonsLangIsTrue, "MultipleCommonsValidate2.kt", 1),
      Arguments.of(ConstraintType.CommonsLang2AllElementsOfType, "MultipleCommonsValidate2.kt", 3));
  }

  private static Stream<Arguments> generatorCommonsValidate2WithStaticImport() {
    return Stream.of(
      Arguments.of(ConstraintType.CommonsLangNotNull, "MultipleCommonsValidate2WildCardImport.kt",
        1),
      Arguments.of(ConstraintType.CommonsLangNotEmpty, "MultipleCommonsValidate2WildCardImport.kt",
        1),
      Arguments.of(ConstraintType.CommonsLangIsTrue, "MultipleCommonsValidate2WildCardImport.kt",
        1));
  }

  @ParameterizedTest
  @MethodSource("generatorCommonsValidate2")
  public void testCommonsValidate2Extractor_whenMultipleUses_expectListOfUses(
    ConstraintType constraintType, String fileName, int count) throws Exception {
    //given
    File file = new File(TEST_DATA_FOLDER, fileName);
    ConstraintCollector collector = new ConstraintCollector();
    CommonsValidate2Extractor commonsValidate2Extractor = new CommonsValidate2Extractor();

    //when
    commonsValidate2Extractor.analyse(Utils.getInputStream(file), "test", "<no version>",
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
  @MethodSource("generatorCommonsValidate2WithStaticImport")
  public void testCommonsValidate2Extractor_whenMultipleUsesWithWildCardImport_expectListOfUses(
    ConstraintType constraintType, String fileName, int count) throws Exception {
    //given
    File file = new File(TEST_DATA_FOLDER, fileName);
    ConstraintCollector collector = new ConstraintCollector();
    CommonsValidate2Extractor commonsValidate2Extractor = new CommonsValidate2Extractor();

    //when
    commonsValidate2Extractor.analyse(Utils.getInputStream(file), "test", "<no version>",
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

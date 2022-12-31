package contractstudy.collectContracts.api.CommonsValidateExtractor.CommonsValidate3Extractor.kotlin;

import contractstudy.collectContracts.api.CommonsValidate.CommonsValidate3.CommonsValidate3Extractor;
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

public class TestCommonsValidate3EnumExtractor {

  private static final File TEST_DATA_FOLDER = new File(Utils.getBasePathTestFolder()
    + "collectContracts/api/CommonsValidateExtractor/CommonsValidate3Extractor/kotlin/testData");

  private static Stream<Arguments> generatorCommonsValidate3() {
    return Stream.of(
      Arguments.of(ConstraintType.CommonsLangNotNull, "MultipleCommonsValidate3.kt", 2),
      Arguments.of(ConstraintType.CommonsLangNotEmpty, "MultipleCommonsValidate3.kt", 1),
      Arguments.of(ConstraintType.CommonsLang3ExclusiveBetween, "MultipleCommonsValidate3.kt", 1),
      Arguments.of(ConstraintType.CommonsLangIsTrue, "MultipleCommonsValidate3.kt", 1),
      Arguments.of(ConstraintType.CommonsLang3MatchesPattern, "MultipleCommonsValidate3.kt", 1),
      Arguments.of(ConstraintType.CommonsLang3NotBlank, "MultipleCommonsValidate3.kt", 1));
  }

  private static Stream<Arguments> generatorCommonsValidate3WildCardImport() {
    return Stream.of(
      Arguments.of(ConstraintType.CommonsLangNotNull, "MultipleCommonsValidate3.kt", 2),
      Arguments.of(ConstraintType.CommonsLangNotEmpty, "MultipleCommonsValidate3.kt", 1),
      Arguments.of(ConstraintType.CommonsLang3ExclusiveBetween, "MultipleCommonsValidate3.kt", 1),
      Arguments.of(ConstraintType.CommonsLangIsTrue, "MultipleCommonsValidate3.kt", 1),
      Arguments.of(ConstraintType.CommonsLang3MatchesPattern, "MultipleCommonsValidate3.kt", 1),
      Arguments.of(ConstraintType.CommonsLang3NotBlank, "MultipleCommonsValidate3.kt", 1));
  }

  @ParameterizedTest
  @MethodSource("generatorCommonsValidate3")
  public void testCommonsValidate3Extractor_whenMultipleUses_expectListOfUses(
    ConstraintType constraintType, String fileName, int count) throws Exception {
    //given
    File file = new File(TEST_DATA_FOLDER, fileName);
    ConstraintCollector collector = new ConstraintCollector();
    CommonsValidate3Extractor commonsValidate3Extractor = new CommonsValidate3Extractor();

    //when
    commonsValidate3Extractor.analyse(Utils.getInputStream(file), "test", "<no version>",
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
  @MethodSource("generatorCommonsValidate3WildCardImport")
  public void testCommonsValidate3Extractor_whenMultipleUsesWithWildCardImport_expectListOfUses(
    ConstraintType constraintType, String fileName, int count) throws Exception {
    //given
    File file = new File(TEST_DATA_FOLDER, fileName);
    ConstraintCollector collector = new ConstraintCollector();
    CommonsValidate3Extractor commonsValidate3Extractor = new CommonsValidate3Extractor();

    //when
    commonsValidate3Extractor.analyse(Utils.getInputStream(file), "test", "<no version>",
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

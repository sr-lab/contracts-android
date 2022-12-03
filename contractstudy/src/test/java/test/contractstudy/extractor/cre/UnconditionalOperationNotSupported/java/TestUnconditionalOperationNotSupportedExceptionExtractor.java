package test.contractstudy.extractor.cre.UnconditionalOperationNotSupported.java;

import contractstudy.collectContracts.cre.UnconditionalOperationNotSupported.UnconditionalOperationNotSupportedExceptionExtractor;
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

public class TestUnconditionalOperationNotSupportedExceptionExtractor {

  private static final File TEST_DATA_FOLDER = new File(
    Utils.getBasePathTestFolder()
      + "extractor/cre/UnconditionalOperationNotSupported/java/testData");

  private static Stream<Arguments> generateJavaCRE() {
    return Stream.of(
      Arguments.of(ConstraintType.UCREUnsupportedOperationException,
        "MultipleUnconditionalOperationNotSupportedException.java", 2));
  }

  @ParameterizedTest
  @MethodSource("generateJavaCRE")
  public void testSpringAssertExtractor_whenMultipleUses_expectListOfUses(
    ConstraintType constraintType, String fileName, int count) throws Exception {
    //given
    File file = new File(TEST_DATA_FOLDER, fileName);
    ConstraintCollector collector = new ConstraintCollector();
    UnconditionalOperationNotSupportedExceptionExtractor extractor = new UnconditionalOperationNotSupportedExceptionExtractor();

    //when
    extractor.analyse(Utils.getInputStream(file), "test", "<no version>",
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

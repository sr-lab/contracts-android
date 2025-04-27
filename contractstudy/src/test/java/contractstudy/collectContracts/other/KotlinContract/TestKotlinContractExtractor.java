package contractstudy.collectContracts.other.KotlinContract;

import contractstudy.constants.constraint.ConstraintCollector;
import contractstudy.constants.constraint.ConstraintType;
import contractstudy.constants.constraint.ContractElement;
import contractstudy.usage.collectContracts.other.KotlinContract.KotlinContractExtractor;
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

public class TestKotlinContractExtractor {

  private static final File TEST_DATA_FOLDER = new File(
    Utils.getBasePathTestFolder() + "collectContracts/other/KotlinContract/testData");

  private static Stream<Arguments> getTestingParams() {
    return Stream.of(
      Arguments.of("KotlinContractTestData1.kt", 1)
    );
  }

  @ParameterizedTest
  @MethodSource("getTestingParams")
  public void testKotlinContractExtractor_whenKotlinContractExists_expectListOfConstraints(
    String fileName, int constraintCount) throws Exception {

    File file = new File(TEST_DATA_FOLDER, fileName);
    ConstraintCollector collector = new ConstraintCollector();
    KotlinContractExtractor extractor = new KotlinContractExtractor();

    extractor.analyse(Utils.getInputStream(file), "test", "<no version>",
      file.getName(), collector);

    List<ContractElement> contractsFound = collector
      .getContractElements()
      .stream()
      .filter(c -> c.getKind().equals(ConstraintType.KotlinContract))
      .collect(Collectors.toList());

    assertNotNull(contractsFound);
    assertEquals(constraintCount, contractsFound.size());
  }

}

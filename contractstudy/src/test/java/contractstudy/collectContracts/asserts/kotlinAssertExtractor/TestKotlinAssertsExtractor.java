package contractstudy.collectContracts.asserts.kotlinAssertExtractor;

import contractstudy.usage.collectContracts.asserts.JavaAssert.JavaAssertExtractor;
import contractstudy.usage.collectContracts.asserts.KotlinAssert.KotlinAssertExtractor;
import contractstudy.constants.constraint.ConstraintCollector;
import contractstudy.constants.constraint.ConstraintType;
import contractstudy.constants.constraint.ContractElement;
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
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class TestKotlinAssertsExtractor {

  private static final File TEST_DATA_FOLDER = new File(
    Utils.getBasePathTestFolder() + "collectContracts/asserts/kotlinAssertExtractor/testData");

  private static Stream<Arguments> getTestingParams() {
    return Stream.of(
      Arguments.of(ConstraintType.KotlinAssert, "KotlinAssertsMultiple.kt", 4),
      Arguments.of(ConstraintType.KotlinCheck, "KotlinAssertsMultiple.kt", 1),
      Arguments.of(ConstraintType.KotlinCheckNotNull, "KotlinAssertsMultiple.kt", 1),
      Arguments.of(ConstraintType.KotlinRequire, "KotlinAssertsMultiple.kt", 2),
      Arguments.of(ConstraintType.KotlinRequireNotNull, "KotlinAssertsMultiple.kt", 1));
  }

  @ParameterizedTest
  @MethodSource("getTestingParams")
  public void testJavaAssertsExtractor_whenJavaAssertsExist_expectListOfAsserts(
    ConstraintType constraintType, String fileName, int constraintCount) throws Exception {

    //given
    File file = new File(TEST_DATA_FOLDER, fileName);
    ConstraintCollector collector = new ConstraintCollector();
    KotlinAssertExtractor kotlinAssertExtractor = new KotlinAssertExtractor();

    //when
    kotlinAssertExtractor.analyse(Utils.getInputStream(file), "test", "<no version>",
      file.getName(), collector);

    //assert
    List<ContractElement> contractsFound = collector
      .getContractElements()
      .stream()
      .filter(c -> c.getKind().equals(constraintType))
      .collect(Collectors.toList());

    assertNotNull(contractsFound);
    assertEquals(constraintCount, contractsFound.size());
  }

  @Test
  public void testJavaAssertsExtractor_whenJavaAssertsNotExist_expectListOfAsserts()
    throws Exception {

    //given
    File file = new File(TEST_DATA_FOLDER, "JavaAssertsNone.java");
    ConstraintCollector collector = new ConstraintCollector();
    JavaAssertExtractor javaAssertExtractor = new JavaAssertExtractor();

    //when
    javaAssertExtractor.analyse(Utils.getInputStream(file), "test", "<no version>", file.getName(),
      collector);

    //assert
    List<ContractElement> contractsFound = collector
      .getContractElements()
      .stream()
      .filter(c -> c.getKind().equals(ConstraintType.JavaAssert))
      .collect(Collectors.toList());

    assertNotNull(contractsFound);
    assertEquals(0, contractsFound.size());
  }

}

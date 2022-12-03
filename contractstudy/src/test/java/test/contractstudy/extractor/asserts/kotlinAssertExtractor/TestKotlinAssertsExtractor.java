package test.contractstudy.extractor.asserts.kotlinAssertExtractor;

import contractstudy.collectContracts.asserts.JavaAssert.JavaAssertExtractor;
import contractstudy.collectContracts.asserts.KotlinAssert.KotlinAssertExtractor;
import contractstudy.constants.constraint.ConstraintCollector;
import contractstudy.constants.constraint.ConstraintType;
import contractstudy.constants.constraint.ContractElement;
import org.junit.jupiter.api.Test;
import test.contractstudy.utils.Utils;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class TestKotlinAssertsExtractor {

  private static final File TEST_DATA_FOLDER = new File(
    Utils.getBasePathTestFolder() + "extractor/asserts/kotlinAssertExtractor/testData");

  @Test
  public void testJavaAssertsExtractor_whenJavaAssertsExist_expectListOfAsserts() throws Exception {

    //given
    File file = new File(TEST_DATA_FOLDER, "KotlinAssertsMultiple.kt");
    ConstraintCollector collector = new ConstraintCollector();
    KotlinAssertExtractor kotlinAssertExtractor = new KotlinAssertExtractor();

    //when
    kotlinAssertExtractor.analyse(Utils.getInputStream(file), "test", "<no version>",
      file.getName(), collector);

    //assert
    List<ContractElement> contractsFound = collector
      .getContractElements()
      .stream()
      .filter(c -> c.getKind().equals(ConstraintType.KotlinAssert))
      .collect(Collectors.toList());

    assertNotNull(contractsFound);
    assertEquals(3, contractsFound.size());
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

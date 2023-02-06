package contractstudy.collectContracts.asserts.javaAssertExtractor;

import contractstudy.usage.collectContracts.asserts.JavaAssert.JavaAssertExtractor;
import contractstudy.constants.constraint.ConstraintCollector;
import contractstudy.constants.constraint.ConstraintType;
import contractstudy.constants.constraint.ContractElement;
import contractstudy.utils.Utils;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class TestJavaAssertsExtractor {

  private static final File TEST_DATA_FOLDER = new File(
    Utils.getBasePathTestFolder() + "collectContracts/asserts/javaAssertExtractor/testData");

  @Test
  public void testJavaAssertsExtractor_whenJavaAssertsExist_expectListOfAsserts() throws Exception {

    //given
    File file = new File(TEST_DATA_FOLDER, "JavaAssertsMultiple.java");
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

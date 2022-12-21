package contractstudy.collectContracts.api.CommonsValidateExtractor.CommonsValidate2Extractor.java.testData;

import org.apache.commons.lang.Validate;

import java.util.List;

public class MultipleCommonsValidate2 {

  public void test1(List<String> list) {
    Validate.notNull(null, "Passes value is null");
    Validate.notEmpty(list);
    Validate.allElementsOfType(list, String.class);
  }

  public void test2(boolean flag, List<String> list) {
    Validate.isTrue(flag, "Flag is not true");
    Validate.notNull(list);
    Validate.allElementsOfType(list, String.class);
  }

  public void test3(List<String> list) {
    Validate.allElementsOfType(list, String.class);
  }
}

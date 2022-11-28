package test.contractstudy.extractor.api.CommonsValidateExtractor.CommonsValidate3Extractor.java.testData;

import org.apache.commons.lang3.Validate;

import java.util.List;

public class MultipleCommonsValidate3 {

  public void test1(List<String> list) {
    Validate.notNull(null, "Passes value is null");
    Validate.notEmpty(list);
    Validate.exclusiveBetween(0, 10, 5);
  }

  public void test2(boolean flag, List<String> list) {
    Validate.isTrue(flag, "Flag is not true");
    Validate.notNull(list);
    Validate.matchesPattern("string", "pattern");
  }

  public void test3() {
    Validate.notBlank("string");
  }
}

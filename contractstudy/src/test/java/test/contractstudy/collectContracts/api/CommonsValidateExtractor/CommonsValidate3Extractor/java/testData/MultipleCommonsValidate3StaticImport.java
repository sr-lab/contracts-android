package test.contractstudy.collectContracts.api.CommonsValidateExtractor.CommonsValidate3Extractor.java.testData;

import java.util.List;

import static org.apache.commons.lang3.Validate.exclusiveBetween;
import static org.apache.commons.lang3.Validate.isTrue;
import static org.apache.commons.lang3.Validate.matchesPattern;
import static org.apache.commons.lang3.Validate.notBlank;
import static org.apache.commons.lang3.Validate.notEmpty;
import static org.apache.commons.lang3.Validate.notNull;

public class MultipleCommonsValidate3StaticImport {

  public void test1(List<String> list) {
    notNull(null, "Passes value is null");
    notEmpty(list);
    exclusiveBetween(0, 10, 5);
  }

  public void test2(boolean flag, List<String> list) {
    isTrue(flag, "Flag is not true");
    notNull(list);
    matchesPattern("string", "pattern");
  }

  public void test3() {
    notBlank("string");
  }
}

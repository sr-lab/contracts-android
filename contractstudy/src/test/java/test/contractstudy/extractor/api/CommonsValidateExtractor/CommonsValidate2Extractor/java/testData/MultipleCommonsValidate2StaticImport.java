package test.contractstudy.extractor.api.CommonsValidateExtractor.CommonsValidate2Extractor.java.testData;

import java.util.List;

import static org.apache.commons.lang.Validate.isTrue;
import static org.apache.commons.lang.Validate.notEmpty;
import static org.apache.commons.lang.Validate.notNull;

public class MultipleCommonsValidate2StaticImport {

  public void test1(List<String> list) {
    notNull(null, "Passes value is null");
    notEmpty(list);
  }

  public void test2(boolean flag) {
    isTrue(flag, "Flag is not true");
  }
}

package contractstudy.old.testdata.commonsvalidate2;

import static org.apache.commons.lang.Validate.notEmpty;

public class NotNull {

  public static void foo(int i) {
    notEmpty("foo", "string should not be null");
  }
}

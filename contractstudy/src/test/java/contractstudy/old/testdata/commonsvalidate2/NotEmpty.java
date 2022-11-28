package contractstudy.old.testdata.commonsvalidate2;

import java.util.ArrayList;

import static org.apache.commons.lang.Validate.notEmpty;

public class NotEmpty {

  public static void foo(int i) {
    notEmpty(new ArrayList(10), "list should not be empty");
  }
}

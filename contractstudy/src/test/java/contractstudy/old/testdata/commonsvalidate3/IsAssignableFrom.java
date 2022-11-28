package contractstudy.old.testdata.commonsvalidate3;

import static org.apache.commons.lang3.Validate.isAssignableFrom;

public class IsAssignableFrom {

  public static void foo(int i) {
    isAssignableFrom(Object.class, String.class, "some message");
  }
}

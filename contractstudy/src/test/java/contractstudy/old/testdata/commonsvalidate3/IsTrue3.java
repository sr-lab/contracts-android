package contractstudy.old.testdata.commonsvalidate3;

// static import all

import static org.apache.commons.lang3.Validate.isTrue;

public class IsTrue3 {

  public static void foo(int i) {
    isTrue(i < 0, "Parameter must be >=0");
  }
}

package contractstudy.old.testdata.commonsvalidate3;

import static org.apache.commons.lang3.Validate.inclusiveBetween;

public class InclusiveBetween {

  public static void foo(int i) {
    inclusiveBetween(1, i, 3, "some message");
  }
}

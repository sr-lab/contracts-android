package contractstudy.old.testdata.commonsvalidate3;

import java.util.ArrayList;

import static org.apache.commons.lang3.Validate.noNullElements;

public class NoNullElements {

  public static void foo(int i) {
    noNullElements(new ArrayList(10), "list should not have nulls");
  }
}

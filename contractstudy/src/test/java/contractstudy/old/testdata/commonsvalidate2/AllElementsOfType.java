package contractstudy.old.testdata.commonsvalidate2;

import java.util.List;

import static org.apache.commons.lang.Validate.allElementsOfType;

public class AllElementsOfType {

  public static void foo(List list) {
    allElementsOfType(list, String.class, "all elements in list must be strings");
  }
}

package test.contractstudy.extractor.asserts.javaAssertExtractor.testData;

import java.util.Arrays;
import java.util.List;

public class JavaAssertsNone {

  public void test1() {
    boolean result = returnAlwaysTrue();
  }

  public void test2() {
    List<Integer> result = returnList();
  }

  public boolean returnAlwaysTrue() {
    return true;
  }

  public List<Integer> returnList() {
    return Arrays.asList(0, 1, 2, 3);
  }

}

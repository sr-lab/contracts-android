package test.contractstudy.extractor.asserts.javaAssertExtractor.testData;

import java.util.Arrays;
import java.util.List;

public class JavaAssertsMultiple {

  public void test1() {
    boolean result = returnAlwaysTrue();
    assert result;
  }

  public void test2() {
    List<Integer> result = returnList();
    assert result.size() > 0;
    assert result.contains(1);
  }

  public boolean returnAlwaysTrue() {
    return true;
  }

  public List<Integer> returnList() {
    return Arrays.asList(0, 1, 2, 3);
  }

}

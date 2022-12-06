package test.contractstudy.collectContracts.api.SpringAssertExtractor.java.testData;

import org.springframework.util.Assert;

import java.util.List;

public class MultipleSpringAssert {

  public void test1(List<String> list) {
    Assert.doesNotContain("contains no", "test");
    Assert.hasLength("contains no", "this has length");
    Assert.notNull(list);
  }

  public void test2(boolean flag, List<String> list) {
    Assert.isTrue(flag, "This is true");
    Assert.state(flag);
    Assert.notNull(list);
  }

  public void test3(List<String> list) {
    Assert.notEmpty(list);
  }
}

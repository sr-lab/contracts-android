package contractstudy.collectContracts.api.GuavaPreconditionsExtractor.java.testData;

import com.google.common.base.Preconditions;

import java.util.List;


public class MultipleGuava {

  public void test1(List<String> list, Boolean flag) {
    Preconditions.checkArgument(flag, "This is an error");
    Preconditions.checkState(flag);
    Preconditions.checkElementIndex(10, 1);
  }

  public void test2(boolean flag, List<String> list) {
    Preconditions.checkNotNull(list, "This is an error");
    Preconditions.checkState(flag);
  }

  public void test3(List<String> list) {
    Preconditions.checkPositionIndex(10, 10);
    Preconditions.checkPositionIndexes(10, 10, 10);
  }
}

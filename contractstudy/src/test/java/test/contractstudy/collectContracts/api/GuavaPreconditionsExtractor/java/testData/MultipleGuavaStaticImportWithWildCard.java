package test.contractstudy.collectContracts.api.GuavaPreconditionsExtractor.java.testData;


import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkElementIndex;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkPositionIndex;
import static com.google.common.base.Preconditions.checkPositionIndexes;
import static com.google.common.base.Preconditions.checkState;


public class MultipleGuavaStaticImportWithWildCard {

  public void test1(List<String> list, Boolean flag) {
    checkArgument(flag, "This is an error");
    checkState(flag);
    checkElementIndex(10, 1);
  }

  public void test2(boolean flag, List<String> list) {
    checkNotNull(list, "This is an error");
    checkState(flag);
  }

  public void test3(List<String> list) {
    checkPositionIndex(10, 10);
    checkPositionIndexes(10, 10, 10);
  }
}

package contractstudy.old.testdata.guava;

// import all preconditions

import static com.google.common.base.Preconditions.checkElementIndex;

public class CheckElementIndex1 {

  public void foo(int i) {
    checkElementIndex(i, 5, "must fit into an array of size 5");
  }
}

package contractstudy.old.testdata.guava;

// import package 

import com.google.common.base.Preconditions;

public class CheckArgument2 {

  public static void foo(int i) {
    Preconditions.checkArgument(i < 0, "Parameter must be >=0");
  }
}

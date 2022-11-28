package contractstudy.old.testdata.springassert;

// import package 

import org.springframework.util.Assert;

public class DoesNotContain2 {

  public static void foo() {
    Assert.doesNotContain("pool", "pooh", "not good");
  }
}

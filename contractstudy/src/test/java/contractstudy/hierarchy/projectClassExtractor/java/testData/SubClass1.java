package contractstudy.hierarchy.projectClassExtractor.java.testData;

import java.util.Arrays;
import java.util.Objects;

enum SubEnum1 {
  NICE, COOL;
  String text() {return "True";};
}

abstract class SubAbstractClass1 {
  public String test() {
    return "test";
  }
}

public class SubClass1 extends ParentClass implements ParentInterface {

  public SubClass1() {
    super("test");
  }

  @Override
  public String test() {
    super.test();
    return "test";
  }

  @Override
  public String test2(String param1) {
    return param1;
  }

  public int test3(int param1, int param2) {
    int sum = super.testSum(param1, param2);
    return param1 + sum;
  }

  public String test3(String[] text) {
    return Arrays.stream(text).filter(x -> Objects.equals(x, "Test")).toString();
  }

  @Override
  public String printName() {
    return null;
  }
}

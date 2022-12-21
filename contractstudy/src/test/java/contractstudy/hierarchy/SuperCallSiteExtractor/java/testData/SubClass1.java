package contractstudy.hierarchy.SuperCallSiteExtractor.java.testData;

public class SubClass1 extends ParentClass {

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
}

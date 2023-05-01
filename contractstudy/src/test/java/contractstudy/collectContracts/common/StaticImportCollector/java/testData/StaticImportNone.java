package contractstudy.collectContracts.common.StaticImportCollector.java.testData;

public class StaticImportNone {

  public boolean test(boolean flag) {
    return !flag;
  }

  public int sum(int num1, int num2) {
    return num1 + num2;
  }

  public int compute(int area1, int area2) {
    StaticImportClass.test(true);
    return sum(area1, area2);
  }

}

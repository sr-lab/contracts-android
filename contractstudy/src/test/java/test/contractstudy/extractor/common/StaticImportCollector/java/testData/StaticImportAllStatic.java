package test.contractstudy.extractor.common.StaticImportCollector.java.testData;

import static java.lang.Math.PI;
import static java.lang.System.out;

public class StaticImportAllStatic {

  public boolean test(boolean flag) {
    return !flag;
  }

  public double sum(int num1, double num2) {
    return num1 + num2;
  }

  public double compute(int area1) {
    return sum(area1, PI);
  }

  public void print(String text) {
    out.println(text);
  }
}

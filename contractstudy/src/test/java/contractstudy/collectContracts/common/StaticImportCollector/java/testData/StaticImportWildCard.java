package contractstudy.collectContracts.common.StaticImportCollector.java.testData;

import java.util.List;

import static java.lang.System.out;

public class StaticImportWildCard {

  public boolean test(boolean flag) {
    return !flag;
  }

  public double sum(int num1, double num2) {
    return num1 + num2;
  }

  public long getTimeStamp(int area1) {
    return System.currentTimeMillis();
  }

  public void print(List<String> list) {
    for (String item : list) {
      out.println(item);
    }
  }
}

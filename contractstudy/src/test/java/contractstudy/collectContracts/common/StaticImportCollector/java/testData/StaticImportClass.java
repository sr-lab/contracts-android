package contractstudy.collectContracts.common.StaticImportCollector.java.testData;

import com.google.common.base.Preconditions;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class StaticImportClass {

  static public boolean test(boolean flag) {
    return !flag;
  }

  public int sum(int num1, int num2) {
    return num1 + num2;
  }

  public int compute(int area1, int area2) {
    return sum(area1, area2);
  }

  @NotNull
  public void print(List<String> list) {
    Preconditions.checkArgument(true, "This is an error");
    for (String item : list) {
      System.out.println(item);
    }
  }
}
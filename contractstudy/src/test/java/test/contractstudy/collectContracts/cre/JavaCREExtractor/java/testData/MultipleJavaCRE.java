package test.contractstudy.collectContracts.cre.JavaCREExtractor.java.testData;


import android.content.res.Resources;

import java.time.DateTimeException;
import java.util.List;

public class MultipleJavaCRE {

  public void test1(List<String> list) {
    if (list.isEmpty()) {
      throw new IllegalArgumentException("List is empty");
    }
    throw new IllegalArgumentException("This is not a JavaCRE");
  }

  public void test2(boolean flag, List<String> list) {
    if (!flag) {
      throw new Resources.NotFoundException("Flag is false");
    }
  }

  public void test3(List<String> list) {
    if (list.size() < 5) {
      throw new IndexOutOfBoundsException("Smaller than 5");
    }
    throw new DateTimeException("This is not an JavaCRE");
  }
}

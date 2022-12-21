package contractstudy.collectContracts.cre.UnconditionalOperationNotSupported.java.testData;


import java.util.List;

public class MultipleUnconditionalOperationNotSupportedException {

  public void test1(List<String> list) {
    if (list.isEmpty()) {
      throw new UnsupportedOperationException("List is empty");
    }
    throw new UnsupportedOperationException("This is not a JavaCRE");
  }

  public void test2(boolean flag, List<String> list) {
    if (!flag) {
      throw new UnsupportedOperationException("Flag is false");
    }
  }

  public void test3(List<String> list) {
    throw new UnsupportedOperationException("This is not an JavaCRE");
  }
}

package contractstudy.collectDatasetStats.java.testData;

interface DummyInterface {

  void test1(String name);

  void test2(String age, int name);
}

public class DummyJavaFile {

  DummyJavaFile(String name, int age) {
    doSomethingPublic(age);
  }

  DummyJavaFile(String name) {
    doSomethingPrivate();
  }

  int doSomethingPublic(int age) {
    return age + 5;
  }

  protected void doSomethingProtected() {
  }

  private int doSomethingPrivate() {
    int age = 10 + 15;
    return age;
  }

}

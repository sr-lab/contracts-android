package contractstudy.evolution.testdata.innerclasses;

import contractstudy.evolution.testdata.innerclassdeps.TestInterfaceInner;
import contractstudy.evolution.testdata.innerclassdeps.TestInterfaceOuter;

/**
 * @author Kamil Jezek [kamil.jezek@verifalabs.com]
 */
public class ClassWithInner implements TestInterfaceOuter {

  // some elements
  Integer field;

  public void method() {
  }

  public void lastMethod() {

  }

  public static class InnerStaticNoInherit {

    interface InnerInnerInterf {

    }

  }

  public static class InnerStaticInherit implements TestInterfaceInner {

  }

  public class InnerNoInherit {

    // comment
    Integer fieldInner;

    public void methodInner() {
    }
  }

  public class InnerInherit implements TestInterfaceInner {

    // commetn
    Number fieldInner2;

    void m() {
    }

  }

}

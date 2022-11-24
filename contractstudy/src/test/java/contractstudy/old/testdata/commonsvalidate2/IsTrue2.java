package contractstudy.old.testdata.commonsvalidate2;

// import package 

import org.apache.commons.lang.Validate;

public class IsTrue2 {
	public static void foo(int i) {
		Validate.isTrue(i<0,"Parameter must be >=0");
	}
}

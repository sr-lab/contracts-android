package contractstudy.old.testdata.commonsvalidate3;

// import package 

import org.apache.commons.lang3.Validate;

public class IsTrue2 {
	public static void foo(int i) {
		Validate.isTrue(i<0,"Parameter must be >=0");
	}
}

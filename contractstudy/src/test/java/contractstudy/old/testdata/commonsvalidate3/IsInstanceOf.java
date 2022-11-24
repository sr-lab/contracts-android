package contractstudy.old.testdata.commonsvalidate3;

import static org.apache.commons.lang3.Validate.isInstanceOf;

public class IsInstanceOf {
	public static void foo(int i) {
		isInstanceOf(Object.class,"foo","some message");
	}
}

package contractstudy.old.testdata.commonsvalidate3;

import static org.apache.commons.lang3.Validate.validState;

public class ValidState {
	public static void foo(int i) {
		validState(i==42,"some message");
	}
}

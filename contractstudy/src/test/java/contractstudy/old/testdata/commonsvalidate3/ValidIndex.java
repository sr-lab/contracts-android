package contractstudy.old.testdata.commonsvalidate3;

import static org.apache.commons.lang3.Validate.validIndex;

public class ValidIndex {
	public static void foo(int i) {
		validIndex(new String[]{},42,"some message");
	}
}

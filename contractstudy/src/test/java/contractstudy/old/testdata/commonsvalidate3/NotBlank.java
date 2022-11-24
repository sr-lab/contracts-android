package contractstudy.old.testdata.commonsvalidate3;

import static org.apache.commons.lang3.Validate.notBlank;

public class NotBlank {
	public static void foo(int i) {
		notBlank("foo","some message");
	}
}

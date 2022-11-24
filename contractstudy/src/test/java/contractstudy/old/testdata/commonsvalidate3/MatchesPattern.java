package contractstudy.old.testdata.commonsvalidate3;

import static org.apache.commons.lang3.Validate.matchesPattern;

public class MatchesPattern {
	public static void foo(int i) {
		matchesPattern("foo","foo*","some message");
	}
}

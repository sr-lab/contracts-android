package contractstudy.old.testdata.guava;

// static import all

import static com.google.common.base.Preconditions.checkArgument;

public class CheckArgument3 {
	public static void foo(int i) {
		checkArgument(i<0,"Parameter must be >=0");
	}
}

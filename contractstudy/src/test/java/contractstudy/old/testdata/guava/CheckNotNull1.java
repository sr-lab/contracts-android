package contractstudy.old.testdata.guava;

// import all preconditions

import static com.google.common.base.Preconditions.checkNotNull;

public class CheckNotNull1 {
	public void foo(Object obj) {
		checkNotNull(obj,"the argument should not be null");
	}
}

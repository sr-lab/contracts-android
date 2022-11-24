package contractstudy.old.testdata.guava;

// import all preconditions

import static com.google.common.base.Preconditions.checkState;

public class CheckState2 {
	int field = 42;
	public void foo(int i) {
		checkState(this.field==42);
	}
}

package contractstudy.old.testdata.springassert;

// static import all

import static org.springframework.util.Assert.doesNotContain;

public class DoesNotContain3 {
	public static void foo() {
        doesNotContain("pool","pooh","not good");
	}
}

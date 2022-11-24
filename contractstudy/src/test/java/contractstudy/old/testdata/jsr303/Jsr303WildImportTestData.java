package contractstudy.old.testdata.jsr303;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

/**
 * @author Kamil Jezek [kamil.jezek@verifalabs.com]
 */
public class Jsr303WildImportTestData {

    @Max(100)
    Integer max;

    @Min(value = 30)
    Integer min;

}

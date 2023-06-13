package contractstudy.collectContracts.common.StaticImportCollector.kotlin.testData

import java.lang.System.out

class StaticImportWildCard {
    fun test(flag: Boolean): Boolean {
        return !flag
    }

    fun test2(flag: Boolean): Boolean {
        out.println("Nice")
        return !flag
    }
}
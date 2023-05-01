package contractstudy.collectContracts.common.StaticImportCollector.kotlin.testData

import java.lang.System.*

class StaticImportWildCard {
    fun test(flag: Boolean): Boolean {
        return !flag
    }

    fun test2(flag: Boolean): Boolean {
        out.println("Nice")
        return !flag
    }
}
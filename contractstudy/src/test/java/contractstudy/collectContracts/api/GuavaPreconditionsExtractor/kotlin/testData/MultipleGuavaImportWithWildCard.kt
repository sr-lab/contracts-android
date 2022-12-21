package contractstudy.collectContracts.api.GuavaPreconditionsExtractor.kotlin.testData

import com.google.common.base.Preconditions.*

class MultipleGuavaImportWithWildCard {
    fun test1(list: List<String?>?, flag: Boolean?) {
        checkArgument(flag!!, "This is an error")
        checkState(flag)
        checkElementIndex(10, 1)
    }

    fun test2(flag: Boolean, list: List<String>) {
        checkNotNull(list, "This is an error")
        checkState(flag)
    }

    fun test3(list: List<String?>?) {
        checkPositionIndex(10, 10)
        checkPositionIndexes(10, 10, 10)
    }
}
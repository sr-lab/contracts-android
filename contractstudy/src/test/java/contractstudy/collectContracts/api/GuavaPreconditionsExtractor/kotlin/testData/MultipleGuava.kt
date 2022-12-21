package contractstudy.collectContracts.api.GuavaPreconditionsExtractor.kotlin.testData

import com.google.common.base.Preconditions

class MultipleGuava {
    fun test1(list: List<String?>?, flag: Boolean?) {
        Preconditions.checkArgument(flag!!, "This is an error")
        Preconditions.checkState(flag)
        Preconditions.checkElementIndex(10, 1)
    }

    fun test2(flag: Boolean, list: List<String>) {
        Preconditions.checkNotNull(list, "This is an error")
        Preconditions.checkState(flag)
    }

    fun test3(list: List<String?>?) {
        Preconditions.checkPositionIndex(10, 10)
        Preconditions.checkPositionIndexes(10, 10, 10)
    }
}
package contractstudy.collectContracts.common.StaticImportCollector.kotlin.testData

import com.google.common.base.Preconditions


class StaticImportSomeStatic {
    fun test(flag: Boolean): Boolean {
        Preconditions.checkArgument(flag, "This is an error")
        return !flag
    }

    private fun sum(num1: Int, num2: Double): Double {
        return num1 + num2
    }

    fun compute(area1: Int): Double {
        return sum(area1, Math.PI)
    }

    fun print(text: String?) {
        println(text)
    }
}
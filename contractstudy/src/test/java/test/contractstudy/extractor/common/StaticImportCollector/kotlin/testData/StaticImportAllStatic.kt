package test.contractstudy.extractor.common.StaticImportCollector.kotlin.testData


class StaticImportAllStatic {
    fun test(flag: Boolean): Boolean {
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
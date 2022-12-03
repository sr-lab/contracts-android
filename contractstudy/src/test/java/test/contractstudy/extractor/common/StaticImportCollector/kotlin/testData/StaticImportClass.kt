package test.contractstudy.extractor.common.StaticImportCollector.kotlin.testData

import java.util.HashMap;

class StaticImportClass {
    fun test(flag: Boolean): Boolean {
        return !flag
    }

    fun sum(num1: Int, num2: Int): Int {
        return num1 + num2
    }

    fun compute(area1: Int, area2: Int): Int {
        return sum(area1, area2)
    }

    fun print(list: HashMap<Int, String>) {
        for (item in list) {
            println(item)
        }
    }
}
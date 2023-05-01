package contractstudy.collectContracts.common.StaticImportCollector.kotlin.testData

import com.google.common.base.Preconditions
import org.jetbrains.annotations.NotNull
import java.util.List

class StaticImportClass {
    fun test(flag: Boolean): Boolean {
        return !flag
    }

    fun sum(num1: Int, num2: Int): Int {
        return num1 + num2
    }

    fun compute(area1: Int, area2: Int): Int {
        Preconditions.checkArgument(true, "This is an error")
        return sum(area1, area2)
    }

    @NotNull
    fun print(list: List<String>) {
        val class1 = StaticImportClass()
        for (item in list) {
            println(item)
        }
    }
}
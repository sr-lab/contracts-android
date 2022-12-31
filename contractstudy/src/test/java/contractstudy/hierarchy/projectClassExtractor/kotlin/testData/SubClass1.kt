package contractstudy.hierarchy.projectClassExtractor.kotlin.testData

import java.util.*

enum class Cool {
    Nice, Cool, Super
}

class SubClass1 : ParentClass, ParentInterface {

    constructor(text: String) : super(text)

    override fun test(): String {
        super.test()
        return "test"
    }

    override fun test2(param1: String): String {
        return param1
    }

    fun test3(param1: Int, param2: Int): Int {
        val sum = super.testSum(param1, param2)
        return param1 + sum
    }

    fun test3(text: Array<String>?): String {
        return Arrays.stream(text).filter { x: String? ->
            x == "Test"
        }.toString()
    }

    override fun printName(): String {
        return "Text"
    }
}
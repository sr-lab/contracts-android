package contractstudy.inheritance.SuperCallSiteExtractor.kotlin.testData

open class ParentClass(age: String) {

    constructor(age: String, text: String) : this(age)

    open fun test(): String {
        return "test"
    }

    open fun test2(param1: String): String {
        return param1
    }

    fun testSum(param1: Int, param2: Int): Int {
        return param1 + param2
    }
}
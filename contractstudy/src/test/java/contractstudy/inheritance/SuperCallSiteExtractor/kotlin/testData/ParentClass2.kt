package contractstudy.inheritance.SuperCallSiteExtractor.kotlin.testData

open class ParentClass2 {

    constructor(age: String, text: String)

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
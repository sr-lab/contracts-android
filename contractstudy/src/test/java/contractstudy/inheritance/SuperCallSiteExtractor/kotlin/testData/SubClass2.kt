package contractstudy.inheritance.SuperCallSiteExtractor.kotlin.testData

class SubClass2: ParentClass2 {

    constructor(text: String, age: String) : super(text, age)

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
}
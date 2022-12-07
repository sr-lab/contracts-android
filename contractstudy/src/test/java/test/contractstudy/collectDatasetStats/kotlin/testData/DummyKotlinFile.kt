package test.contractstudy.collectDatasetStats.kotlin.testData


interface DummyInterface {
    fun test1(name: String)
    fun test2(age: String, name: Int)
}

class DummyKotlinFile(name: String) {

    constructor(name: String, age: Int) : this(name) {
        doSomethingPublic(age)
    }

    fun doSomethingPublic(age: Int): Int {
        return age + 5
    }

    protected fun doSomethingProtected() {
        return
    }

    private fun doSomethingPrivate(): Int {
        val age = 10 + 15
        return age
    }

}
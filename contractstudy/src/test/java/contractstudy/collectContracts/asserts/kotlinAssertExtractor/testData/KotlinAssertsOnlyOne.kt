package contractstudy.collectContracts.asserts.kotlinAssertExtractor.testData

class KotlinAssertsOnlyOne {
    fun test1(visible: Boolean, realAssert: Boolean) {
        assert(visible)
        assert(realAssert) { "this is a real assert" }
    }

    fun assert(expression: Boolean) {
        return
    }
}
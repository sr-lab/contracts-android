package contractstudy.collectContracts.asserts.kotlinAssertExtractor.testData

class KotlinAssertsMultiple {
    fun test1() {
        val result = returnAlwaysTrue()
        assert(result) { "Fail" }
    }

    fun test2() {
        val result = returnList()
        assert(result.size > 0)
        assert(true)
        assert(result.contains(1)) { "Otherwise fail" }
    }

    private fun returnAlwaysTrue(): Boolean {
        return true
    }

    private fun returnList(): List<Int> {
        return listOf(0, 1, 2, 3)
    }

    private fun assert(name: Boolean) {
        return
    }
}
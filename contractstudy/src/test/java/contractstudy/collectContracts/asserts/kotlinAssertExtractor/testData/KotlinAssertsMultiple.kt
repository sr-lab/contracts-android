package contractstudy.collectContracts.asserts.kotlinAssertExtractor.testData

class KotlinAssertsMultiple {
    fun test1() {
        val result = returnAlwaysTrue()
        assert(result) { "Fail" }
        require(result) { "nice" }
    }

    fun test2() {
        val result = returnList()
        assert(result.size > 0)
        assert(true)
        assert(result.contains(1)) { "Otherwise fail" }
    }

    fun testMethod(list: List<String>) {
        check(list.isEmpty()) { "List is empty" }
        checkNotNull(list.get(0) != null)
    }

    private fun returnAlwaysTrue(): Boolean {
        require(false)
        return true
    }

    private fun returnList(): List<Int> {
        requireNotNull(false)
        return listOf(0, 1, 2, 3)
    }

}
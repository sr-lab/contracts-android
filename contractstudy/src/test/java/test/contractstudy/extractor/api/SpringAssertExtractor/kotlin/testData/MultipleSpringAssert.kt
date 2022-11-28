package test.contractstudy.extractor.api.SpringAssertExtractor.kotlin.testData

import org.springframework.util.Assert

class MultipleSpringAssert {
    fun test1(list: List<String?>?) {
        Assert.doesNotContain("contains no", "test")
        Assert.hasLength("contains no", "this has length")
        Assert.notNull(list)
    }

    fun test2(flag: Boolean, list: List<String?>?) {
        Assert.isTrue(flag, "This is true")
        Assert.state(flag)
        Assert.notNull(list)
    }

    fun test3(list: List<String?>?) {
        Assert.notEmpty(list)
    }
}
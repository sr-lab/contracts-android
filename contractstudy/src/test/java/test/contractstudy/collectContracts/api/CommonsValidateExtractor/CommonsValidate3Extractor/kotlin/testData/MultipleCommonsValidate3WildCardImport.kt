package test.contractstudy.collectContracts.api.CommonsValidateExtractor.CommonsValidate3Extractor.kotlin.testData

import org.apache.commons.lang3.Validate.*

class MultipleCommonsValidate3WildCardImport {
    fun test1(list: List<String>) {
        notNull<Any?>(null, "Passes value is null")
        notEmpty(list)
        exclusiveBetween(0, 10, 5)
    }

    fun test2(flag: Boolean, list: List<String>) {
        isTrue(flag, "Flag is not true")
        notNull(list)
        matchesPattern("string", "pattern")
    }

    fun test3() {
        notBlank("string")
    }
}
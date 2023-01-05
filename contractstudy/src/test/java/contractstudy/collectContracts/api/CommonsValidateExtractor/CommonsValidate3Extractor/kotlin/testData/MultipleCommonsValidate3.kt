package contractstudy.collectContracts.api.CommonsValidateExtractor.CommonsValidate3Extractor.kotlin.testData

import org.apache.commons.lang3.Validate

class MultipleCommonsValidate3 {
    fun test1(list: List<String>) {
        Validate.notNull<Any?>(null, "Passes value is null")
        Validate.notEmpty(list)
        Validate.exclusiveBetween(0, 10, 5, "Message");
    }

    fun test2(flag: Boolean, list: List<String>) {
        Validate.isTrue(flag, "Flag is not true")
        Validate.notNull(list)
        Validate.matchesPattern("string", "pattern", "Message123")
    }

    fun test3() {
        Validate.notBlank("string")
    }
}
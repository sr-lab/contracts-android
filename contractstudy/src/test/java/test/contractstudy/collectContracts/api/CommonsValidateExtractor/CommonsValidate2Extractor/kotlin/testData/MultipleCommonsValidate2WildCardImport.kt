package test.contractstudy.collectContracts.api.CommonsValidateExtractor.CommonsValidate2Extractor.kotlin.testData

import org.apache.commons.lang.Validate.*

class MultipleCommonsValidate2WildCardImport {
    fun test1(list: List<String?>?) {
        notNull(null, "Passes value is null")
        notEmpty(list)
    }

    fun test2(flag: Boolean) {
        isTrue(flag, "Flag is not true")
    }
}
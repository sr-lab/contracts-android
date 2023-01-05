package contractstudy.collectContracts.api.CommonsValidateExtractor.CommonsValidate2Extractor.kotlin.testData

import org.apache.commons.lang.Validate

interface Common

class MultipleCommonsValidate2(name: String) {

    fun test1(list: List<String?>?) {
        Validate.notNull(null, "Passes value is null")
        Validate.notEmpty(list)
        Validate.allElementsOfType(list, String::class.java)
    }

    fun test2(flag: Boolean, list: List<String?>?) {
        Validate.isTrue(flag, "Flag is not true")
        Validate.notNull(list)
        Validate.allElementsOfType(list, String::class.java, "Message")
    }

    fun test3(list: List<String?>?) {
        Validate.allElementsOfType(list, String::class.java)
    }
}
package contractstudy.collectContracts.other.KotlinContract.testData

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

class KotlinContractTestData1 {

    @ExperimentalContracts
    fun testMethod(birthdate: String) {
        isBirthdateValid(birthdate)
        val birthdateParts = birthdate.split("/")
    }

    @ExperimentalContracts
    private fun isBirthdateValid(birthdate: String?) {
        contract { returns() implies (birthdate != null) }
        if (birthdate == null) {
            throw java.lang.IllegalArgumentException()
        }
    }

    @ExperimentalContracts
    private fun testSomething(birthdate: String?) {
        contract { returns() } //TODO: Currently, this is not considered a Kotlin Contract. Should we?
        if (birthdate == null) {
            throw java.lang.IllegalArgumentException()
        }
    }
}
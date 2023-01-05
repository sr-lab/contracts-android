package contractstudy.collectContracts.annotation.kotlin.testData

import javax.validation.constraints.*

class AnnotationsWildCard(
    @field:NotNull private val manufacturer: String, @field:Size(
        min = 2,
        max = 14
    ) @field:NotNull private val licensePlate: String, @field:Min(
        2
    ) private val seatCount: Int
) {
    operator fun get(@Max(5) index: Int): String {
        return "string"
    }
}
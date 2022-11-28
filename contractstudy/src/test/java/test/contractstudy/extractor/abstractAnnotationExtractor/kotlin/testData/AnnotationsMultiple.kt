package test.contractstudy.extractor.abstractAnnotationExtractor.kotlin.testData

import javax.validation.constraints.Max
import javax.validation.constraints.Min
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

class AnnotationsMultiple(
    @field:[NotNull] val manufacturer: String, @field:Size(
        min = 2,
        max = 14
    ) @field:NotNull val licensePlate: String, @field:Min(
        2
    ) val seatCount: Int
) {
    operator fun get(@Max(5) index: Int): String {
        return "string"
    }
}
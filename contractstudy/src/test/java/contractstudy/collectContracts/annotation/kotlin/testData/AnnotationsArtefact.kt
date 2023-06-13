package contractstudy.collectContracts.annotation.kotlin.testData

import android.annotation.SuppressLint
import javax.validation.constraints.*

class AnnotationsArtefact(
    @NotNull private val manufacturer: String
) {
    @Min(1)
    private val manufacturer123: String = "test"

    operator fun get(@Max(5) index: Int): String {
        return "string"
    }

    @SuppressLint("SetJavaScriptEnabled", "NewApi", "ShowToast")
    fun test123(manufacturer: String?): String? {
        return manufacturer
    }

    @Size(min = 1)
    fun addProductToList(): List<String> {
        return ArrayList()
    }

    fun testThis(hello: String, @Null test: Int): List<String> {
        return ArrayList()
    }
}
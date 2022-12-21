package contractstudy.collectContracts.cre.JavaCREExtractor.kotlin.testData

import android.content.res.Resources
import java.time.DateTimeException

class MultipleJavaCRE {
    fun test1(list: List<String?>) {
        if (!list.isEmpty()) {
            throw IllegalArgumentException("List is empty")
        }
        throw IllegalArgumentException("This is not a JavaCRE")
    }

    fun test2(flag: Boolean, list: List<String?>?) {
        if (!flag) {
            throw Resources.NotFoundException("Flag is false")
        }
    }

    fun test3(list: List<String?>) {
        if (list.size < 5) {
            throw IndexOutOfBoundsException("Smaller than 5")
        }
        throw DateTimeException("This is not an JavaCRE")
    }
}
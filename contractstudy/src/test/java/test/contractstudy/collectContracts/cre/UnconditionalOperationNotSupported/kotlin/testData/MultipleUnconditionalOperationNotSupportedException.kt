package test.contractstudy.extractor.cre.JavaCREExtractor.kotlin.testData

class MultipleUnconditionalOperationNotSupportedException {
    fun test1(list: List<String?>) {
        if (list.isEmpty()) {
            throw UnsupportedOperationException("List is empty")
        }
        throw UnsupportedOperationException("This is not a JavaCRE")
    }

    fun test2(flag: Boolean, list: List<String?>?) {
        if (!flag) {
            throw UnsupportedOperationException("Flag is false")
        }
    }

    fun test3(list: List<String?>?) {
        throw UnsupportedOperationException("This is not an JavaCRE")
    }
}
package contractstudy.utils.kotlinParser

import contractstudy.kotlinParser.parser.createKotlinCoreEnvironment
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.jetbrains.kotlin.com.intellij.openapi.util.text.StringUtilRt
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtPsiFactory

open class KotlinParser(
    private val environment: KotlinCoreEnvironment = createKotlinCoreEnvironment(printStream = System.err)
) {

    private val psiFileFactory = KtPsiFactory(environment.project, markGenerated = false)

    fun createKtFile(fileName: String, content: String): KtFile {
        return psiFileFactory.createFile(
            fileName,
            StringUtilRt.convertLineSeparators(content)
        )
    }
}
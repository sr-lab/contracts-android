package contractstudy.kotlinParser

import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.jetbrains.kotlin.com.intellij.openapi.util.text.StringUtilRt
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtPsiFactory

open class KtCompiler(
    protected val environment: KotlinCoreEnvironment = createKotlinCoreEnvironment(printStream = System.err)
) {

    protected val psiFileFactory = KtPsiFactory(environment.project, markGenerated = false)

    /*fun compile(basePath: Path?, path: Path): KtFile {
        require(Files.isRegularFile(path)) { "Given sub path ($path) should be a regular file!" }
        val content = path.toFile().readText()
        return createKtFile(content, basePath, path)
    }*/

    fun createKtFile(content: String): KtFile {
        //require(Files.isRegularFile(path)) { "Given sub path ($path) should be a regular file!" }

        //val normalizedAbsolutePath = path.toAbsolutePath().normalize()
        //val lineSeparator = content.determineLineSeparator()

        var test = StringUtilRt.convertLineSeparators(content);

        val psiFile = psiFileFactory.createFile(
            "test.kt",
            StringUtilRt.convertLineSeparators(content)
        )

        return psiFile

        /*return psiFile.apply {
            putUserData(LINE_SEPARATOR, lineSeparator)
            val normalizedBasePath = basePath?.toAbsolutePath()?.normalize()
            normalizedBasePath?.relativize(normalizedAbsolutePath)?.let { relativePath ->
                putUserData(BASE_PATH, normalizedBasePath.toAbsolutePath().toString())
                putUserData(RELATIVE_PATH, relativePath.toString())
            }
        }*/
    }
}

internal fun String.determineLineSeparator(): String {
    val i = this.lastIndexOf('\n')
    if (i == -1) {
        return if (this.lastIndexOf('\r') == -1) System.getProperty("line.separator") else "\r"
    }
    return if (i != 0 && this[i - 1] == '\r') "\r\n" else "\n"
}
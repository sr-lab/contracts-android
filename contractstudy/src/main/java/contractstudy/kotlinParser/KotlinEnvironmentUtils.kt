package contractstudy.kotlinParser

import org.jetbrains.kotlin.cli.common.CLIConfigurationKeys
import org.jetbrains.kotlin.cli.common.environment.setIdeaIoUseFallback
import org.jetbrains.kotlin.cli.common.messages.MessageRenderer
import org.jetbrains.kotlin.cli.common.messages.PrintingMessageCollector
import org.jetbrains.kotlin.cli.jvm.compiler.EnvironmentConfigFiles
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.jetbrains.kotlin.com.intellij.mock.MockProject
import org.jetbrains.kotlin.com.intellij.openapi.Disposable
import org.jetbrains.kotlin.com.intellij.openapi.util.Disposer
import org.jetbrains.kotlin.config.*
import java.io.PrintStream

/**
 * Creates an environment instance which can be used to compile source code to KtFile's.
 * This environment also allows to modify the resulting AST files.
 */
fun createKotlinCoreEnvironment(
    configuration: CompilerConfiguration = CompilerConfiguration(),
    disposable: Disposable = Disposer.newDisposable(),
    printStream: PrintStream,
): KotlinCoreEnvironment {
    setIdeaIoUseFallback()
    configuration.put(
        CLIConfigurationKeys.MESSAGE_COLLECTOR_KEY,
        PrintingMessageCollector(printStream, MessageRenderer.PLAIN_FULL_PATHS, false)
    )
    configuration.put(CommonConfigurationKeys.MODULE_NAME, "contractstudy")

    val environment = KotlinCoreEnvironment.createForProduction(
        disposable,
        configuration,
        EnvironmentConfigFiles.JVM_CONFIG_FILES
    )

    val projectCandidate = environment.project

    val project = requireNotNull(projectCandidate as? MockProject) {
        "MockProject type expected, actual - ${projectCandidate.javaClass.simpleName}"
    }

    project.registerService(PomModel::class.java, PomModel(project))

    return environment
}
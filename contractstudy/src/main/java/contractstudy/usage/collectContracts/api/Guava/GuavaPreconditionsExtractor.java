package contractstudy.usage.collectContracts.api.Guava;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import contractstudy.constants.constraint.ContractElement;
import contractstudy.model.ExtractionListener;
import contractstudy.model.Extractor;
import contractstudy.usage.collectContracts.api.Guava.visitor.MethodVisitorToCollectGuavaPreconditionsInvocations;
import contractstudy.usage.collectContracts.api.Guava.visitor.MethodVisitorToCollectGuavaPreconditionsInvocationsKotlin;
import contractstudy.usage.collectContracts.common.StaticImportCollector.StaticImportCollector;
import contractstudy.usage.collectContracts.common.StaticImportCollector.StaticImportCollectorKotlin;
import contractstudy.utils.InputStreamToStringConversion;
import contractstudy.utils.LanguageUtils;
import contractstudy.utils.kotlinParser.KotlinParser;
import org.jetbrains.kotlin.com.intellij.psi.PsiFile;

import java.io.IOException;
import java.io.InputStream;

/**
 * Extractor to find calls of the guava Preconditions API methods in source code.
 *
 * @author jens dietrich
 */
public class GuavaPreconditionsExtractor implements Extractor<ContractElement> {

  @Override
  public void analyse(InputStream in, String programName, String version, String cuName,
    ExtractionListener<ContractElement> consumer) throws Exception {
    try {
      switch (LanguageUtils.getLanguageFromNameExtension(cuName)) {
        case JAVA:
          analyseJava(in, programName, version, cuName, consumer);
          break;
        case KOTLIN:
          analyseKotlin(in, programName, version, cuName, consumer);
          break;
      }
    } catch (Exception t) {
      consumer.extractionExceptionEncountered(
        "Cannot parse " + programName + "-" + version + "/" + cuName, t);
    }
  }

  private void analyseJava(
    final InputStream in,
    final String programName,
    final String version,
    final String cuName,
    final ExtractionListener<ContractElement> consumer) {
    CompilationUnit cu = StaticJavaParser.parse(in);
    StaticImportCollector staticCollector = new StaticImportCollector("com.google.common.base",
      "com.google.common.base.Preconditions");
    staticCollector.visit(cu, null);
    new MethodVisitorToCollectGuavaPreconditionsInvocations(consumer, programName, version,
      cuName, staticCollector.getStaticImportState(),
      staticCollector.getStaticallyImportedMethodNames()).visit(cu, null);
  }

  private void analyseKotlin(
    final InputStream in,
    final String programName,
    final String version,
    final String cuName,
    final ExtractionListener<ContractElement> consumer) throws IOException {
    String src = new InputStreamToStringConversion(in).getResult();
    PsiFile psiFile = new KotlinParser().createKtFile(cuName, src);
    StaticImportCollectorKotlin staticCollector = new StaticImportCollectorKotlin(
      "com.google.common.base",
      "com.google.common.base.Preconditions");
    psiFile.accept(staticCollector);
    MethodVisitorToCollectGuavaPreconditionsInvocationsKotlin visitor = new MethodVisitorToCollectGuavaPreconditionsInvocationsKotlin(
      consumer, programName, version,
      cuName, staticCollector.getStaticImportState(),
      staticCollector.getStaticallyImportedMethodNames());
    psiFile.accept(visitor);
  }

}

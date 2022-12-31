package contractstudy.collectContracts.api.SpringAssert;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import contractstudy.model.ExtractionListener;
import contractstudy.model.Extractor;
import contractstudy.collectContracts.api.SpringAssert.visitor.MethodVisitorToCollectSpringAssertInvocations;
import contractstudy.collectContracts.api.SpringAssert.visitor.MethodVisitorToCollectSpringAssertInvocationsKotlin;
import contractstudy.collectContracts.common.StaticImportCollector.StaticImportCollector;
import contractstudy.collectContracts.common.StaticImportCollector.StaticImportCollectorKotlin;
import contractstudy.constants.constraint.ContractElement;
import contractstudy.utils.InputStreamToStringConversion;
import contractstudy.utils.LanguageUtils;
import contractstudy.utils.kotlinParser.KotlinParser;
import org.jetbrains.kotlin.com.intellij.psi.PsiFile;

import java.io.IOException;
import java.io.InputStream;

/**
 * Extractor to find calls of the org.springframework.util.Assert API methods in source code.
 *
 * @author jens dietrich
 */
public class SpringAssertExtractor implements Extractor<ContractElement> {

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
    StaticImportCollector staticCollector = new StaticImportCollector("org.springframework.util",
      "org.springframework.util.Assert");
    staticCollector.visit(cu, null);
    new MethodVisitorToCollectSpringAssertInvocations(consumer, programName, version, cuName,
      staticCollector.getStaticImportState(),
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
      "org.springframework.util",
      "org.springframework.util.Assert");
    psiFile.accept(staticCollector);
    MethodVisitorToCollectSpringAssertInvocationsKotlin methodVisitorToCollectSpringAssertInvocations =
      new MethodVisitorToCollectSpringAssertInvocationsKotlin(
        consumer, programName, version, cuName,
        staticCollector.getStaticImportState(),
        staticCollector.getStaticallyImportedMethodNames()
      );
    psiFile.accept(methodVisitorToCollectSpringAssertInvocations);
  }

}

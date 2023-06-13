package contractstudy.usage.collectContracts.cre.UnconditionalOperationNotSupported;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import contractstudy.config.Logging;
import contractstudy.constants.constraint.ContractElement;
import contractstudy.model.ExtractionListener;
import contractstudy.model.Extractor;
import contractstudy.usage.collectContracts.cre.UnconditionalOperationNotSupported.visitor.MethodVisitorToCollectUnconditionalUnsupportedOperationExceptionThrows;
import contractstudy.usage.collectContracts.cre.UnconditionalOperationNotSupported.visitor.MethodVisitorToCollectUnconditionalUnsupportedOperationExceptionThrowsKotlin;
import contractstudy.utils.InputStreamToStringConversion;
import contractstudy.utils.LanguageUtils;
import contractstudy.utils.kotlinParser.KotlinParser;
import org.apache.log4j.Logger;
import org.jetbrains.kotlin.com.intellij.psi.PsiFile;

import java.io.IOException;
import java.io.InputStream;

/**
 * Extractor to find simple patterns "if (<condition>) throw new <SomeException>" in source code.
 *
 * @author jens dietrich
 */
public class UnconditionalOperationNotSupportedExceptionExtractor implements Extractor<ContractElement> {

  private static final Logger LOGGER = Logging.getLogger(UnconditionalOperationNotSupportedExceptionExtractor.class);

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
        default:
      }
    } catch (Exception t) {
      t.printStackTrace();
      LOGGER.warn("Exception while extracting from " + cuName);
      consumer.extractionExceptionEncountered("Cannot parse " + programName + "-" + version + "/" + cuName, t);
    }
  }

  private void analyseJava(
    final InputStream in,
    final String programName,
    final String version,
    final String cuName,
    final ExtractionListener<ContractElement> consumer) {
    CompilationUnit cu = StaticJavaParser.parse(in);
    new MethodVisitorToCollectUnconditionalUnsupportedOperationExceptionThrows(consumer,
      programName, version, cuName).visit(cu, null);
  }

  private void analyseKotlin(
    final InputStream in,
    final String programName,
    final String version,
    final String cuName,
    final ExtractionListener<ContractElement> consumer) throws IOException {
    String src = new InputStreamToStringConversion(in).getResult();
    PsiFile psiFile = new KotlinParser().createKtFile(cuName, src);
    MethodVisitorToCollectUnconditionalUnsupportedOperationExceptionThrowsKotlin visitor =
      new MethodVisitorToCollectUnconditionalUnsupportedOperationExceptionThrowsKotlin(consumer,
        programName, version, cuName);
    psiFile.accept(visitor);
  }
}

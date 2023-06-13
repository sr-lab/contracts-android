package contractstudy.usage.collectContracts.api.CommonsValidate.CommonsValidate3;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import contractstudy.config.Logging;
import contractstudy.constants.constraint.ConstraintCategory;
import contractstudy.constants.constraint.ContractElement;
import contractstudy.model.ExtractionListener;
import contractstudy.model.Extractor;
import contractstudy.usage.collectContracts.api.CommonsValidate.CommonsValidate3.visitor.MethodVisitorToCollectCommons3ValidateInvocations;
import contractstudy.usage.collectContracts.api.CommonsValidate.CommonsValidate3.visitor.MethodVisitorToCollectCommons3ValidateInvocationsKotlin;
import contractstudy.usage.collectContracts.common.StaticImportCollector.StaticImportCollector;
import contractstudy.usage.collectContracts.common.StaticImportCollector.StaticImportCollectorKotlin;
import contractstudy.utils.InputStreamToStringConversion;
import contractstudy.utils.LanguageUtils;
import contractstudy.utils.kotlinParser.KotlinParser;
import org.apache.log4j.Logger;
import org.jetbrains.kotlin.com.intellij.psi.PsiFile;

import java.io.IOException;
import java.io.InputStream;

/**
 * Extractor for the commons validate API v3.
 *
 * @author jens dietrich
 */
public class CommonsValidate3Extractor implements Extractor<ContractElement> {

  private static final Logger LOGGER = Logging.getLogger(CommonsValidate3Extractor.class);

  @Override
  public void analyse(
    InputStream in,
    String programName,
    String version,
    String cuName,
    ExtractionListener<ContractElement> consumer
  ) {
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
    StaticImportCollector staticCollector = new StaticImportCollector("org.apache.commons.lang3",
      "org.apache.commons.lang3.Validate");
    staticCollector.visit(cu, null);
    new MethodVisitorToCollectCommons3ValidateInvocations(consumer, programName, version, cuName,
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
      "org.apache.commons.lang3",
      "org.apache.commons.lang3.Validate",
      ConstraintCategory.API);
    psiFile.accept(staticCollector);
    MethodVisitorToCollectCommons3ValidateInvocationsKotlin methodVisitorToCollectCommons3ValidateInvocationsKotlin =
      new MethodVisitorToCollectCommons3ValidateInvocationsKotlin(
        consumer, programName, version, cuName,
        staticCollector.getStaticImportState(),
        staticCollector.getStaticallyImportedMethodNames()
      );
    psiFile.accept(methodVisitorToCollectCommons3ValidateInvocationsKotlin);
  }

}

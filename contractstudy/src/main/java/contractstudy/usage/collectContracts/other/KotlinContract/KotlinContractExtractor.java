package contractstudy.usage.collectContracts.other.KotlinContract;

import contractstudy.config.Logging;
import contractstudy.constants.constraint.ContractElement;
import contractstudy.model.ExtractionListener;
import contractstudy.model.Extractor;
import contractstudy.usage.collectContracts.cre.UnconditionalOperationNotSupported.UnconditionalOperationNotSupportedExceptionExtractor;
import contractstudy.utils.InputStreamToStringConversion;
import contractstudy.utils.LanguageUtils;
import contractstudy.utils.kotlinParser.KotlinParser;
import org.apache.log4j.Logger;
import org.jetbrains.kotlin.com.intellij.psi.PsiFile;

import java.io.InputStream;

public class KotlinContractExtractor implements Extractor<ContractElement> {

  private static final Logger LOGGER = Logging.getLogger(UnconditionalOperationNotSupportedExceptionExtractor.class);

  @Override
  public void analyse(
    final InputStream in,
    final String programName,
    final String version,
    final String cuName,
    final ExtractionListener<ContractElement> consumer) throws Exception {
    try {
      if (LanguageUtils.getLanguageFromNameExtension(cuName) == LanguageUtils.Language.KOTLIN) {
        analyseKotlin(in, programName, version, cuName, consumer);
      }
    } catch (Error | Exception e) {
      LOGGER.warn("Exception while extracting from " + cuName);
      consumer.extractionExceptionEncountered("Cannot parse " + programName + "-" + version + "/" + cuName, e);
    }
  }

  private void analyseKotlin(
    final InputStream in,
    final String programName,
    final String version,
    final String cuName,
    final ExtractionListener<ContractElement> consumer) throws Exception {
    String src = new InputStreamToStringConversion(in).getResult();
    PsiFile psiFile = new KotlinParser().createKtFile(cuName, src);
    KotlinContractVisitor kotlinAssertVisitor = new KotlinContractVisitor(programName, version,
      cuName,
      consumer);
    psiFile.accept(kotlinAssertVisitor);
  }

}
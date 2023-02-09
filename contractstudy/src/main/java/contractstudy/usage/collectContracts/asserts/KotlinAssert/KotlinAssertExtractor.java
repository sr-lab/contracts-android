package contractstudy.usage.collectContracts.asserts.KotlinAssert;

import contractstudy.constants.constraint.ContractElement;
import contractstudy.model.ExtractionListener;
import contractstudy.model.Extractor;
import contractstudy.utils.InputStreamToStringConversion;
import contractstudy.utils.LanguageUtils;
import contractstudy.utils.kotlinParser.KotlinParser;
import org.jetbrains.kotlin.com.intellij.psi.PsiFile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class KotlinAssertExtractor implements Extractor<ContractElement> {

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
      consumer.extractionExceptionEncountered(
        "Cannot parse " + programName + "-" + version + "/" + cuName, e);
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
    List<KotlinAssertExpression> ambiguousAssertions = new ArrayList<>();
    KotlinPotentialAmbiguousAssertionsVisitor ambiguousAssertionsVisitor = new KotlinPotentialAmbiguousAssertionsVisitor(programName, version, cuName, consumer, ambiguousAssertions);
    psiFile.accept(ambiguousAssertionsVisitor);
    ambiguousAssertions = ambiguousAssertionsVisitor.getAmbiguousExpressions();
    KotlinAssertVisitor kotlinAssertVisitor = new KotlinAssertVisitor(programName, version, cuName,
      consumer, ambiguousAssertions);
    psiFile.accept(kotlinAssertVisitor);
  }

}

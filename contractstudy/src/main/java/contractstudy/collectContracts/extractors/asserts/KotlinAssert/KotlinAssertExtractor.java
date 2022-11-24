package contractstudy.collectContracts.extractors.asserts.KotlinAssert;

import contractstudy.ContractElement;
import contractstudy.ExtractionListener;
import contractstudy.Extractor;
import contractstudy.kotlinParser.KotlinParser;
import contractstudy.utils.InputStreamToStringConversion;
import contractstudy.utils.LanguageUtils;
import org.jetbrains.kotlin.psi.KtFile;

import java.io.InputStream;

/**
 * @author Kamil Jezek [kamil.jezek@verifalabs.com]
 * <p>
 * Java asserts don't work on Kotlin.
 */
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
    KtFile ktFile = new KotlinParser().createKtFile(cuName, src);

    KotlinAssertVisitor kotlinAssertVisitor = new KotlinAssertVisitor(programName, version, cuName,
      consumer);

    ktFile.accept(kotlinAssertVisitor);

    //new KotlinAssertVisitor(programName, version, cuName, consumer).visitKtFile(ktFile);

    //KotlinAssertVisitor kotlinAssertVisitor = new KotlinAssertVisitor(programName, version, cuName, consumer);
    //ktFile.accept(kotlinAssertVisitor);

    System.out.println("nice");

    //CompilationUnit cu = StaticJavaParser.parse(in);
    //new JavaAssertVisitor(programName, version, cuName, consumer).visit(cu, null);

  }

}

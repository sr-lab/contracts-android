package contractstudy.collectContracts.asserts.JavaAssert;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import contractstudy.model.ExtractionListener;
import contractstudy.model.Extractor;
import contractstudy.constants.constraint.ContractElement;
import contractstudy.utils.LanguageUtils;

import java.io.InputStream;

/**
 * @author Kamil Jezek [kamil.jezek@verifalabs.com]
 * <p>
 * Java asserts don't work on Kotlin.
 */
public class JavaAssertExtractor implements Extractor<ContractElement> {

  @Override
  public void analyse(
    final InputStream in,
    final String programName,
    final String version,
    final String cuName,
    final ExtractionListener<ContractElement> consumer) throws Exception {

    try {
      if (LanguageUtils.getLanguageFromNameExtension(cuName) == LanguageUtils.Language.JAVA) {
        analyseJava(in, programName, version, cuName, consumer);
      }
    } catch (Error | Exception e) {
      consumer.extractionExceptionEncountered(
        "Cannot parse " + programName + "-" + version + "/" + cuName, e);
    }

  }

  private void analyseJava(
    final InputStream in,
    final String programName,
    final String version,
    final String cuName,
    final ExtractionListener<ContractElement> consumer) {

    CompilationUnit cu = StaticJavaParser.parse(in);
    new JavaAssertVisitor(programName, version, cuName, consumer).visit(cu, null);

  }

}

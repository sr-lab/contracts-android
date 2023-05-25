package contractstudy.usage.collectContracts.asserts.JavaAssert;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import contractstudy.config.Logging;
import contractstudy.constants.constraint.ContractElement;
import contractstudy.model.ExtractionListener;
import contractstudy.model.Extractor;
import contractstudy.usage.collectContracts.api.SpringAssert.SpringAssertExtractor;
import contractstudy.utils.LanguageUtils;
import org.apache.log4j.Logger;

import java.io.InputStream;

/**
 * @author Kamil Jezek [kamil.jezek@verifalabs.com]
 * <p>
 * Java asserts don't work on Kotlin.
 */
public class JavaAssertExtractor implements Extractor<ContractElement> {

  private static final Logger LOGGER = Logging.getLogger(JavaAssertExtractor.class);

  @Override
  public void analyse(
    final InputStream in,
    final String programName,
    final String version,
    final String cuName,
    final ExtractionListener<ContractElement> consumer
  ) {
    try {
      if (LanguageUtils.getLanguageFromNameExtension(cuName) == LanguageUtils.Language.JAVA) {
        analyseJava(in, programName, version, cuName, consumer);
      }
    } catch (Error | Exception e) {
      LOGGER.warn("Exception while extracting from " + cuName);
      consumer.extractionExceptionEncountered("Cannot parse " + programName + "-" + version + "/" + cuName, e);
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

package contractstudy.collectContracts.extractors;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import contractstudy.ContractElement;
import contractstudy.ExtractionListener;
import contractstudy.Extractor;
import contractstudy.collectContracts.extractors.common.StaticImportCollector.StaticImportCollector;
import contractstudy.collectContracts.extractors.visitors.java.MethodVisitorToCollectGuavaPreconditionsInvocations;

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
      CompilationUnit cu = StaticJavaParser.parse(in);
      StaticImportCollector staticCollector = new StaticImportCollector("com.google.common.base",
        "com.google.common.base.Preconditions");
      staticCollector.visit(cu, null);

      new MethodVisitorToCollectGuavaPreconditionsInvocations(consumer, programName, version,
        cuName, staticCollector.getStaticImportState(),
        staticCollector.getStaticallyImportedMethodNames()).visit(cu, null);
    } catch (Exception t) {
      consumer.extractionExceptionEncountered(
        "Cannot parse " + programName + "-" + version + "/" + cuName, t);
    }
  }

}

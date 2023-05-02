package contractstudy.usage.collectContracts.annotation;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import contractstudy.constants.constraint.ConstraintType;
import contractstudy.constants.constraint.ContractElement;
import contractstudy.model.ExtractionListener;
import contractstudy.model.Extractor;
import contractstudy.usage.collectContracts.annotation.visitor.VisitorToCollectAnnotations;
import contractstudy.usage.collectContracts.annotation.visitor.VisitorToCollectAnnotationsKotlin;
import contractstudy.usage.collectContracts.common.StaticImportCollector.StaticImportCollector;
import contractstudy.usage.collectContracts.common.StaticImportCollector.StaticImportCollectorKotlin;
import contractstudy.usage.collectContracts.common.StaticImportCollector.constants.StaticImportState;
import contractstudy.utils.InputStreamToStringConversion;
import contractstudy.utils.LanguageUtils;
import contractstudy.utils.kotlinParser.KotlinParser;
import org.jetbrains.kotlin.com.intellij.psi.PsiFile;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Abstract annotation extractor based on Kamil's JSR303 extractor.
 *
 * @author Kamil Jezek [kamil.jezek@verifalabs.com]
 */
public class AbstractAnnotationExtractor implements Extractor<ContractElement> {

  private final Map<String, ConstraintType> constraintsByName = new HashMap<>();
  private final String annotationPackageName;


  public AbstractAnnotationExtractor(String constraintTypePrefix, String annotationPackageName) {
    this.annotationPackageName = annotationPackageName;

    for (ConstraintType p : ConstraintType.values()) {
      String name = p.name();
      if (name.startsWith(constraintTypePrefix)) {
        constraintsByName.put(name.substring(constraintTypePrefix.length()), p);
      }
    }
  }

  @Override
  public void analyse(
    final InputStream in,
    final String programName,
    final String version,
    final String cuName,
    final ExtractionListener<ContractElement> consumer) throws Exception {
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
    final ExtractionListener<ContractElement> consumer
  ) {
    CompilationUnit cu = StaticJavaParser.parse(in);
    StaticImportCollector importsCollector = new StaticImportCollector(annotationPackageName, "");
    importsCollector.visit(cu, null);
    StaticImportState importState = importsCollector.getStaticImportState();
    VisitorToCollectAnnotations visitor = new VisitorToCollectAnnotations(consumer, programName,
      version, cuName, importState, constraintsByName);
    visitor.visit(cu, null);
  }

  private void analyseKotlin(
    final InputStream in,
    final String programName,
    final String version,
    final String cuName,
    final ExtractionListener<ContractElement> consumer
  ) throws Exception {
    String src = new InputStreamToStringConversion(in).getResult();
    PsiFile psiFile = new KotlinParser().createKtFile(cuName, src);
    StaticImportCollectorKotlin importsCollector = new StaticImportCollectorKotlin(annotationPackageName, "");
    psiFile.accept(importsCollector);
    StaticImportState importState = importsCollector.getStaticImportState();
    VisitorToCollectAnnotationsKotlin visitor = new VisitorToCollectAnnotationsKotlin(consumer, programName, version, cuName,
      importState, constraintsByName);
    psiFile.accept(visitor);
  }

}

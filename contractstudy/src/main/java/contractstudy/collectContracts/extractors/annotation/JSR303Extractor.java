package contractstudy.collectContracts.extractors.annotation;

/**
 * Extractor for JSR303 annotations
 *
 * @author jens dietrich
 */
public class JSR303Extractor extends AbstractAnnotationExtractor {

  public JSR303Extractor() {
    super("JSR303", "javax.validation.constraints");
  }
}

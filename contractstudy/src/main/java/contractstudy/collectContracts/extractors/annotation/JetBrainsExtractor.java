package contractstudy.collectContracts.extractors.annotation;

/**
 * Extractor for JetBrains annotations
 *
 * @author jens dietrich
 */
public class JetBrainsExtractor extends AbstractAnnotationExtractor {

  public JetBrainsExtractor() {
    super("JetBrains", "org.jetbrains.annotations");
  }
}

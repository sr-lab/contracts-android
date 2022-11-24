package contractstudy.collectContracts.extractors;

import contractstudy.collectContracts.extractors.annotations.AbstractAnnotationExtractor;

/**
 * Extractor for JSR303 annotations
 *
 * @author jens dietrich
 */
public class FindBugsAnnotationExtractor extends AbstractAnnotationExtractor {

  public FindBugsAnnotationExtractor() {
    super("FindBugs", "edu.umd.cs.findbugs.annotations");
  }
}

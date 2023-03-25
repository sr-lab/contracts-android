package contractstudy.constants.constraint;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Highest level of grouping. The grouping is as follows: constraint type > contstraint group > constraint category example:
 * ConstraintTypeJSR303Null > ConstraintGroup.ANNO_JSR30 > ConstraintCategory.ANNOTATION
 *
 * @author jens dietrich
 */
@Getter
@AllArgsConstructor
public enum ConstraintCategory {
  ASSERTION("assertion"),
  RUNTIME_EXCEPTION("runtime exception"),
  API("api"),
  ANNOTATION("annotation"),
  OTHERS("others");

  private final String name;

  static public Map<ConstraintCategory, Map<String, Integer>> getConstraintCategoryListForProgramVersion() {
    Map<ConstraintCategory, Map<String, Integer>> categories = new LinkedHashMap<>();
    categories.put(ConstraintCategory.API, new HashMap<>());
    categories.put(ConstraintCategory.ANNOTATION, new HashMap<>());
    categories.put(ConstraintCategory.ASSERTION, new HashMap<>());
    categories.put(ConstraintCategory.RUNTIME_EXCEPTION, new HashMap<>());
    categories.put(ConstraintCategory.OTHERS, new HashMap<>());
    return categories;
  }
}

package contractstudy.constants.constraint;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * Highest level of grouping. The grouping is as follows: constraint type > contstraint group >
 * constraint category example: ConstraintTypeJSR303Null > ConstraintGroup.ANNO_JSR30 >
 * ConstraintCategory.ANNOTATION
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
    return Map.ofEntries(
      Map.entry(ConstraintCategory.API, new HashMap<>()),
      Map.entry(ConstraintCategory.ANNOTATION, new HashMap<>()),
      Map.entry(ConstraintCategory.ASSERTION, new HashMap<>()),
      Map.entry(ConstraintCategory.RUNTIME_EXCEPTION, new HashMap<>()),
      Map.entry(ConstraintCategory.OTHERS, new HashMap<>())
    );
  }
}

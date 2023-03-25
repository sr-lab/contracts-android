package contractstudy.constants.constraint;

import lombok.Getter;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Groups of constraints, usually grouping is by extractor. constraint type > constraint group > constraint category example:
 * ConstraintTypeJSR303Null > ConstraintGroup.ANNO_JSR30 > ConstraintCategory.ANNOTATION
 *
 * @author jens dietrich
 */
@Getter
public enum ConstraintGroup {
  CONDITIONAL_RUNTIME_EXCEPTION("conditional runtime exception", "cond. runtime exc.",
    ConstraintCategory.RUNTIME_EXCEPTION),
  UNCONDITIONAL_RUNTIME_EXCEPTION("unsupported operation exception", "unsupp. op. exc.",
    ConstraintCategory.RUNTIME_EXCEPTION),
  ASSERTION("assertion", "assert", ConstraintCategory.ASSERTION),
  CAPI_GUAVA("guava precondition API", "guava precond.", ConstraintCategory.API),
  CAPI_COMMONS_VALIDATE("commons validate API", "commons validate", ConstraintCategory.API),
  CAPI_SPRING_ASSERT("spring assert API", "spring assert", ConstraintCategory.API),
  ANNO_JSR303("JSR303, JSR349", "JSR303, JSR349", ConstraintCategory.ANNOTATION),
  ANNO_JSR305("JSR305", "JSR305", ConstraintCategory.ANNOTATION),
  ANNO_FINDBUGS("FindBugs", "findbugs", ConstraintCategory.ANNOTATION),
  ANNO_JetBrains("JetBrains", "jetbrains", ConstraintCategory.ANNOTATION),
  ANNO_Lombok("Lombok", "lombok", ConstraintCategory.ANNOTATION),
  ANNO_Android("Android", "android", ConstraintCategory.ANNOTATION),
  ANNO_AndroidX("AndroidX", "androidx", ConstraintCategory.ANNOTATION),

  KOTLIN_CONTRACTS("Kotlin Contracts", "kotlin contracts", ConstraintCategory.OTHERS);

  private final String name;
  private final String shortName;
  private final ConstraintCategory category;

  ConstraintGroup(String name, String shortName, ConstraintCategory category) {
    this.name = name;
    this.shortName = shortName;
    this.category = category;
  }

  static public Map<ConstraintGroup, Integer> getListOfConstraintsGroup() {
    Map<ConstraintGroup, Integer> groups = new LinkedHashMap<>();
    groups.put(ConstraintGroup.ASSERTION, 0);
    groups.put(ConstraintGroup.CONDITIONAL_RUNTIME_EXCEPTION, 0);
    groups.put(ConstraintGroup.UNCONDITIONAL_RUNTIME_EXCEPTION, 0);
    groups.put(ConstraintGroup.CAPI_GUAVA, 0);
    groups.put(ConstraintGroup.CAPI_SPRING_ASSERT, 0);
    groups.put(ConstraintGroup.CAPI_COMMONS_VALIDATE, 0);
    groups.put(ConstraintGroup.ANNO_JSR303, 0);
    groups.put(ConstraintGroup.ANNO_JSR305, 0);
    groups.put(ConstraintGroup.ANNO_Android, 0);
    groups.put(ConstraintGroup.ANNO_AndroidX, 0);
    groups.put(ConstraintGroup.KOTLIN_CONTRACTS, 0);
    return groups;
  }
}

package contractstudy.evolution.constants;

import contractstudy.constants.VisibilityModifier;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SubtypeDiffKeys {
  /**
   * We do not use class name, as it is not stored in the Constraint. Only CU is stored.
   */
  EMPTY_CLASS_NAME(""),
  /**
   * Removed because cannot be sorted.
   */
  REMOVED_SORT("sort"),
  /**
   * Removed because it is abstract method.
   */
  REMOVED_ABSTRACT("abstract"),
  /**
   * Removed because it calls super.
   */
  REMOVED_SUPER("super"),
  /**
   * Removed because it uses annotations.
   */
  REMOVED_ANNOTATIONS("annotations");

  private final String key;
}

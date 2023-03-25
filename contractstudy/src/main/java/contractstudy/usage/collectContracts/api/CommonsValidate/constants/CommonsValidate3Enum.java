package contractstudy.usage.collectContracts.api.CommonsValidate.constants;

import contractstudy.constants.constraint.ConstraintType;
import lombok.AllArgsConstructor;

/**
 * Patterns supported by both org.apache.commons.lang2.Validate and org.apache.commons.lang3.Validate.
 */
@AllArgsConstructor
public enum CommonsValidate3Enum {
  EXCLUSIVE_BETWEEN(ConstraintType.CommonsLang3ExclusiveBetween, "exclusiveBetween"),
  INCLUSIVE_BETWEEN(ConstraintType.CommonsLang3InclusiveBetween, "inclusiveBetween"),
  IS_ASSIGNABLE_FROM(ConstraintType.CommonsLang3IsAssignableFrom, "isAssignableFrom"),
  MATCHES_PATTERN(ConstraintType.CommonsLang3MatchesPattern, "matchesPattern"),
  IS_INSTANCE_OF(ConstraintType.CommonsLang3IsInstanceOf, "isInstanceOf"),
  NOT_BLANK(ConstraintType.CommonsLang3NotBlank, "notBlank"),
  VALID_INDEX(ConstraintType.CommonsLang3ValidIndex, "validIndex"),
  VALID_STATE(ConstraintType.CommonsLang3ValidState, "validState");

  public final ConstraintType constraintType;
  public final String methodName;

  public static CommonsValidate3Enum getEnumValueFromMethodName(String methodName) {
    switch (methodName) {
      case "exclusiveBetween":
        return EXCLUSIVE_BETWEEN;
      case "inclusiveBetween":
        return INCLUSIVE_BETWEEN;
      case "isAssignableFrom":
        return IS_ASSIGNABLE_FROM;
      case "matchesPattern":
        return MATCHES_PATTERN;
      case "isInstanceOf":
        return IS_INSTANCE_OF;
      case "notBlank":
        return NOT_BLANK;
      case "validIndex":
        return VALID_INDEX;
      case "validState":
        return VALID_STATE;
      default:
        return null;
    }
  }
}

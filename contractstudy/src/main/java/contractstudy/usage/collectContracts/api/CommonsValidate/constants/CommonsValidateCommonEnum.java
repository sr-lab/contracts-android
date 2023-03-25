package contractstudy.usage.collectContracts.api.CommonsValidate.constants;

import contractstudy.constants.constraint.ConstraintType;
import lombok.AllArgsConstructor;

/**
 * Patterns supported by both org.apache.commons.lang2.Validate and org.apache.commons.lang3.Validate.
 */
@AllArgsConstructor
public enum CommonsValidateCommonEnum {
  IS_TRUE(ConstraintType.CommonsLangIsTrue, "isTrue"),
  NO_NULL_ELEMENTS(ConstraintType.CommonsLangNoNullElements, "noNullElements"),
  NOT_EMPTY(ConstraintType.CommonsLangNotEmpty, "notEmpty"),
  NOT_NULL(ConstraintType.CommonsLangNotNull, "notNull");

  public final ConstraintType constraintType;
  public final String methodName;

  public static CommonsValidateCommonEnum getEnumValueFromMethodName(String methodName) {
    switch (methodName) {
      case "isTrue":
        return IS_TRUE;
      case "noNullElements":
        return NO_NULL_ELEMENTS;
      case "notEmpty":
        return NOT_EMPTY;
      case "notNull":
        return NOT_NULL;
      default:
        return null;
    }
  }
}

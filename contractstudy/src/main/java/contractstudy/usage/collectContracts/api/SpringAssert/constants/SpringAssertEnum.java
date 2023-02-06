package contractstudy.usage.collectContracts.api.SpringAssert.constants;

import contractstudy.constants.constraint.ConstraintType;
import lombok.AllArgsConstructor;

/**
 * Patterns supported by both org.apache.commons.lang2.Validate and
 * org.apache.commons.lang3.Validate.
 */
@AllArgsConstructor
public enum SpringAssertEnum {
  DOES_NOT_CONTAIN(ConstraintType.SpringAssertDoesNotContain, "doesNotContain"),
  HAS_LENGTH(ConstraintType.SpringAssertHasLength, "hasLength"),
  HAS_TEXT(ConstraintType.SpringAssertHasText, "hasText"),
  NOT_EMPTY(ConstraintType.SpringAssertNotEmpty, "notEmpty"),
  NO_NULL_ELEMENTS(ConstraintType.SpringAssertNoNullElements, "noNullElements"),
  IS_NULL(ConstraintType.SpringAssertIsNull, "isNull"),
  NOT_NULL(ConstraintType.SpringAssertNotNull, "notNull"),
  IS_INSTANCE_OF(ConstraintType.SpringIsInstanceOf, "isInstanceOf"),
  IS_ASSIGNABLE(ConstraintType.SpringIsAssignable, "isAssignable"),
  STATE(ConstraintType.SpringAssertState, "state"),
  IS_TRUE(ConstraintType.SpringAssertIsTrue, "isTrue");

  public final ConstraintType constraintType;
  public final String methodName;

  public static SpringAssertEnum getEnumValueFromMethodName(String methodName) {
    switch (methodName) {
      case "doesNotContain":
        return DOES_NOT_CONTAIN;
      case "hasLength":
        return HAS_LENGTH;
      case "hasText":
        return HAS_TEXT;
      case "notEmpty":
        return NOT_EMPTY;
      case "noNullElements":
        return NO_NULL_ELEMENTS;
      case "isNull":
        return IS_NULL;
      case "notNull":
        return NOT_NULL;
      case "isInstanceOf":
        return IS_INSTANCE_OF;
      case "isAssignable":
        return IS_ASSIGNABLE;
      case "state":
        return STATE;
      case "isTrue":
        return IS_TRUE;
      default:
        return null;
    }
  }
}

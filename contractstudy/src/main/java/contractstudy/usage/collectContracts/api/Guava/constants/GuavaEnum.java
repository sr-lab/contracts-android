package contractstudy.usage.collectContracts.api.Guava.constants;

import contractstudy.constants.constraint.ConstraintType;
import lombok.AllArgsConstructor;

/**
 * Patterns supported by both org.apache.commons.lang2.Validate and org.apache.commons.lang3.Validate.
 */
@AllArgsConstructor
public enum GuavaEnum {
  CHECK_ARGUMENT(ConstraintType.GuavaPreconditionCheckArgument, "checkArgument"),
  CHECK_STATE(ConstraintType.GuavaPreconditionCheckState, "checkState"),
  ELEMENT_INDEX(ConstraintType.GuavaPreconditionElementIndex, "checkElementIndex"),
  NOT_NULL(ConstraintType.GuavaPreconditionNotNull, "checkNotNull"),
  POSITION_INDEX(ConstraintType.GuavaPreconditionPositionIndex, "checkPositionIndex"),
  POSITION_INDEXES(ConstraintType.GuavaPreconditionPositionIndexes, "checkPositionIndexes");

  public final ConstraintType constraintType;
  public final String methodName;

  public static GuavaEnum getEnumValueFromMethodName(String methodName) {
    switch (methodName) {
      case "checkArgument":
        return CHECK_ARGUMENT;
      case "checkState":
        return CHECK_STATE;
      case "checkElementIndex":
        return ELEMENT_INDEX;
      case "checkNotNull":
        return NOT_NULL;
      case "checkPositionIndex":
        return POSITION_INDEX;
      case "checkPositionIndexes":
        return POSITION_INDEXES;
      default:
        return null;
    }
  }
}

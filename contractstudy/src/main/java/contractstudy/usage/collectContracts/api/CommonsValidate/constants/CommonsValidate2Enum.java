package contractstudy.usage.collectContracts.api.CommonsValidate.constants;

import contractstudy.constants.constraint.ConstraintType;
import lombok.AllArgsConstructor;

/**
 * Patterns supported by both org.apache.commons.lang2.Validate and
 * org.apache.commons.lang3.Validate.
 */
@AllArgsConstructor
public enum CommonsValidate2Enum {
  ALL_ELEMENTS_OF_TYPE(ConstraintType.CommonsLang2AllElementsOfType, "allElementsOfType");

  public final ConstraintType constraintType;
  public final String methodName;

  public static CommonsValidate2Enum getEnumValueFromMethodName(String methodName) {
    if ("allElementsOfType".equals(methodName)) {
      return ALL_ELEMENTS_OF_TYPE;
    }
    return null;
  }
}

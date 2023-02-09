package contractstudy.usage.collectContracts.asserts.KotlinAssert;

import contractstudy.constants.constraint.ConstraintType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import static contractstudy.constants.constraint.ConstraintType.KotlinAssert;
import static contractstudy.constants.constraint.ConstraintType.KotlinCheck;
import static contractstudy.constants.constraint.ConstraintType.KotlinCheckNotNull;
import static contractstudy.constants.constraint.ConstraintType.KotlinRequire;
import static contractstudy.constants.constraint.ConstraintType.KotlinRequireNotNull;

@AllArgsConstructor
@Getter
public enum KotlinAssertExpression {
  ASSERT("assert"),
  CHECK("check"),
  CHECK_NOT_NULL("checkNotNull"),
  REQUIRE("require"),
  REQUIRE_NOT_NULL("requireNotNull"),
  NONE("none");

  private final String expression;

  public static KotlinAssertExpression getExpressionKeyBy(String expression) {
    if (expression == null) {
      return NONE;
    }
    switch (expression) {
      case "assert":
        return ASSERT;
      case "check":
        return CHECK;
      case "checkNotNull":
        return CHECK_NOT_NULL;
      case "require":
        return REQUIRE;
      case "requireNotNull":
        return REQUIRE_NOT_NULL;
      case "none":
        return NONE;
    }
    return NONE;
  }

  public static ConstraintType getConstraintTypeBy(KotlinAssertExpression expressionKey) {
    switch (expressionKey) {
      case ASSERT:
        return KotlinAssert;
      case CHECK:
        return KotlinCheck;
      case CHECK_NOT_NULL:
        return KotlinCheckNotNull;
      case REQUIRE:
        return KotlinRequire;
      case REQUIRE_NOT_NULL:
        return KotlinRequireNotNull;
    }
    return null;
  }

}
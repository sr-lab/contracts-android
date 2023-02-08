package contractstudy.constants.constraint;

import lombok.AllArgsConstructor;
import lombok.Getter;

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

  private String name;

}

package contractstudy.constants.constraint;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Classification into kind of constraint: precondition, postcondition etc.
 *
 * @author jens dietrich
 */
@Getter
@AllArgsConstructor
public enum ConstraintClassification {
  PRECONDITION("pre-condition"),
  POSTCONDITION("post-condition"),
  INVARIANT("invariants"),
  ANY("not classified");

  private final String name;

  static public Map<ConstraintClassification, Integer> getConstraintClassificationList() {
    Map<ConstraintClassification, Integer> classifications = new LinkedHashMap<>();
    classifications.put(ConstraintClassification.PRECONDITION, 0);
    classifications.put(ConstraintClassification.POSTCONDITION, 0);
    classifications.put(ConstraintClassification.INVARIANT, 0);
    classifications.put(ConstraintClassification.ANY, 0);
    return classifications;
  }

}

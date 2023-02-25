package contractstudy.constants.constraint;

import lombok.AllArgsConstructor;
import lombok.Getter;

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

  private String name;

  static public Map<ConstraintClassification, Integer> getConstraintClassificationList() {
    return Map.ofEntries(
      Map.entry(ConstraintClassification.PRECONDITION, 0),
      Map.entry(ConstraintClassification.POSTCONDITION, 0),
      Map.entry(ConstraintClassification.INVARIANT, 0),
      Map.entry(ConstraintClassification.ANY, 0)
    );
  }


}

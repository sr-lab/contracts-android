package contractstudy.evolution.diffRules;

import contractstudy.constants.constraint.ConstraintClassification;
import contractstudy.constants.constraint.ContractElement;
import contractstudy.evolution.constants.DiffResult;
import contractstudy.evolution.model.DiffRule;

import java.util.List;

/**
 * Check whether a precondition was added. This corresponds to "strengthened expectations" and
 * usually constitutes a contract violation in the context of method overriding or evolution.
 *
 * @author jens dietrich
 */
public class PreconditionsStrengthened implements DiffRule {

  @Override
  public DiffResult compare(List<ContractElement> constraints1,
    List<ContractElement> constraints2) {

    constraints1 = Utils.filter(constraints1,
      c -> c.getClassification() == ConstraintClassification.PRECONDITION);
    constraints2 = Utils.filter(constraints2,
      c -> c.getClassification() == ConstraintClassification.PRECONDITION);

    // if preconditions are the sane, it is handled by a earlier rule
    if (constraints2.size() > 0 && constraints1.size() <= constraints2.size()) {
      // check containment, by lax - do not check whether additionalInfo field matches
      // complexity is terrible, but lists will be very small
      boolean allFound = true;
      for (ContractElement c1 : constraints1) {
        boolean found = false;
        for (ContractElement c2 : constraints2) {
          found = found || Utils.unchanged(c1, c2, true) || Utils.constraintStrengthened(c1, c2);
        }
        allFound = allFound && found;
      }
      if (allFound) {
        return DiffResult.PRECONDITION_ADDED;
      }
    }
    return DiffResult.CANNOT_BE_CLASSIFIED;
  }

}

package contractstudy.evolution.model;

import contractstudy.constants.constraint.ContractElement;
import contractstudy.evolution.constants.DiffResult;

import java.util.List;

/**
 * Plugin for differ.
 *
 * @author jens dietrich
 * @see Differ
 */
public interface DiffRule {

  /**
   * Try to classify the difference between two lists of constraints. If this cannot be done, return
   * DiffResult.CANNOT_BE_CLASSIFIED
   *
   * @param constraints1
   * @param constraints2
   * @return
   */
  DiffResult compare(List<ContractElement> constraints1, List<ContractElement> constraints2);
}

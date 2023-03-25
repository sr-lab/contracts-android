package contractstudy.model;

import contractstudy.constants.constraint.ContractElement;

/**
 * Consumes constraints found, for instance, by collecting them in a list, or by appending them to a report.
 *
 * @author jens dietrich
 */
public interface ExtractionListener<T extends ContractElement> {

  void constraintFound(T constraint);

  void extractionExceptionEncountered(String message, Throwable x);
}

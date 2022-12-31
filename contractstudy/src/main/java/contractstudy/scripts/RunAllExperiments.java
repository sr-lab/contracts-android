package contractstudy.scripts;

import contractstudy.config.Logging;
import contractstudy.scripts.model.Experiment;
import contractstudy.scripts.model.ExperimentArtefact;
import org.apache.log4j.Logger;

/**
 * This is the main script that invokes all experiments.
 * <p>
 * So far, new experiments must be added in this script in right order, no automatic management is
 * provided.
 *
 * @author Kamil Jezek [kamil.jezek@verifalabs.com]
 */
public class RunAllExperiments {

  private static final Experiment[] EXPERIMENTS = new Experiment[]{
    new CollectContracts(),
    new AnalyseContractUsage(),
    new CollectDatasetStats(),
    new CollectInvocationViaSuper(),
    new ComputeInheritanceHierarchy(),
    new AnalyseContractEvolution(),
    new CollectProgramVersionStats(),
    new AnalyseContractUsageAcrossVersions(),
    new AnalyseHierarchyContracts(),
  };

  private static final Logger LOGGER = Logging.getLogger(ComputeInheritanceHierarchy.class);

  public static void main(String[] args) throws Exception {
    for (Experiment experiment : EXPERIMENTS) {
      allPrerequisitesExistOrElseThrow(experiment);
      runExperimentIfNotAlreadyExecuted(experiment);
    }
  }

  private static void allPrerequisitesExistOrElseThrow(Experiment experiment) {
    for (ExperimentArtefact artefact : experiment.requires()) {
      if (!artefact.exists()) {
        throw new IllegalStateException(
          "No artefact " + artefact.getName() + " found to proceed with " + experiment.provides()
            .getName());
      }
    }
  }

  private static void runExperimentIfNotAlreadyExecuted(Experiment experiment) throws Exception {
    if (experiment.provides().exists()) {
      LOGGER.info("Skipping already performed experiment: " + experiment.provides().getName());
    } else {
      LOGGER.info("Invoking: " + experiment.provides().getName());
      experiment.invoke();
    }
  }

}

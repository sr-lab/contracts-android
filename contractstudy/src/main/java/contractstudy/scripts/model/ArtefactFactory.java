package contractstudy.scripts.model;

import contractstudy.config.Preferences;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

import static org.apache.commons.io.FileUtils.listFiles;

/**
 * Util class to produce artefacts.
 *
 * @author Kamil Jezek [kamil.jezek@verifalabs.com]
 */
public class ArtefactFactory {

  public static final File INPUT_DIR = new File(Preferences.getDataFolder());
  public static final Collection<File> INPUT_ZIP_FILES = listFiles(INPUT_DIR, new String[]{"zip"},
    true);

  public static final File RESULTS_FOLDER = new File(Preferences.getOutputFolder());
  public static final File RESULTS_USAGE_FOLDER = new File(RESULTS_FOLDER, "/usage");
  public static final File RESULTS_INHERITANCE_FOLDER = new File(RESULTS_FOLDER, "/inheritance");
  public static final File RESULTS_EVOLUTION_FOLDER = new File(RESULTS_FOLDER, "/evolution");

  public static final File USAGE_CONTRACTS_FOLDER = new File(RESULTS_USAGE_FOLDER,"/contracts");
  public static final File USAGE_GINI = new File(RESULTS_USAGE_FOLDER, "/gini.tex");
  public static final File USAGE_GINI_ANNOTATIONS = new File(RESULTS_USAGE_FOLDER, "/gini-annotations.tex");
  public static final File USAGE_GINI_ASSERTIONS = new File(RESULTS_USAGE_FOLDER, "/gini-assertions.tex");
  public static final File USAGE_GINI_APIS = new File(RESULTS_USAGE_FOLDER, "/gini-apis.tex");
  public static final File USAGE_GINI_RUNTIME_EXCEPTIONS = new File(RESULTS_USAGE_FOLDER, "/gini-runtime-exceptions.tex");
  public static final File USAGE_CONTRACTS_TOP_USER_IN_LAST_VERSION = new File(RESULTS_USAGE_FOLDER, "/top-users-in-last-version.tex");
  public static final File USAGE_CONTRACTS_BY_CLASSIFICATION = new File(RESULTS_USAGE_FOLDER, "/contractsbyclassification.tex");
  public static final File USAGE_CONTRACTS_BY_TYPE = new File(RESULTS_USAGE_FOLDER, "/contractsbytype.tex");
  public static final File USAGE_CONTRACTS_BY_TYPE_FIRST_LAST_VERSION = new File(RESULTS_USAGE_FOLDER, "/contractsbytype_first_last.tex");
  public static final File USAGE_DATASET_STATS = new File(RESULTS_USAGE_FOLDER, "/dataset.tex");
  public static final File USAGE_DATASET_ERRORS = new File(RESULTS_USAGE_FOLDER, "/dataset-errors.tex");

  public static final File INHERITANCE_SUPER_CALL_SITE = new File(RESULTS_INHERITANCE_FOLDER, "/superCallSites.csv");
  public static final File INHERITANCE_STRUCTURE_FOLDER = new File(RESULTS_INHERITANCE_FOLDER, "/struct");
  public static final File INHERITANCE_POST_CONDITION_REMOVED = new File(RESULTS_INHERITANCE_FOLDER, "/postconditions_removed_inheritance.txt");
  public static final File INHERITANCE_PRE_CONDITION_ADDED = new File(RESULTS_INHERITANCE_FOLDER, "/preconditions_added_inheritance.txt");
  public static final File INHERITANCE_CONTRACTS_NOT_CLASSIFIED = new File(RESULTS_INHERITANCE_FOLDER, "/contracts_cannot_be_classified_inheritance.txt");
  public static final File INHERITANCE_STATS = new File(RESULTS_INHERITANCE_FOLDER, "/hierarchy.tex");

  public static final File EVOLUTION_STATS = new File(RESULTS_EVOLUTION_FOLDER, "/evolution.tex");
  public static final File EVOLUTION_POST_CONDITION_REMOVED = new File(RESULTS_EVOLUTION_FOLDER, "/postconditions_removed_evolution.txt");
  public static final File EVOLUTION_PRE_CONDITION_ADDED = new File(RESULTS_EVOLUTION_FOLDER, "/preconditions_added_evolution.txt");
  public static final File EVOLUTION_CONTRACTS_NOT_CLASSIFIED = new File(RESULTS_EVOLUTION_FOLDER, "/contracts_cannot_be_classified_evolution.txt");
  public static final File EVOLUTION_VERSION_STATS = new File(RESULTS_EVOLUTION_FOLDER, "/programversion_stats.csv");
  public static final File EVOLUTION_CONTRACTS_ACROSS_VERSIONS = new File(RESULTS_EVOLUTION_FOLDER,  "/contraints_across_versions.csv");
  public static final File EVOLUTION_EVOLUTION_OK = new File(RESULTS_EVOLUTION_FOLDER,  "/evolution_ok.tex");
  public static final File EVOLUTION_EVOLUTION_ERROR = new File(RESULTS_EVOLUTION_FOLDER,  "/evolution_error.tex");

  public static final Collection<File> CONTRACT_FILES = new ArrayList<>();
  public static final Collection<File> STRUCT_FILES = new ArrayList<>();

  /**
   * @return input ZIP files with source code
   */
  public static ExperimentArtefact inputSrcZipFiles() {
    return new FileScanArtefact(INPUT_DIR, INPUT_ZIP_FILES);
  }

  /**
   * @return contracts in Json format.
   */
  public static ExperimentArtefact contracts() {
    return new FileScanArtefact(USAGE_CONTRACTS_FOLDER, CONTRACT_FILES);
  }

  static {
    if (USAGE_CONTRACTS_FOLDER.exists()) {
      CONTRACT_FILES.addAll(FileUtils.listFiles(USAGE_CONTRACTS_FOLDER, new String[]{"json"}, true));
    }
    if (INHERITANCE_STRUCTURE_FOLDER.exists()) {
      STRUCT_FILES.addAll(FileUtils.listFiles(INHERITANCE_STRUCTURE_FOLDER, new String[]{"json"}, true));
    }
  }

  /**
   * @return several tables about contracts usage
   */
  public static ExperimentArtefact contractsUsage() {
    return new FileScanArtefact(USAGE_CONTRACTS_BY_TYPE_FIRST_LAST_VERSION);
  }

  /**
   * @return TeX table with dataset statistics
   */
  public static ExperimentArtefact datasetStatistics() {
    return new FileScanArtefact(USAGE_DATASET_STATS);
  }

  /**
   * @return CSV table with information of super calls.
   */
  public static ExperimentArtefact superCalls() {
    return new FileScanArtefact(INHERITANCE_SUPER_CALL_SITE);
  }

  /**
   * @return class structures (methods, super types) in Json format
   */
  public static ExperimentArtefact classStructure() {
    return new FileScanArtefact(INHERITANCE_STRUCTURE_FOLDER, STRUCT_FILES);
  }

  /**
   * @return Tex table with contracts diff in hierarchy
   */
  public static ExperimentArtefact contractsHierarchyTable() {
    return new FileScanArtefact(INHERITANCE_STATS);
  }

  /**
   * @return TeX table with constrain evolution
   */
  public static ExperimentArtefact contractsEvolutionTable() {
    return new FileScanArtefact(EVOLUTION_STATS);
  }

  /**
   * @return CSV table with program version statistics.
   */
  public static ExperimentArtefact programVersionStatistics() {
    return new FileScanArtefact(EVOLUTION_VERSION_STATS);
  }

  /**
   * @return CSV table with program contracts usage among versions
   */
  public static ExperimentArtefact contractsUsageAcrossVersions() {
    return new FileScanArtefact(EVOLUTION_CONTRACTS_ACROSS_VERSIONS);
  }

}

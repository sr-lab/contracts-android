package contractstudy.scripts;

import com.google.common.base.Preconditions;
import contractstudy.config.Logging;
import contractstudy.constants.constraint.ContractElement;
import contractstudy.model.FindFirstAndLastProgramVersions;
import contractstudy.model.ProgramVersion;
import contractstudy.scripts.model.ArtefactFactory;
import contractstudy.scripts.model.Experiment;
import contractstudy.scripts.model.ExperimentArtefact;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static contractstudy.constants.SetStatsDataKeys.ALL_CONSTRUCTORS;
import static contractstudy.constants.SetStatsDataKeys.ALL_METHODS;
import static contractstudy.constants.SetStatsDataKeys.CLASSES;
import static contractstudy.constants.SetStatsDataKeys.COMPILATION_UNITS;
import static contractstudy.constants.SetStatsDataKeys.LOC;
import static contractstudy.constants.SetStatsDataKeys.PUBLIC_CONSTRUCTORS;
import static contractstudy.constants.SetStatsDataKeys.PUBLIC_METHODS;

/**
 * Script used to analyse how contractual constraints are used by different versions of the same program.
 *
 * @author jens dietrich
 */
public class AnalyseContractUsageAcrossVersions implements Experiment {

  static final File INPUT_DATA_FOLDER = ArtefactFactory.USAGE_CONTRACTS_FOLDER;
  static Logger LOGGER = Logging.getLogger(AnalyseContractUsageAcrossVersions.class);

  public static void main(String[] args) throws Exception {
    new AnalyseContractUsageAcrossVersions().run();
  }

  private static List<ContractElement> readContractElementsFromFiles()
    throws IOException {

    Preconditions.checkState(INPUT_DATA_FOLDER.exists(),
      "Data folder does not exist: " + INPUT_DATA_FOLDER);

    List<ContractElement> contractElements = new ArrayList<>();
    Collection<File> jsonFiles = FileUtils.listFiles(INPUT_DATA_FOLDER, new String[]{"json"}, true);
    for (File json : jsonFiles) {
      String data = FileUtils.readFileToString(json, StandardCharsets.UTF_8);
      JSONArray all = new JSONArray(data);
      all.forEach(e -> {
        ContractElement c = ContractElement.fromJSON((JSONObject) e);
        contractElements.add(c);
      });
    }
    return contractElements;
  }

  private static Map<ProgramVersion, Map<String, Integer>> readMetricsFromFile(File metrics)
    throws Exception {
    LOGGER.info("Reading metrics from " + metrics);
    Map<ProgramVersion, Map<String, Integer>> data = new HashMap<>();
    try (BufferedReader reader = new BufferedReader(new FileReader(metrics))) {
      reader.readLine();
      String line;
      while ((line = reader.readLine()) != null) {
        String[] tokens = line.split(",");
        assert tokens.length == 9;
        Map<String, Integer> map = new HashMap<>();
        ProgramVersion pv = ProgramVersion.getOrCreate(tokens[0], tokens[1]);
        map.put(LOC.getKey(), Integer.parseInt(tokens[2]));
        map.put(COMPILATION_UNITS.getKey(), Integer.parseInt(tokens[3]));
        map.put(CLASSES.getKey(), Integer.parseInt(tokens[4]));
        map.put(ALL_METHODS.getKey(), Integer.parseInt(tokens[5]));
        map.put(ALL_CONSTRUCTORS.getKey(), Integer.parseInt(tokens[6]));
        map.put(PUBLIC_METHODS.getKey(), Integer.parseInt(tokens[7]));
        map.put(PUBLIC_CONSTRUCTORS.getKey(), Integer.parseInt(tokens[8]));
        data.put(pv, map);
      }
    }
    return data;
  }

  public void run() throws Exception {
    File METRICS_FILE = ArtefactFactory.EVOLUTION_VERSION_STATS;

    List<ContractElement> contractElements = readContractElementsFromFiles();

    Map<ProgramVersion, Integer> constraintsInFirstVersions = new HashMap<>();
    Map<ProgramVersion, Integer> constraintsInLastVersions = new HashMap<>();

    // extract the latest versions for cross-referencing
    Pair<Map<String, ProgramVersion>, Map<String, ProgramVersion>> firstAndLatestVersions = FindFirstAndLastProgramVersions.find();
    Map<String, ProgramVersion> firstVersions = firstAndLatestVersions.getLeft();
    Map<String, ProgramVersion> lastVersions = firstAndLatestVersions.getRight();

    for (ContractElement c : contractElements) {
      if (firstVersions.containsValue(c.getProgramVersion())) {
        constraintsInFirstVersions.compute(c.getProgramVersion(), (p, v) -> v == null ? 1 : v + 1);
      } else if (lastVersions.containsValue(c.getProgramVersion())) {
        constraintsInLastVersions.compute(c.getProgramVersion(), (p, v) -> v == null ? 1 : v + 1);
      }
    }

    Map<ProgramVersion, Map<String, Integer>> metrics = readMetricsFromFile(METRICS_FILE);
    outputResultsToCSVFile(firstAndLatestVersions, metrics, constraintsInFirstVersions,
      constraintsInLastVersions);
  }

  public void outputResultsToCSVFile(
    Pair<Map<String, ProgramVersion>, Map<String, ProgramVersion>> firstAndLatestVersions,
    Map<ProgramVersion, Map<String, Integer>> metrics,
    Map<ProgramVersion, Integer> constraintsInFirstVersions,
    Map<ProgramVersion, Integer> constraintsInLastVersions
  ) throws IOException {

    LOGGER.info("Finished contract usage across versions analysis");

    File csv = ArtefactFactory.EVOLUTION_CONTRACTS_ACROSS_VERSIONS;
    char SEP = ',';

    try (PrintStream out = new PrintStream(Files.newOutputStream(csv.toPath()))) {
      out.println(
        "program" + SEP + "version1" + SEP + "methods1" + SEP + "constraints1" + SEP + "version2"
          + SEP + "methods2" + SEP + "constraints2");

      for (String program : firstAndLatestVersions.getLeft().keySet()) {

        ProgramVersion firstVersion = firstAndLatestVersions.getLeft().get(program);
        int methodCountInFirstVersion = computeMethodCountInVersion(metrics, firstVersion);
        int constraintCountInFirstVersion = getConstraintCountInVersion(metrics, firstVersion,
          constraintsInFirstVersions);

        ProgramVersion lastVersion = firstAndLatestVersions.getRight().get(program);
        if (metrics.get(lastVersion) == null) {
          continue; // JFF: FIXME
        }
        int methodCountInLastVersion = computeMethodCountInVersion(metrics, lastVersion);
        int constraintCountInLastVersion = getConstraintCountInVersion(metrics, lastVersion,
          constraintsInLastVersions);

        if (include(firstVersion, lastVersion, metrics, constraintCountInFirstVersion,
          constraintCountInLastVersion)) {
          out.print(program);
          out.print(SEP);
          out.print(firstVersion.getVersion());
          out.print(SEP);
          out.print(methodCountInFirstVersion);
          out.print(SEP);
          out.print(constraintCountInFirstVersion);
          out.print(SEP);
          out.print(lastVersion.getVersion());
          out.print(SEP);
          out.print(methodCountInLastVersion);
          out.print(SEP);
          out.print(constraintCountInLastVersion);
          out.println();
        }
      }
      out.println();
    }
  }

  private int computeMethodCountInVersion(Map<ProgramVersion, Map<String, Integer>> metrics, ProgramVersion version) {
    try {
      int methodCountInVersion = metrics.get(version).get(ALL_METHODS.getKey());
      methodCountInVersion = methodCountInVersion + metrics.get(version).get(ALL_CONSTRUCTORS.getKey());
      return methodCountInVersion;
    } catch (NullPointerException exception) {
      return 0;
    }
  }

  private int getConstraintCountInVersion(Map<ProgramVersion, Map<String, Integer>> metrics,
    ProgramVersion version, Map<ProgramVersion, Integer> constraintsInVersion) {
    Integer tmp = constraintsInVersion.get(version);
    return tmp == null ? 0 : tmp;
  }

  public boolean include(
    ProgramVersion firstProgramVersion,
    ProgramVersion lastProgramVersion,
    Map<ProgramVersion, Map<String, Integer>> metrics,
    int constraintsInFirstVersions,
    int constraintsInLastVersions
  ) {
    return true;
  }

  @Override
  public void invoke() throws Exception {
    if (provides().exists()) {
      LOGGER.info("Skipping already performed experiment: " + provides().getName());
      return;
    }
    AnalyseContractUsageAcrossVersions.main(new String[]{});
  }

  @Override
  public ExperimentArtefact[] requires() {
    return new ExperimentArtefact[]{
      ArtefactFactory.contracts(),
      ArtefactFactory.programVersionStatistics()
    };
  }

  @Override
  public ExperimentArtefact provides() {
    return ArtefactFactory.contractsUsageAcrossVersions();
  }
}

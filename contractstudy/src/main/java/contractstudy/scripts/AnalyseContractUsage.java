package contractstudy.scripts;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import contractstudy.config.Logging;
import contractstudy.constants.constraint.ConstraintCategory;
import contractstudy.constants.constraint.ConstraintClassification;
import contractstudy.constants.constraint.ConstraintGroup;
import contractstudy.constants.constraint.ConstraintType;
import contractstudy.constants.constraint.ContractElement;
import contractstudy.evolution.model.diffRules.Utils;
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

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static contractstudy.evolution.model.diffRules.Utils.NF;

/**
 * Script used to analyse how contracts are used by programs. This script counts the contracts
 * elements.
 *
 * @author jens dietrich
 */
public class AnalyseContractUsage implements Experiment {

  final static File INPUT_DATA_FOLDER = ArtefactFactory.USAGE_CONTRACTS_FOLDER;
  final static File OUTPUT_GINI_FOLDER = ArtefactFactory.USAGE_GINI_FOLDER;
  static Logger LOGGER = Logging.getLogger(AnalyseContractUsage.class);

  public static void main(String[] args) throws Exception {

    List<ContractElement> contractElements = readContractsFromFile();

    Multimap<ConstraintGroup, String> programsUsingConstraintGroups = HashMultimap.create();
    Multimap<ConstraintClassification, String> programsUsingConstraintClassifications = HashMultimap.create();

    Map<ConstraintGroup, Integer> constraintsByGroup = getConstraintsByGroup();
    Map<ConstraintGroup, Integer> constraintsByGroupLV = new LinkedHashMap<>(
      getConstraintsByGroup());

    Map<ConstraintCategory, Map<String, Integer>> constraintsByProgramLatestVersionAndCategory = getConstraintByProgramLatestVersionAndCategory();

    Map<ConstraintClassification, Integer> constraintsByClassification = getConstraintsByClassification();
    Map<ConstraintClassification, Integer> constraintsByClassificationLV = new LinkedHashMap<>(
      constraintsByClassification);

    // extract the latest versions for cross-referencing
    Pair<Map<String, ProgramVersion>, Map<String, ProgramVersion>> firstAndLatestVersions = FindFirstAndLastProgramVersions
      .find();
    Collection<ProgramVersion> latestVersions = firstAndLatestVersions.getRight().values();
    // use linked hashmaps to fix order of keys
    Map<String, Integer> constraintsByProgram = new HashMap<>();
    // LatestVersion only.
    Map<String, Integer> constraintsByProgramLV = new HashMap<>();

    for (ContractElement c : contractElements) {
      ConstraintType type = c.getKind();
      ConstraintGroup group = type.getGroup();
      ConstraintClassification classification = c.getClassification();
      String program = c.getProgramVersion().getName();

      constraintsByGroup.compute(group, (g, i) -> i == null ? 1 : i + 1);
      constraintsByClassification.compute(classification, (g, i) -> i == null ? 1 : i + 1);
      constraintsByProgram.compute(program, (g, i) -> i == null ? 1 : i + 1);

      if (latestVersions.contains(c.getProgramVersion())) {
        constraintsByGroupLV.compute(group, (g, i) -> i == null ? 1 : i + 1);
        constraintsByClassificationLV.compute(classification, (g, i) -> i == null ? 1 : i + 1);
        constraintsByProgramLV.compute(program, (g, i) -> i == null ? 1 : i + 1);
        Map<String, Integer> data = constraintsByProgramLatestVersionAndCategory.get(
          c.getKind().getGroup().getCategory());
        data.compute(program, (g, i) -> i == null ? 1 : i + 1);
      }
      programsUsingConstraintGroups.put(group, program);
      programsUsingConstraintClassifications.put(classification, program);
    }

    Collection<String> topProgramsUsingContracts = getTopProgramsUsingContracts(
      constraintsByProgram);
    Collection<String> topProgramsUsingContractsInLV = getTopProgramsUsingContractsInLatestVersion(
      constraintsByProgramLV);

    double gini4AllConstraints = computeGini(constraintsByProgramLV);
    double gini4Assertions = computeGini(
      constraintsByProgramLatestVersionAndCategory.get(ConstraintCategory.ASSERTION));
    double gini4APIs = computeGini(
      constraintsByProgramLatestVersionAndCategory.get(ConstraintCategory.API));
    double gini4RTExc = computeGini(
      constraintsByProgramLatestVersionAndCategory.get(ConstraintCategory.RUNTIME_EXCEPTION));
    double gini4Annotations = computeGini(
      constraintsByProgramLatestVersionAndCategory.get(ConstraintCategory.ANNOTATION));
    double gini4Others = computeGini(
      constraintsByProgramLatestVersionAndCategory.get(ConstraintCategory.OTHERS));

    LOGGER.info("Finished contract usage analysis");

    OUTPUT_GINI_FOLDER.mkdir();

    outputGiniToConsole(gini4AllConstraints, gini4Assertions, gini4APIs, gini4RTExc,
      gini4Annotations, gini4Others);
    outputContractConstraintsToConsole(constraintsByGroup, constraintsByGroupLV,
      constraintsByClassification, constraintsByClassificationLV, constraintsByProgram,
      constraintsByProgramLV, topProgramsUsingContracts, topProgramsUsingContractsInLV);

    LOGGER.info("Rendering output to latex");

    outputConstraintsByGroupToLatex(constraintsByGroup, constraintsByGroupLV,
      programsUsingConstraintGroups);
    outputConstraintsByGroupInFirstAndLastVersionToLatex(constraintsByGroup,
      constraintsByGroupLV, programsUsingConstraintGroups);
    outputConstraintsByClassificationToLatex(constraintsByClassification,
      constraintsByClassificationLV, programsUsingConstraintClassifications);
    outputGiniToLatex(gini4AllConstraints, gini4Assertions, gini4APIs, gini4RTExc,
      gini4Annotations, gini4Others);
    outputCategoriesInfoToLatex(constraintsByProgramLatestVersionAndCategory);
  }

  private static List<ContractElement> readContractsFromFile() throws IOException {
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

  private static Map<ConstraintGroup, Integer> getConstraintsByGroup() {
    Map<ConstraintGroup, Integer> constraintsByGroup = new LinkedHashMap<>();
    // set keys to fix order
    constraintsByGroup.put(ConstraintGroup.ASSERTION, 0);
    constraintsByGroup.put(ConstraintGroup.CONDITIONAL_RUNTIME_EXCEPTION, 0);
    constraintsByGroup.put(ConstraintGroup.UNCONDITIONAL_RUNTIME_EXCEPTION, 0);
    constraintsByGroup.put(ConstraintGroup.CAPI_GUAVA, 0);
    constraintsByGroup.put(ConstraintGroup.CAPI_SPRING_ASSERT, 0);
    constraintsByGroup.put(ConstraintGroup.CAPI_COMMONS_VALIDATE, 0);
    constraintsByGroup.put(ConstraintGroup.ANNO_JSR303, 0);
    constraintsByGroup.put(ConstraintGroup.ANNO_JSR305, 0);
    constraintsByGroup.put(ConstraintGroup.ANNO_Android, 0);
    constraintsByGroup.put(ConstraintGroup.KOTLIN_CONTRACTS, 0);
    return constraintsByGroup;
  }

  private static Map<ConstraintCategory, Map<String, Integer>> getConstraintByProgramLatestVersionAndCategory() {
    Map<ConstraintCategory, Map<String, Integer>> constraints = new HashMap<>();
    constraints.put(ConstraintCategory.API, new HashMap<>());
    constraints.put(ConstraintCategory.ANNOTATION, new HashMap<>());
    constraints.put(ConstraintCategory.ASSERTION, new HashMap<>());
    constraints.put(ConstraintCategory.RUNTIME_EXCEPTION, new HashMap<>());
    constraints.put(ConstraintCategory.OTHERS, new HashMap<>());
    return constraints;
  }

  private static Map<ConstraintClassification, Integer> getConstraintsByClassification() {
    Map<ConstraintClassification, Integer> constraintsByClassification = new LinkedHashMap<>();
    constraintsByClassification.put(ConstraintClassification.PRECONDITION, 0);
    constraintsByClassification.put(ConstraintClassification.POSTCONDITION, 0);
    constraintsByClassification.put(ConstraintClassification.INVARIANT, 0);
    constraintsByClassification.put(ConstraintClassification.ANY, 0);
    return constraintsByClassification;
  }

  private static Collection<String> getTopProgramsUsingContracts(
    Map<String, Integer> constraintsByProgram) {
    return constraintsByProgram.keySet().stream()
      .sorted((s1, s2) -> constraintsByProgram.get(s2) - constraintsByProgram.get(s1)).limit(10)
      .collect(Collectors.toList());
  }

  private static Collection<String> getTopProgramsUsingContractsInLatestVersion(
    Map<String, Integer> constraintsByProgramLatestVersion) {
    return constraintsByProgramLatestVersion.keySet().stream()
      .sorted((s1, s2) -> constraintsByProgramLatestVersion.get(s2)
        - constraintsByProgramLatestVersion.get(s1)).limit(10)
      .collect(Collectors.toList());
  }

  private static double computeGini(Map<String, Integer> constraintsByProgramLVs) {
    Integer[] vals = constraintsByProgramLVs.values().stream().sorted().toArray(Integer[]::new);
    return jct.util.Gini.compute(vals, true);
  }

  private static void outputGiniToConsole(
    double gini4AllConstraints,
    double gini4Assertions,
    double gini4APIs,
    double gini4RTExc,
    double gini4Annotations,
    double gini4Others
  ) {
    LOGGER.info(
      "\tGINI for distribution of contracts amongst latest version is " + gini4AllConstraints);
    LOGGER.info("\tGINI assertions only " + gini4Assertions);
    LOGGER.info("\tGINI annotations only " + gini4Annotations);
    LOGGER.info("\tGINI apis only " + gini4APIs);
    LOGGER.info("\tGINI rt exceptions only " + gini4RTExc);
    LOGGER.info("\tGINI others only " + gini4Others);
  }

  private static void outputContractConstraintsToConsole(
    Map<ConstraintGroup, Integer> constraintsByGroup,
    Map<ConstraintGroup, Integer> constraintsByGroupLV,
    Map<ConstraintClassification, Integer> constraintsByClassification,
    Map<ConstraintClassification, Integer> constraintsByClassificationLV,
    Map<String, Integer> constraintsByProgram, Map<String, Integer> constraintsByProgramLV,
    Collection<String> topProgramsUsingContracts,
    Collection<String> topProgramsUsingContractsInLV
  ) {
    LOGGER.info("Contract groups used (name,count):");
    for (Map.Entry<ConstraintGroup, Integer> entry : constraintsByGroup.entrySet()) {
      LOGGER.info("\t" + entry.getKey() + " : " + entry.getValue());
    }
    LOGGER.info("Contract groups used [in latest versions only!] (name,count):");
    for (Map.Entry<ConstraintGroup, Integer> entry : constraintsByGroupLV.entrySet()) {
      LOGGER.info("\t" + entry.getKey() + " : " + entry.getValue());
    }
    LOGGER.info("Contract classifications used (name,count):");
    for (Map.Entry<ConstraintClassification, Integer> entry : constraintsByClassification.entrySet()) {
      LOGGER.info("\t" + entry.getKey() + " : " + entry.getValue());
    }
    LOGGER.info("Contract classifications used [in latest versions only!] (name,count):");
    for (Map.Entry<ConstraintClassification, Integer> entry : constraintsByClassificationLV.entrySet()) {
      LOGGER.info("\t" + entry.getKey() + " : " + entry.getValue());
    }
    LOGGER.info("Top usage by program (name,count):");
    for (String program : topProgramsUsingContracts) {
      LOGGER.info("\t" + program + " : " + constraintsByProgram.get(program));
    }

    LOGGER.info("Top usage by program [in latest versions only!] (name,count):");
    for (String program : topProgramsUsingContractsInLV) {
      LOGGER.info("\t" + program + " : " + constraintsByProgramLV.get(program));
    }
  }

  private static void outputConstraintsByGroupToLatex(
    Map<ConstraintGroup, Integer> constraintsByGroup,
    Map<ConstraintGroup, Integer> constraintsByGroupLV,
    Multimap<ConstraintGroup, String> programsUsingConstraintGroups
  )
    throws IOException {
    File latex = ArtefactFactory.USAGE_CONTRACTS_BY_TYPE;
    try (PrintStream out = new PrintStream(Files.newOutputStream(latex.toPath()))) {
      out.println("% TABLE GENERATED BY " + AnalyseContractUsage.class.getName());
      out.println("% TIMESTAMP:   " + new Date());
      out.println("\\begin{table}[]");
      out.println("\\centering");
      out.println("\\caption{Contract elements by type}");
      out.println("\\label{tab:contractsbytype}");
      out.println("\\begin{tabular}{|p{5cm}|p{3cm}|r|r|r|} \\hline");
      out.println(
        "   type & category & \\parbox{1.2cm}{contracts\\ (all ver.)}  & \\parbox{1.2cm}{contracts\\ (latest)} & pro\\-grams \\\\ \\hline");
      for (Map.Entry<ConstraintGroup, Integer> entry : constraintsByGroup.entrySet()) {
        out.print("\t");
        out.print(entry.getKey().getShortName() + " &  ");
        out.print(entry.getKey().getCategory().getName() + " &  ");
        out.print(NF.format(entry.getValue()) + " &  ");
        out.print(NF.format(constraintsByGroupLV.get(entry.getKey())) + " &  ");
        out.println(NF.format(programsUsingConstraintGroups.get(entry.getKey()).size()) + " \\\\ ");
      }
      out.println("\\hline");
      out.println("\\end{tabular}");
      out.println("\\end{table}");
    }
  }

  private static void outputConstraintsByGroupInFirstAndLastVersionToLatex(
    Map<ConstraintGroup, Integer> constraintsByGroup,
    Map<ConstraintGroup, Integer> constraintsByGroupLV,
    Multimap<ConstraintGroup, String> programsUsingConstraintGroups
  ) throws IOException {
    File latex = ArtefactFactory.USAGE_CONTRACTS_BY_TYPE_FIRST_LAST_VERSION;
    try (PrintStream out = new PrintStream(Files.newOutputStream(latex.toPath()))) {
      out.println("% TABLE GENERATED BY " + AnalyseContractUsage.class.getName());
      out.println("% TIMESTAMP:   " + new Date());
      out.println("\\begin{table}[]");
      out.println("\\centering");
      out.println("\\caption{Contract elements by type}");
      out.println("\\label{tab:contractsbytype}");
      out.println("\\begin{tabular}{|p{5cm}|p{3cm}|r|r|r|} \\hline");
      out.println(
        "   type & category & \\parbox{1.2cm}{contracts\\ (1st ver.)}  & \\parbox{1.2cm}{contracts\\ (latest)} & pro\\-grams \\\\ \\hline");
      for (Map.Entry<ConstraintGroup, Integer> entry : constraintsByGroup.entrySet()) {
        out.print("\t");
        out.print(entry.getKey().getShortName() + " &  ");
        out.print(entry.getKey().getCategory().getName() + " &  ");
        int total_contracts = entry.getValue();
        int last_version = constraintsByGroupLV.get(entry.getKey());
        int first_version = total_contracts - last_version;

        double increase_rate = 0;
        boolean show_increase_rate = true;
        if (last_version > first_version && first_version != 0) {
          increase_rate = (double) last_version / first_version;
        } else if (first_version > last_version && last_version != 0) {
          increase_rate = -((double) first_version / last_version);
        }
        if (first_version == 0 || last_version == 0) {
          show_increase_rate = false;
        }

        if (total_contracts >= last_version) {
          out.print(NF.format(first_version) + " &  ");
          if (show_increase_rate) {
            if (increase_rate != 0) {
              out.print(
                NF.format(constraintsByGroupLV.get(entry.getKey())) + " ( " + String.format("%.2f",
                  increase_rate) + "x )" + " &  ");
            } else {
              out.print(
                NF.format(constraintsByGroupLV.get(entry.getKey())) + " ( \\approx )" + " &  ");
            }
          } else {
            out.print(NF.format(constraintsByGroupLV.get(entry.getKey())) + " &  ");
          }
        } else {
          out.print(NF.format(entry.getValue()) + " &  ");
          out.print(NF.format(constraintsByGroupLV.get(entry.getKey())) + " &  ");
        }
        out.println(NF.format(programsUsingConstraintGroups.get(entry.getKey()).size()) + " \\\\ ");
      }
      out.println("\\hline");
      out.println("\\end{tabular}");
      out.println("\\end{table}");
    }
  }

  private static void outputConstraintsByClassificationToLatex(
    Map<ConstraintClassification, Integer> constraintsByClassification,
    Map<ConstraintClassification, Integer> constraintsByClassificationLV,
    Multimap<ConstraintClassification, String> programsUsingConstraintClassifications
  )
    throws IOException {
    File latex = ArtefactFactory.USAGE_CONTRACTS_BY_CLASSIFICATION;
    try (PrintStream out = new PrintStream(Files.newOutputStream(latex.toPath()))) {
      out.println("% TABLE GENERATED BY " + AnalyseContractUsage.class.getName());
      out.println("% TIMESTAMP:   " + new Date());
      out.println("\\begin{table}[]");
      out.println("\\centering");
      out.println("\\caption{Contracts by classification}");
      out.println("\\label{tab:contractsbyclassification}");
      out.println("\\begin{tabular}{|p{2.1cm}|r|r|r|} \\hline");
      out.println(
        "   classification & \\parbox{1.2cm}{contracts\\ (all ver.)} & \\parbox{1.2cm}{contracts\\ (latest)} & programs \\\\ \\hline");
      for (Map.Entry<ConstraintClassification, Integer> entry : constraintsByClassification.entrySet()) {
        out.print("\t");
        out.print(entry.getKey().getName() + " &  ");
        out.println(NF.format(entry.getValue()) + " & ");
        out.println(NF.format(constraintsByClassificationLV.get(entry.getKey())) + " & ");
        out.println(
          NF.format(programsUsingConstraintClassifications.get(entry.getKey()).size()) + " \\\\ ");
      }
      out.println("\\hline");
      out.println("\\end{tabular}");
      out.println("\\end{table}");
    }
  }

  private static void outputGiniToLatex(
    double gini4AllConstraints,
    double gini4Assertions,
    double gini4APIs,
    double gini4RTExc,
    double gini4Annotations,
    double gini4Others
  ) throws Exception {
    File latex = ArtefactFactory.USAGE_GINI;
    try (PrintStream out = new PrintStream(Files.newOutputStream(latex.toPath()))) {
      out.print(Math.round(gini4AllConstraints * 100.0) / 100.0);
    }
    exportGini(gini4AllConstraints, ArtefactFactory.USAGE_GINI);
    exportGini(gini4Annotations, ArtefactFactory.USAGE_GINI_ANNOTATIONS);
    exportGini(gini4Assertions, ArtefactFactory.USAGE_GINI_ASSERTIONS);
    exportGini(gini4APIs, ArtefactFactory.USAGE_GINI_APIS);
    exportGini(gini4RTExc, ArtefactFactory.USAGE_GINI_RUNTIME_EXCEPTIONS);
    exportGini(gini4Others, ArtefactFactory.USAGE_GINI_OTHERS);
  }

  private static void exportGini(double value, File outputFile) throws Exception {
    try (PrintStream out = new PrintStream(Files.newOutputStream(outputFile.toPath()))) {
      out.print(Math.round(value * 100.0) / 100.0);
    }
    LOGGER.info("Gini value written to " + outputFile.getAbsolutePath());
  }

  private static void outputCategoriesInfoToLatex(
    Map<ConstraintCategory, Map<String, Integer>> constraintsByProgramLatestVersionAndCategory
  )
    throws IOException {
    File latex = ArtefactFactory.USAGE_CONTRACTS_TOP_USER_IN_LAST_VERSION;
    try (PrintStream out = new PrintStream(Files.newOutputStream(latex.toPath()))) {
      out.println("% TABLE GENERATED BY " + CollectDatasetStats.class.getName());
      out.println("% TIMESTAMP:   " + new Date());
      out.println("\\begin{table}[]");
      out.println("\\centering");
      out.println("\\caption{Top programs using contracts (latest versions only)}");
      out.println("\\label{tab:topusers}");
      out.println("\\begin{tabular}{|p{2.0cm}|p{6.0cm}|} \\hline");
      out.println("   category & programs \\\\ \\hline");
      for (ConstraintCategory category : ConstraintCategory.values()) {
        Map<String, Integer> counts = constraintsByProgramLatestVersionAndCategory.get(category);
        List<Integer> counts2 = new ArrayList<>();
        List<Map.Entry<String, Integer>> entries = counts.entrySet().stream()
          .filter(e -> e.getValue() > 0)
          .sorted((e1, e2) -> e2.getValue() - e1.getValue()).collect(Collectors.toList());
        String s1 = "values:";
        for (Map.Entry<String, Integer> e : entries) {
          s1 = s1 + " " + e.getValue();
        }
        String s2 = "progr.:";
        for (Map.Entry<String, Integer> e : entries) {
          s2 = s2 + " " + e.getKey();
        }
        LOGGER.info("Distribution of constraints of category " + category + ":");
        LOGGER.info("\t" + s1);
        LOGGER.info("\t" + s2);

        // append latex output
        List<Map.Entry<String, Integer>> topEntries = entries.stream().limit(10)
          .collect(Collectors.toList());
        out.print(category.getName() + " & ");
        String s = "";
        for (Map.Entry<String, Integer> e : topEntries) {
          // out.print(category.getName());
          if (s.length() > 0)
          // replace _ with - so that it doesn't cause LaTeX errors
          {
            s = s.replace('_', '-');
          }
          s = s + ", ";
          s = s + e.getKey() + " (" + Utils.NF.format(e.getValue()) + ")";
        }
        out.println(s + " \\\\ ");
      }
      out.println("\\hline");
      out.println("\\end{tabular}");
      out.println("\\end{table}");
      LOGGER.info("Top user data written to " + latex.getAbsolutePath());
    }
  }

  @Override
  public void invoke() throws Exception {
    if (provides().exists()) {
      LOGGER.info("Skipping already performed experiment: " + provides().getName());
      return;
    }
    AnalyseContractUsage.main(new String[]{});
  }

  @Override
  public ExperimentArtefact[] requires() {
    return new ExperimentArtefact[]{ArtefactFactory.contracts()};
  }

  @Override
  public ExperimentArtefact provides() {
    return ArtefactFactory.contractsUsage();
  }

}

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
import contractstudy.utils.LanguageUtils.Language;
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
 * Script used to analyse how contracts are used by programs. This script counts the contracts elements.
 *
 * @author jens dietrich
 */
public class AnalyseContractUsage implements Experiment {

  final static File INPUT_DATA_FOLDER = ArtefactFactory.USAGE_CONTRACTS_FOLDER;
  static Logger LOGGER = Logging.getLogger(AnalyseContractUsage.class);

  static Map<ConstraintGroup, Integer> constraintsByGroup = ConstraintGroup.getListOfConstraintsGroup();
  static Map<ConstraintGroup, Integer> constraintsByGroupJava = ConstraintGroup.getListOfConstraintsGroup();
  static Map<ConstraintGroup, Integer> constraintsByGroupKotlin = ConstraintGroup.getListOfConstraintsGroup();
  static Map<ConstraintGroup, Integer> constraintsByGroupLV = new LinkedHashMap<>(
    ConstraintGroup.getListOfConstraintsGroup());
  static Map<ConstraintGroup, Integer> constraintsByGroupLVJava = new LinkedHashMap<>(
    ConstraintGroup.getListOfConstraintsGroup());
  static Map<ConstraintGroup, Integer> constraintsByGroupLVKotlin = new LinkedHashMap<>(
    ConstraintGroup.getListOfConstraintsGroup());

  static Map<ConstraintClassification, Integer> constraintsByClassification = ConstraintClassification.getConstraintClassificationList();
  static Map<ConstraintClassification, Integer> constraintsByClassificationJava = ConstraintClassification.getConstraintClassificationList();
  static Map<ConstraintClassification, Integer> constraintsByClassificationKotlin = ConstraintClassification.getConstraintClassificationList();
  static Map<ConstraintClassification, Integer> constraintsByClassificationLV = new LinkedHashMap<>(
    constraintsByClassification);
  static Map<ConstraintClassification, Integer> constraintsByClassificationLVJava = new LinkedHashMap<>(
    constraintsByClassification);
  static Map<ConstraintClassification, Integer> constraintsByClassificationLVKotlin = new LinkedHashMap<>(
    constraintsByClassification);

  static Multimap<ConstraintGroup, String> programsUsingConstraintGroups = HashMultimap.create();
  static Multimap<ConstraintGroup, String> programsUsingConstraintGroupsJava = HashMultimap.create();
  static Multimap<ConstraintGroup, String> programsUsingConstraintGroupsKotlin = HashMultimap.create();

  static Multimap<ConstraintClassification, String> programsUsingConstraintClassifications = HashMultimap.create();
  static Multimap<ConstraintClassification, String> programsUsingConstraintClassificationsJava = HashMultimap.create();
  static Multimap<ConstraintClassification, String> programsUsingConstraintClassificationsKotlin = HashMultimap.create();

  static Map<String, Integer> constraintsByProgramLV = new HashMap<>();
  static Map<String, Integer> constraintsByProgramLVJava = new HashMap<>();
  static Map<String, Integer> constraintsByProgramLVKotlin = new HashMap<>();
  static Map<ConstraintCategory, Map<String, Integer>> constraintsByCategoryInLV = ConstraintCategory.getConstraintCategoryListForProgramVersion();
  static Map<ConstraintCategory, Map<String, Integer>> constraintsByCategoryInLVJava = ConstraintCategory.getConstraintCategoryListForProgramVersion();
  static Map<ConstraintCategory, Map<String, Integer>> constraintsByCategoryInLVKotlin = ConstraintCategory.getConstraintCategoryListForProgramVersion();

  public static void main(String[] args) throws Exception {

    List<ContractElement> contractElements = readContractsFromFile();

    Pair<Map<String, ProgramVersion>, Map<String, ProgramVersion>> firstAndLatestVersions = FindFirstAndLastProgramVersions.find();
    Collection<ProgramVersion> latestVersions = firstAndLatestVersions.getRight().values();
    Map<String, Integer> constraintsByProgram = new HashMap<>();

    for (ContractElement c : contractElements) {

      ConstraintType type = c.getKind();
      ConstraintGroup group = type.getGroup();
      ConstraintClassification classification = c.getClassification();
      String program = c.getProgramVersion().getName();
      Language fileLanguage = c.getFileLanguage();

      programsUsingConstraintGroups.put(group, program);
      constraintsByProgram.compute(program, (g, i) -> i == null ? 1 : i + 1);
      constraintsByClassification.compute(classification, (g, i) -> i == null ? 1 : i + 1);
      constraintsByGroup.compute(group, (g, i) -> i == null ? 1 : i + 1);

      if (fileLanguage == Language.JAVA) {
        constraintsByClassificationJava.compute(classification, (g, i) -> i == null ? 1 : i + 1);
        constraintsByGroupJava.compute(group, (g, i) -> i == null ? 1 : i + 1);
        programsUsingConstraintGroupsJava.put(group, program);
      } else if (fileLanguage == Language.KOTLIN) {
        constraintsByClassificationKotlin.compute(classification, (g, i) -> i == null ? 1 : i + 1);
        constraintsByGroupKotlin.compute(group, (g, i) -> i == null ? 1 : i + 1);
        programsUsingConstraintGroupsKotlin.put(group, program);
      }

      if (latestVersions.contains(c.getProgramVersion())) {

        constraintsByProgramLV.compute(program, (g, i) -> i == null ? 1 : i + 1);
        constraintsByClassificationLV.compute(classification, (g, i) -> i == null ? 1 : i + 1);
        constraintsByGroupLV.compute(group, (g, i) -> i == null ? 1 : i + 1);
        programsUsingConstraintClassifications.put(classification, program);

        Map<String, Integer> data = constraintsByCategoryInLV.get(c.getKind().getGroup().getCategory());
        data.compute(program, (g, i) -> i == null ? 1 : i + 1);

        if (fileLanguage == Language.JAVA) {
          constraintsByProgramLVJava.compute(program, (g, i) -> i == null ? 1 : i + 1);
          constraintsByClassificationLVJava.compute(classification, (g, i) -> i == null ? 1 : i + 1);
          constraintsByGroupLVJava.compute(group, (g, i) -> i == null ? 1 : i + 1);
          programsUsingConstraintClassificationsJava.put(classification, program);

          Map<String, Integer> dataJava = constraintsByCategoryInLVJava.get(c.getKind().getGroup().getCategory());
          dataJava.compute(program, (g, i) -> i == null ? 1 : i + 1);

        } else if (fileLanguage == Language.KOTLIN) {
          constraintsByProgramLVKotlin.compute(program, (g, i) -> i == null ? 1 : i + 1);
          constraintsByClassificationLVKotlin.compute(classification, (g, i) -> i == null ? 1 : i + 1);
          constraintsByGroupLVKotlin.compute(group, (g, i) -> i == null ? 1 : i + 1);
          programsUsingConstraintClassificationsKotlin.put(classification, program);

          Map<String, Integer> dataJavaKotlin = constraintsByCategoryInLVKotlin.get(c.getKind().getGroup().getCategory());
          dataJavaKotlin.compute(program, (g, i) -> i == null ? 1 : i + 1);
        }
      }
    }

    LOGGER.info("Finished contract usage analysis");
    LOGGER.info("Rendering output to latex");

    outputConstraintsByGroupToLatex();
    outputConstraintsByGroupInFirstAndLastVersionToLatex();
    outputConstraintsByClassificationToLatex();
    computeGiniAndOutputToLatex();
    outputCategoriesInfoToLatex(constraintsByCategoryInLV);
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

  private static double computeGini(Map<String, Integer> constraintsByProgramLVs) {
    Integer[] vals = constraintsByProgramLVs.values().stream().sorted().toArray(Integer[]::new);
    return jct.util.Gini.compute(vals, true);
  }

  private static void outputConstraintsByGroupToLatex() throws IOException {
    File latex = ArtefactFactory.USAGE_CONTRACTS_BY_GROUP;
    try (PrintStream out = new PrintStream(Files.newOutputStream(latex.toPath()))) {
      out.println("% TABLE GENERATED BY " + AnalyseContractUsage.class.getName());
      out.println("% TIMESTAMP:   " + new Date());
      out.println("\\begin{table}[]");
      out.println("\\centering");
      out.println("\\caption{Contract elements by type}");
      out.println("\\label{tab:contractsbytype}");
      out.println("\\begin{tabular}{| c | c | c | c | c | c | c | c |} \\hline");
      out.println("type & category & \\multicolumn{2}{ c |}{contracts (all ver.)} & "
        + "\\multicolumn{2}{ c |}{contracts (latest)} & "
        + "\\multicolumn{2}{ c |}{contracts (programs)} \\\\ \n"
        + "\\cline{3-8}\n"
        + "& & Java & Kotlin & Java & Kotlin & Java & Kotlin \\\\\n"
        + "\\hline");
      for (Map.Entry<ConstraintGroup, Integer> entry : constraintsByGroup.entrySet()) {
        System.out.println(entry.getKey());
        out.print("\t");
        out.print(entry.getKey().getShortName() + " & ");
        out.print(entry.getKey().getCategory().getName() + " & ");
        out.print(NF.format(constraintsByGroupJava.get(entry.getKey())) + " & ");
        out.print(NF.format(constraintsByGroupKotlin.get(entry.getKey())) + " & ");
        out.print(NF.format(constraintsByGroupLVJava.get(entry.getKey())) + " & ");
        out.print(NF.format(constraintsByGroupLVKotlin.get(entry.getKey())) + " & ");
        out.print(NF.format(programsUsingConstraintGroupsJava.get(entry.getKey()).size()) + " & ");
        out.println(
          NF.format(programsUsingConstraintGroupsKotlin.get(entry.getKey()).size()) + " \\\\ ");
      }
      out.println("\\hline");
      out.println("\\end{tabular}");
      out.println("\\end{table}");
    }
  }

  private static void outputConstraintsByGroupInFirstAndLastVersionToLatex() throws IOException {
    File latex = ArtefactFactory.USAGE_CONTRACTS_BY_GROUP_FIRST_LAST_VERSION;
    try (PrintStream out = new PrintStream(Files.newOutputStream(latex.toPath()))) {
      out.println("% TABLE GENERATED BY " + AnalyseContractUsage.class.getName());
      out.println("% TIMESTAMP:   " + new Date());
      out.println("\\begin{table}[]");
      out.println("\\centering");
      out.println("\\caption{Contract elements by type}");
      out.println("\\label{tab:contractsbytype}");
      out.println("\\begin{tabular}{| c | c | c | c | c | c | c | c |} \\hline");
      out.println("type & category & \\multicolumn{2}{ c |}{contracts (1st vers.)} & "
        + "\\multicolumn{2}{ c |}{contracts (latest vers.)} & "
        + "\\multicolumn{2}{ c |}{programs} \\\\ \n"
        + "\\cline{3-8}\n"
        + "& & Java & Kotlin & Java & Kotlin & Java & Kotlin \\\\\n"
        + "\\hline");
      for (Map.Entry<ConstraintGroup, Integer> entry : constraintsByGroup.entrySet()) {
        out.print("\t");
        out.print(entry.getKey().getShortName() + " & ");
        out.print(entry.getKey().getCategory().getName() + " & ");
        out.print(NF.format(constraintsByGroupJava.get(entry.getKey()) - constraintsByGroupLVJava.get(entry.getKey())) + " & ");
        out.print(NF.format(constraintsByGroupKotlin.get(entry.getKey()) - constraintsByGroupLVKotlin.get( entry.getKey())) + " & ");
        out.print(NF.format(constraintsByGroupLVJava.get(entry.getKey())) + " & ");
        out.print(NF.format(constraintsByGroupLVKotlin.get(entry.getKey())) + " & ");
        out.print(NF.format(programsUsingConstraintGroupsJava.get(entry.getKey()).size()) + " & ");
        out.println(
          NF.format(programsUsingConstraintGroupsKotlin.get(entry.getKey()).size()) + " \\\\ ");
      }
      out.println("\\hline");
      out.println("\\end{tabular}");
      out.println("\\end{table}");
    }
  }

  private static void outputConstraintsByClassificationToLatex() throws IOException {
    File latex = ArtefactFactory.USAGE_CONTRACTS_BY_CLASSIFICATION;
    try (PrintStream out = new PrintStream(Files.newOutputStream(latex.toPath()))) {
      out.println("% TABLE GENERATED BY " + AnalyseContractUsage.class.getName());
      out.println("% TIMESTAMP:   " + new Date());
      out.println("\\begin{table}[]");
      out.println("\\centering");
      out.println("\\caption{Contracts by classification}");
      out.println("\\label{tab:contractsbyclassification}");
      out.println("\\begin{tabular}{| c | c | c | c | c | c | c |} \\hline");
      out.println("type & \\multicolumn{2}{ c |}{contracts (all ver.)} & "
        + "\\multicolumn{2}{ c |}{contracts (latest)} & "
        + "\\multicolumn{2}{ c |}{contracts (programs)} \\\\ \n"
        + "\\cline{2-7}\n"
        + "& Java & Kotlin & Java & Kotlin & Java & Kotlin \\\\\n"
        + "\\hline");
      for (Map.Entry<ConstraintClassification, Integer> entry : constraintsByClassification.entrySet()) {
        out.print("\t");
        out.print(entry.getKey().getName() + " & ");
        out.print(NF.format(constraintsByClassificationJava.get(entry.getKey())) + " & ");
        out.print(NF.format(constraintsByClassificationKotlin.get(entry.getKey())) + " & ");
        out.print(NF.format(constraintsByClassificationLVJava.get(entry.getKey())) + " & ");
        out.print(NF.format(constraintsByClassificationLVKotlin.get(entry.getKey())) + " & ");
        try {
          out.print(NF.format(programsUsingConstraintClassificationsJava.get(entry.getKey()).size())
            + " & ");
        } catch (IllegalArgumentException e) {
          out.print(NF.format(0) + " & ");
        }
        try {
          out.println(
            NF.format(programsUsingConstraintClassificationsKotlin.get(entry.getKey()).size())
              + " \\\\ ");
        } catch (IllegalArgumentException e) {
          out.println(NF.format(0) + " \\\\ ");
        }
      }
      out.println("\\hline");
      out.println("\\end{tabular}");
      out.println("\\end{table}");
    }
  }

  private static void computeGiniAndOutputToLatex() throws IOException {
    double gini4AllConstraints = computeGini(constraintsByProgramLV);
    double gini4Assertions = computeGini(
      constraintsByCategoryInLV.get(ConstraintCategory.ASSERTION));
    double gini4APIs = computeGini(
      constraintsByCategoryInLV.get(ConstraintCategory.API));
    double gini4RTExc = computeGini(
      constraintsByCategoryInLV.get(ConstraintCategory.RUNTIME_EXCEPTION));
    double gini4Annotations = computeGini(
      constraintsByCategoryInLV.get(ConstraintCategory.ANNOTATION));
    double gini4Others = computeGini(
      constraintsByCategoryInLV.get(ConstraintCategory.OTHERS));

    double gini4AllConstraintsJava = computeGini(constraintsByProgramLVJava);
    double gini4AssertionsJava = computeGini(
      constraintsByCategoryInLVJava.get(ConstraintCategory.ASSERTION));
    double gini4APIsJava = computeGini(
      constraintsByCategoryInLVJava.get(ConstraintCategory.API));
    double gini4RTExcJava = computeGini(
      constraintsByCategoryInLVJava.get(ConstraintCategory.RUNTIME_EXCEPTION));
    double gini4AnnotationsJava = computeGini(
      constraintsByCategoryInLVJava.get(ConstraintCategory.ANNOTATION));
    double gini4OthersJava = computeGini(
      constraintsByCategoryInLVJava.get(ConstraintCategory.OTHERS));

    double gini4AllConstraintsKotlin = computeGini(constraintsByProgramLVKotlin);
    double gini4AssertionsKotlin = computeGini(
      constraintsByCategoryInLVKotlin.get(ConstraintCategory.ASSERTION));
    double gini4APIsKotlin = computeGini(
      constraintsByCategoryInLVKotlin.get(ConstraintCategory.API));
    double gini4RTExcKotlin = computeGini(
      constraintsByCategoryInLVKotlin.get(ConstraintCategory.RUNTIME_EXCEPTION));
    double gini4AnnotationsKotlin = computeGini(
      constraintsByCategoryInLVKotlin.get(ConstraintCategory.ANNOTATION));
    double gini4OthersKotlin = computeGini(
      constraintsByCategoryInLVKotlin.get(ConstraintCategory.OTHERS));

    File latex = ArtefactFactory.USAGE_GINI_ALL;
    try (PrintStream out = new PrintStream(Files.newOutputStream(latex.toPath()))) {
      out.println("% TABLE GENERATED BY " + AnalyseContractUsage.class.getName());
      out.println("% TIMESTAMP:   " + new Date());
      out.println("\\begin{table}[]");
      out.println("\\centering");
      out.println("\\caption{Gini}");
      out.println("\\label{tab:contractsGini}");
      out.println("\\begin{tabular}{| c | c | c | c | } \\hline");
      out.println("category & Java & Kotlin & Both \\ \\hline");
      out.print("\t");
      out.println("All constraints & " + getGini(gini4AllConstraintsJava) + " & " + getGini(
        gini4AllConstraintsKotlin) + " & " + getGini(gini4AllConstraints) + " \\\\ ");
      out.print("\t");
      out.println(
        "Assertions & " + getGini(gini4AssertionsJava) + " & " + getGini(gini4AssertionsKotlin)
          + " & " + getGini(gini4Assertions) + " \\\\ ");
      out.print("\t");
      out.println(
        "APIs & " + getGini(gini4APIsJava) + " & " + getGini(gini4APIsKotlin) + " & " + getGini(
          gini4APIs) + " \\\\ ");
      out.print("\t");
      out.println(
        "Annotations & " + getGini(gini4AnnotationsJava) + " & " + getGini(gini4AnnotationsKotlin)
          + " & " + getGini(gini4Annotations) + " \\\\ ");
      out.print("\t");
      out.println(
        "CRE & " + getGini(gini4RTExcJava) + " & " + getGini(gini4RTExcKotlin) + " & " + getGini(
          gini4RTExc) + " \\\\ ");
      out.print("\t");
      out.println(
        "Others & " + getGini(gini4OthersJava) + " & " + getGini(gini4OthersKotlin) + " & "
          + getGini(gini4Others) + " \\\\ ");
      out.println("\\hline");
      out.println("\\end{tabular}");
      out.println("\\end{table}");
    }
  }

  private static double getGini(double value) {
    return Math.round(value * 100.0) / 100.0;
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

package contractstudy.scripts;

import contractstudy.config.Logging;
import contractstudy.config.Preferences;
import contractstudy.constants.constraint.ContractElement;
import contractstudy.evolution.SubtypeDiffExtractor;
import contractstudy.evolution.constants.DiffResult;
import contractstudy.evolution.model.DiffRecord;
import contractstudy.evolution.model.Differ;
import contractstudy.scripts.model.ArtefactFactory;
import contractstudy.scripts.model.Experiment;
import contractstudy.scripts.model.ExperimentArtefact;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static contractstudy.evolution.model.diffRules.Utils.NF;

/**
 * Script to analyse how contracts evolve from one to the next version of an artefact.
 *
 * @author jens dietrich
 * @author Kamil Jezek [kjezek@kiv.zcu.cz]
 */
public class AnalyseHierarchyContracts implements Experiment {

  private static final Logger LOGGER = Logging.getLogger(AnalyseHierarchyContracts.class);
  private static final File RESULTS_FOLDER = new File(Preferences.getOutputFolder());

  final static Map<DiffResult, File> LOG_FILES = new HashMap<>() {
    {
      put(DiffResult.POSTCONDITION_REMOVED, ArtefactFactory.INHERITANCE_POST_CONDITION_REMOVED);
      put(DiffResult.PRECONDITION_ADDED, ArtefactFactory.INHERITANCE_PRE_CONDITION_ADDED);
      put(DiffResult.CANNOT_BE_CLASSIFIED, ArtefactFactory.INHERITANCE_CONTRACTS_NOT_CLASSIFIED);
    }
  };

  public static void main(String[] args) throws Exception {
    FileUtils.forceMkdir(RESULTS_FOLDER);
    resetLogsByDeletingFiles();
    Map<DiffResult, Integer> stats = compareEvolutionAndLogResults();
    outputStatsToConsole(stats);
    outputStatsToLatex(stats);
  }

  private static void resetLogsByDeletingFiles() {
    for (File log : LOG_FILES.values()) {
      log.delete();
    }
  }

  public static Map<DiffResult, Integer> compareEvolutionAndLogResults() throws Exception {
    List<DiffRecord> evolutionData = new SubtypeDiffExtractor().extract();
    Differ differ = new Differ();
    Map<DiffResult, Integer> stats = initEmpty();
    for (DiffRecord record : evolutionData) {
      DiffResult result = differ.compare(record.getConstraints1(), record.getConstraints2());
      stats.compute(result, (k, v) -> (v == null) ? 1 : v + 1);
      File log = LOG_FILES.get(result);
      if (log != null) {
        outputEachHierarchyStatsToFile(record, result, log);
      }
    }
    return stats;
  }

  public static Map<DiffResult, Integer> initEmpty() {
    Map<DiffResult, Integer> stats = new HashMap<>();
    for (DiffResult res : DiffResult.values()) {
      stats.put(res, 0);
    }
    return stats;
  }

  private static void outputEachHierarchyStatsToFile(
    DiffRecord record,
    DiffResult result,
    File log
  ) {
    try (PrintWriter out = new PrintWriter(new FileWriter(log, true))) {
      out.println(result);
      out.println("version with super type: " + record.getProgramVersion1());
      out.println("version with sub type:   " + record.getProgramVersion2());
      out.println("compilation unit: " + record.getCu2() + " extends " + record.getCu1());
      if (record.getMethodDecl1() != null) {
        out.println("method: " + record.getMethodDecl1());
      }
      out.println("constraints in " + record.getProgramVersion1());
      for (ContractElement c : record.getConstraints1()) {
        String addInfo = c.getAdditionalInfo() == null ? "?"
          : c.getAdditionalInfo().replaceAll("\\r\\n|\\r|\\n", " "); // remove new line chars !
        out.println(
          "\t" + c.getKind() + " condition: \"" + c.getCondition() + "\" , add info: " + addInfo);
      }
      out.println("constraints in " + record.getProgramVersion2());
      for (ContractElement c : record.getConstraints2()) {
        String addInfo = c.getAdditionalInfo() == null ? "?"
          : c.getAdditionalInfo().replaceAll("\\r\\n|\\r|\\n", " "); // remove new line chars !
        out.println(
          "\t" + c.getKind() + " condition: \"" + c.getCondition() + "\" , add info: " + addInfo);
      }
      out.println();
    } catch (IOException x) {
      LOGGER.warn("Exception writing details to log " + log.getAbsolutePath(), x);
    }
  }

  private static void outputStatsToConsole(Map<DiffResult, Integer> stats) {
    LOGGER.info("Analysis finished, printing stats");
    LOGGER.info("Details written to " + RESULTS_FOLDER.getAbsolutePath());
    for (Map.Entry<DiffResult, Integer> entry : stats.entrySet()) {
      LOGGER.info("" + entry.getKey() + " : " + entry.getValue());
    }
  }

  private static void outputStatsToLatex(Map<DiffResult, Integer> stats)
    throws IOException {
    LOGGER.info("Rendering output to latex");
    File latex = ArtefactFactory.INHERITANCE_STATS;
    try (PrintStream out = new PrintStream(Files.newOutputStream(latex.toPath()))) {
      out.println("% TABLE GENERATED BY " + AnalyseHierarchyContracts.class.getName());
      out.println("% TIMESTAMP:   " + new Date());
      out.println("\\begin{table}[]");
      out.println("\\centering");
      out.println("\\caption{Contract hierarchy data result summary}");
      out.println("\\label{tab:results-hierarchy}");
      out.println("\\begin{tabular}{|l|l|r|} \\hline");
      out.println("   evolution & critical & count \\\\ \\hline");
      out.println("   unchanged & no & " + NF.format(stats.get(DiffResult.UNCHANGED)) + "  \\\\");
      out.println(
        "   minor change & no & " + NF.format(stats.get(DiffResult.MINOR_CHANGE)) + "  \\\\");
      out.println(
        "   pre-conditions weakened & no & " + NF.format(stats.get(DiffResult.PRECONDITION_REMOVED))
          + "  \\\\");
      out.println("   post-conditions strengthened & no & " + NF.format(
        stats.get(DiffResult.POSTCONDITION_ADDED)) + "  \\\\ \\hline");
      out.println("   pre-conditions strengthened & yes & " + NF.format(
        stats.get(DiffResult.PRECONDITION_ADDED)) + "  \\\\");
      out.println("   post-conditions weakened & yes & " + NF.format(
        stats.get(DiffResult.POSTCONDITION_REMOVED)) + "  \\\\ \\hline");
      out.println("   unclassified & ? & " + NF.format(stats.get(DiffResult.CANNOT_BE_CLASSIFIED))
        + "  \\\\ \\hline");
      out.println("\\end{tabular}");
      out.println("\\end{table}");
    }
    LOGGER.info("Latex table with results created at " + latex.getAbsolutePath());
  }

  @Override
  public void invoke() throws Exception {
    if (provides().exists()) {
      LOGGER.info("Skipping already performed experiment: " + provides().getName());
      return;
    }
    AnalyseHierarchyContracts.main(new String[]{});
  }


  @Override
  public ExperimentArtefact[] requires() {
    return new ExperimentArtefact[]{
      ArtefactFactory.contracts(),
      ArtefactFactory.superCalls(),
      ArtefactFactory.classStructure()
    };
  }

  @Override
  public ExperimentArtefact provides() {
    return ArtefactFactory.contractsHierarchyTable();
  }
}

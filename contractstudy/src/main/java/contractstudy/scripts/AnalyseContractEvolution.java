package contractstudy.scripts;

import contractstudy.config.Logging;
import contractstudy.constants.constraint.ContractElement;
import contractstudy.evolution.EvolutionDiffExtractor;
import contractstudy.evolution.constants.DiffResult;
import contractstudy.evolution.model.DiffRecord;
import contractstudy.evolution.model.Differ;
import contractstudy.scripts.model.ArtefactFactory;
import contractstudy.scripts.model.Experiment;
import contractstudy.scripts.model.ExperimentArtefact;
import contractstudy.utils.LanguageUtils;
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
 */

public class AnalyseContractEvolution implements Experiment {

  static Logger LOGGER = Logging.getLogger(AnalyseContractEvolution.class);
  final static File RESULTS_FOLDER = ArtefactFactory.RESULTS_EVOLUTION_FOLDER;
  final static Map<DiffResult, File> LOG_FILES = new HashMap<>() {
    {
      put(DiffResult.POSTCONDITION_REMOVED, ArtefactFactory.EVOLUTION_POST_CONDITION_REMOVED);
      put(DiffResult.PRECONDITION_ADDED, ArtefactFactory.EVOLUTION_PRE_CONDITION_ADDED);
      put(DiffResult.CANNOT_BE_CLASSIFIED, ArtefactFactory.EVOLUTION_CONTRACTS_NOT_CLASSIFIED);
    }
  };
  static Map<DiffResult, Integer> evolutionStatsInJava = new HashMap<>();
  static Map<DiffResult, Integer> evolutionStatsInKotlin = new HashMap<>();

  public static void main(String[] args) throws Exception {
    RESULTS_FOLDER.mkdirs();
    resetLogsByDeletingFiles();
    Map<DiffResult, Integer> stats = compareEvolutionAndLogResults();
    outputStatsToConsole(stats);
    outputStatsToLatex();
  }

  private static void resetLogsByDeletingFiles() {
    for (File log : LOG_FILES.values()) {
      log.delete();
    }
  }

  public static Map<DiffResult, Integer> compareEvolutionAndLogResults() throws Exception {
    List<DiffRecord> evolutionRecords = new EvolutionDiffExtractor().extract();
    Differ differ = new Differ();

    Map<DiffResult, Integer> evolutionStats = initEmpty();
    evolutionStatsInJava = initEmpty();
    evolutionStatsInKotlin = initEmpty();

    for (DiffRecord record : evolutionRecords) {
      DiffResult result = differ.compare(record.getConstraints1(), record.getConstraints2());
      evolutionStats.compute(result, (k, v) -> (v == null) ? 1 : v + 1);
      File log = LOG_FILES.get(result);
      if (log != null) {
        outputEachEvolutionStatsToFile(record, result, log);
      }

      LanguageUtils.Language language = LanguageUtils.getLanguageFromNameExtension(record.getCu1());
      if (language == LanguageUtils.Language.JAVA) {
        evolutionStatsInJava.compute(result, (k, v) -> (v == null) ? 1 : v + 1);
      } else if (language == LanguageUtils.Language.KOTLIN) {
        evolutionStatsInKotlin.compute(result, (k, v) -> (v == null) ? 1 : v + 1);
      }
    }

    return evolutionStats;
  }

  public static Map<DiffResult, Integer> initEmpty() {
    Map<DiffResult, Integer> stats = new HashMap<>();
    for (DiffResult res : DiffResult.values()) {
      stats.put(res, 0);
    }
    return stats;
  }

  private static void outputEachEvolutionStatsToFile(DiffRecord record, DiffResult result, File log) {
    try (PrintWriter out = new PrintWriter(new FileWriter(log, true))) {
      out.println(result);
      out.println("version 1: " + record.getProgramVersion1());
      out.println("version 2: " + record.getProgramVersion2());
      out.println("compilation unit: " + record.getCu1());
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

  private static void outputStatsToLatex()
    throws IOException {
    LOGGER.info("Rendering output to latex");
    File latex = ArtefactFactory.EVOLUTION_STATS;
    try (PrintStream out = new PrintStream(Files.newOutputStream(latex.toPath()))) {
      out.println("% TABLE GENERATED BY " + AnalyseContractEvolution.class.getName());
      out.println("% TIMESTAMP:   " + new Date());
      out.println("\\begin{table}[]");
      out.println("\\centering");
      out.println("\\caption{Contract evolution data result summary}");
      out.println("\\label{tab:results-evolution}");
      out.println("\\begin{tabular}{|l|l|r|r|} \\hline");
      out.println("   evolution & critical & java count & kotlin count \\\\ \\hline");
      out.println("   unchanged & no & "
        + NF.format(evolutionStatsInJava.get(DiffResult.UNCHANGED)) + " & " + NF.format(evolutionStatsInKotlin.get(DiffResult.UNCHANGED)) + "  \\\\");
      out.println("   minor change & no & "
        + NF.format(evolutionStatsInJava.get(DiffResult.MINOR_CHANGE)) + " & " + NF.format(evolutionStatsInKotlin.get(DiffResult.MINOR_CHANGE)) + "  \\\\");
      out.println("   pre-conditions weakened & no & "
          + NF.format(evolutionStatsInJava.get(DiffResult.PRECONDITION_REMOVED)) + " & " + NF.format(evolutionStatsInKotlin.get(DiffResult.PRECONDITION_REMOVED)) + "  \\\\");
      out.println("   post-conditions strengthened & no & " +
        NF.format(evolutionStatsInJava.get(DiffResult.POSTCONDITION_ADDED)) + " & " + NF.format(evolutionStatsInKotlin.get(DiffResult.POSTCONDITION_ADDED)) + "  \\\\ \\hline");
      out.println("   pre-conditions strengthened & yes & "
        + NF.format(evolutionStatsInJava.get(DiffResult.PRECONDITION_ADDED)) + " & " + NF.format(evolutionStatsInKotlin.get(DiffResult.PRECONDITION_ADDED)) + "  \\\\");
      out.println("   post-conditions weakened & yes & "
        + NF.format(evolutionStatsInJava.get(DiffResult.POSTCONDITION_REMOVED)) + " & " +  NF.format(evolutionStatsInKotlin.get(DiffResult.POSTCONDITION_REMOVED)) + "  \\\\ \\hline");
      out.println("   unclassified & ? & "
        + NF.format(evolutionStatsInJava.get(DiffResult.CANNOT_BE_CLASSIFIED)) + " & " + NF.format(evolutionStatsInKotlin.get(DiffResult.CANNOT_BE_CLASSIFIED)) + "  \\\\ \\hline");
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
    AnalyseContractEvolution.main(new String[]{});
  }

  @Override
  public ExperimentArtefact[] requires() {
    return new ExperimentArtefact[]{
      ArtefactFactory.classStructure(),
      ArtefactFactory.contracts()
    };
  }

  @Override
  public ExperimentArtefact provides() {
    return ArtefactFactory.contractsEvolutionTable();
  }
}

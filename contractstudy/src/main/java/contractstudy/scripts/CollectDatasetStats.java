package contractstudy.scripts;

import com.google.common.base.Preconditions;
import contractstudy.collectDatasetStats.DataCollectionExtractor;
import contractstudy.config.Logging;
import contractstudy.config.Preferences;
import contractstudy.model.ProgramVersion;
import contractstudy.scripts.model.ArtefactFactory;
import contractstudy.scripts.model.Experiment;
import contractstudy.scripts.model.ExperimentArtefact;
import contractstudy.utils.LanguageUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static contractstudy.constants.SetStatsDataKeys.ALL_CONSTRUCTORS;
import static contractstudy.constants.SetStatsDataKeys.ALL_METHODS;
import static contractstudy.constants.SetStatsDataKeys.CLASSES;
import static contractstudy.constants.SetStatsDataKeys.COMPILATION_UNITS;
import static contractstudy.constants.SetStatsDataKeys.COMPILATION_UNITS_PARSING_FAILED;
import static contractstudy.constants.SetStatsDataKeys.LOC;
import static contractstudy.constants.SetStatsDataKeys.PROGRAMS;
import static contractstudy.constants.SetStatsDataKeys.PUBLIC_CONSTRUCTORS;
import static contractstudy.constants.SetStatsDataKeys.PUBLIC_METHODS;
import static contractstudy.constants.SetStatsDataKeys.VERSIONS;
import static contractstudy.evolution.model.diffRules.Utils.NF;


/**
 * Script used to collect some stats about the dataset being used.
 *
 * @author jens dietrich
 */
public class CollectDatasetStats implements Experiment {

  private static final Logger LOGGER = Logging.getLogger(CollectDatasetStats.class);

  public static void main(String[] args) throws Exception {

    File DATA_FOLDER = new File(Preferences.getDataFolder());
    Preconditions.checkArgument(DATA_FOLDER.exists(),
      "Cannot find data in " + DATA_FOLDER.getAbsolutePath());

    int THREAD_COUNT = Preferences.getThreadCount();
    File RESULTS_FOLDER = new File(Preferences.getOutputFolder());

    Map<String, Integer> data = new ConcurrentHashMap<>();
    Collection<File> zips = FileUtils.listFiles(DATA_FOLDER, new String[]{"zip"}, true);
    ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);
    Set<String> seenPrograms = Collections.synchronizedSet(new HashSet<>());

    AtomicInteger counter = new AtomicInteger(0);

    List<String> errorCuNames = Collections.synchronizedList(new ArrayList<>());

    for (File f : zips) {

      ProgramVersion pv = ProgramVersion.getOrCreateFromFile(f);
      String programName = pv.getName();

      if (seenPrograms.add(programName)) {
        data.compute(PROGRAMS.getKey(), (k, v) -> v == null ? 1 : v + 1);
        System.out.println("Found new program " + programName);
      }

      data.compute(VERSIONS.getKey(), (k, v) -> v == null ? 1 : v + 1);

      Runnable task = new Runnable() {
        @Override
        public void run() {
          try {
            LOGGER.info(
              "Analysing " + counter.incrementAndGet() + "/" + zips.size() + " - " + f.getName());
            ZipFile zip = new ZipFile(f);
            DataCollectionExtractor dataCollectionExtractor = new DataCollectionExtractor();
            collectStats(zip, dataCollectionExtractor, data, errorCuNames);
          } catch (Exception e) {
            LOGGER.warn("Cannot parse file: " + f, e);
          }
        }
      };
      executor.submit(task);
    }

    executor.shutdown();
    executor.awaitTermination(1, TimeUnit.DAYS);

    LOGGER.info("Analysis finished, printing stats");

    outputStatsToConsole(RESULTS_FOLDER, data);
    outputStatsToLatex(RESULTS_FOLDER, data);
    outputErrorsToFile(RESULTS_FOLDER, errorCuNames);
  }

  private static void collectStats(ZipFile zip, DataCollectionExtractor dataCollectionExtractor,
    Map<String, Integer> data, List<String> errorCuNames) throws IOException {
    Enumeration<? extends ZipEntry> en = zip.entries();
    while (en.hasMoreElements()) {
      ZipEntry e = en.nextElement();
      String name = e.getName();
      LanguageUtils.Language language = LanguageUtils.getLanguageFromNameExtension(name);
      if (language == LanguageUtils.Language.JAVA || language == LanguageUtils.Language.KOTLIN) {
        try (InputStream in = zip.getInputStream(e)) {
          try {
            dataCollectionExtractor.analyse(e.getName(), in, data);
          } catch (Exception t) {
            data.compute(COMPILATION_UNITS_PARSING_FAILED.getKey(),
              (k, v) -> v == null ? 1 : v + 1);
            errorCuNames.add(zip.getName() + ", " + name + ", Error: " + t.getMessage());
          }
        }
      }
    }
  }

  private static void outputStatsToConsole(File RESULTS_FOLDER, Map<String, Integer> data) {
    LOGGER.info("Details written to " + RESULTS_FOLDER.getAbsolutePath());
    for (Map.Entry<String, Integer> entry : data.entrySet()) {
      LOGGER.info("" + entry.getKey() + " : " + entry.getValue());
    }
  }

  private static void outputStatsToLatex(File RESULTS_FOLDER, Map<String, Integer> data)
    throws IOException {
    LOGGER.info("Rendering output to latex");
    File latex = new File(RESULTS_FOLDER, "dataset.tex");
    try (PrintStream out = new PrintStream(Files.newOutputStream(latex.toPath()))) {
      out.println("% TABLE GENERATED BY " + CollectDatasetStats.class.getName());
      out.println("% TIMESTAMP:   " + new Date());
      out.println("\\begin{table}[]");
      out.println("\\centering");
      out.println("\\caption{Data set metrics }");
      out.println("\\label{tab:data-metrics}");
      out.println("\\begin{tabular}{|l|l|} \\hline");
      out.println("   metric & value  \\\\ \\hline");
      out.println("   programs & " + NF.format(data.get(PROGRAMS.getKey())) + "  \\\\");
      out.println("   program versions & " + NF.format(data.get(VERSIONS.getKey())) + "  \\\\");
      out.println(
        "   compilation units & " + NF.format(data.get(COMPILATION_UNITS.getKey())) + "  \\\\");
      if (data.get(COMPILATION_UNITS_PARSING_FAILED.getKey()) != null)  // JFF: it was failing here
      {
        out.println("   unparsable compilation units & " + NF.format(
          data.get(COMPILATION_UNITS_PARSING_FAILED.getKey())) + "  \\\\");
      }
      out.println("   \\hline");
      out.println("   classes & " + NF.format(data.get(CLASSES.getKey())) + "  \\\\");
      out.println("   methods (all)& " + NF.format(data.get(ALL_METHODS.getKey())) + "  \\\\");
      out.println(
        "   constructors (all)& " + NF.format(data.get(ALL_CONSTRUCTORS.getKey())) + "  \\\\");
      out.println(
        "   methods (public and protected) & " + NF.format(data.get(PUBLIC_METHODS.getKey()))
          + "  \\\\");
      out.println(
        "   constructors (public and protected) & " + NF.format(
          data.get(PUBLIC_CONSTRUCTORS.getKey()))
          + "  \\\\");
      out.println(
        "   KLOC incl comments& " + NF.format(data.get(LOC.getKey()) / 1000) + "  \\\\ \\hline");
      out.println("\\end{tabular}");
      out.println("\\end{table}");
    }
    LOGGER.info("Latex table with results created at " + latex.getAbsolutePath());
  }

  private static void outputErrorsToFile(File RESULTS_FOLDER, List<String> errorCuNames)
    throws IOException {
    File dataSetErrors = new File(RESULTS_FOLDER, "dataset.errors");
    Collections.sort(errorCuNames);
    IOUtils.writeLines(errorCuNames, "\n", Files.newOutputStream(dataSetErrors.toPath()),
      StandardCharsets.UTF_8);
  }

  @Override
  public void invoke() throws Exception {
    if (provides().exists()) {
      LOGGER.info("Skipping already performed experiment: " + provides().getName());
      return;
    }
    CollectDatasetStats.main(new String[]{});
  }

  @Override
  public ExperimentArtefact[] requires() {
    return new ExperimentArtefact[]{
      ArtefactFactory.inputSrcZipFiles()
    };
  }

  @Override
  public ExperimentArtefact provides() {
    return ArtefactFactory.datasetStatistics();
  }

}

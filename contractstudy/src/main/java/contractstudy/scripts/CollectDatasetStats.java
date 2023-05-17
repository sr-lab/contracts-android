package contractstudy.scripts;

import com.google.common.base.Preconditions;
import contractstudy.config.Logging;
import contractstudy.config.Preferences;
import contractstudy.constants.SetStatsDataKeys;
import contractstudy.model.ProgramVersion;
import contractstudy.scripts.model.ArtefactFactory;
import contractstudy.scripts.model.Experiment;
import contractstudy.scripts.model.ExperimentArtefact;
import contractstudy.usage.collectDatasetStats.DataCollectionExtractor;
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

  static Map<String, Integer> data = new ConcurrentHashMap<>();
  static Map<String, Integer> dataJava = new ConcurrentHashMap<>();
  static Map<String, Integer> dataKotlin = new ConcurrentHashMap<>();

  public static void main(String[] args) throws Exception {

    File DATA_FOLDER = new File(Preferences.getDataFolder());
    Preconditions.checkArgument(DATA_FOLDER.exists(),
      "Cannot find data in " + DATA_FOLDER.getAbsolutePath());

    int THREAD_COUNT = Preferences.getThreadCount();

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
            collectStats(zip, dataCollectionExtractor, errorCuNames);
          } catch (Exception e) {
            LOGGER.warn("Cannot parse file: " + f, e);
          }
        }
      };
      executor.submit(task);
    }

    executor.shutdown();
    executor.awaitTermination(1, TimeUnit.DAYS);

    LOGGER.info("Analysis finished.");

    outputStatsToLatex();
    outputErrorsToFile(errorCuNames);
  }

  private static void collectStats(ZipFile zip, DataCollectionExtractor dataCollectionExtractor
    , List<String> errorCuNames) throws IOException {
    Enumeration<? extends ZipEntry> en = zip.entries();
    while (en.hasMoreElements()) {
      ZipEntry e = en.nextElement();
      String name = e.getName();
      LanguageUtils.Language language = LanguageUtils.getLanguageFromNameExtension(name);
      if (language == LanguageUtils.Language.JAVA || language == LanguageUtils.Language.KOTLIN) {
        try (InputStream in = zip.getInputStream(e)) {
          try {
            dataCollectionExtractor.analyse(e.getName(), in, dataJava, dataKotlin);
          } catch (Exception t) {
            if (language == LanguageUtils.Language.JAVA) {
              dataJava.compute(COMPILATION_UNITS_PARSING_FAILED.getKey(),
                (k, v) -> v == null ? 1 : v + 1);
            } else {
              dataKotlin.compute(COMPILATION_UNITS_PARSING_FAILED.getKey(),
                (k, v) -> v == null ? 1 : v + 1);
            }
            errorCuNames.add(zip.getName() + ", " + name + ", Error: " + t.getMessage());
          }
        }
      }
    }
  }

  private static void outputStatsToLatex() throws IOException {
    LOGGER.info("Rendering output to latex");
    File latex = ArtefactFactory.USAGE_DATASET_STATS;
    try (PrintStream out = new PrintStream(Files.newOutputStream(latex.toPath()))) {
      out.println("% TABLE GENERATED BY " + CollectDatasetStats.class.getName());
      out.println("% TIMESTAMP:   " + new Date());
      out.println("\\begin{table}[]");
      out.println("\\centering");
      out.println("\\caption{Dataset metrics }");
      out.println("\\label{tab:data-metrics}");
      out.println("\\begin{tabular}{|l|l|l|l|} \\hline");
      out.println("metric & Java & Kotlin & Both \\\\ \\hline");
      out.println("programs & ? & ? &" + get(data, PROGRAMS) + "  \\\\");
      out.println("program versions & ? & ? &" + get(data, VERSIONS) + "  \\\\");
      out.println(
        "compilation units & " + get(dataJava, COMPILATION_UNITS) + " & " + get(dataKotlin,
          COMPILATION_UNITS) + " & " + getTotal(COMPILATION_UNITS) + " \\\\");
      out.println(
        "unparsable compilation units & " + get(dataJava, COMPILATION_UNITS_PARSING_FAILED) + " & "
          + get(dataKotlin, COMPILATION_UNITS_PARSING_FAILED) + " & " + get(data,
          COMPILATION_UNITS_PARSING_FAILED) + " \\\\");
      out.println("\\hline");
      out.println(
        "classes & " + get(dataJava, CLASSES) + " & " + get(dataKotlin, CLASSES) + " & " + getTotal(
          CLASSES) + " \\\\");
      out.println(
        "methods (all) & " + get(dataJava, ALL_METHODS) + " & " + get(dataKotlin, ALL_METHODS)
          + " & " + getTotal(ALL_METHODS) + " \\\\");
      out.println(
        "constructors (all) & " + get(dataJava, ALL_CONSTRUCTORS) + " & " + get(dataKotlin,
          ALL_CONSTRUCTORS) + " & " + getTotal(ALL_CONSTRUCTORS) + " \\\\");
      out.println("methods (public and protected) & " + get(dataJava, PUBLIC_METHODS) + " & " + get(
        dataKotlin, PUBLIC_METHODS) + " & " + getTotal(PUBLIC_METHODS) + " \\\\");
      out.println(
        "constructors (public and protected) & " + get(dataJava, PUBLIC_CONSTRUCTORS) + " & " + get(
          dataKotlin, PUBLIC_CONSTRUCTORS) + " & " + getTotal(PUBLIC_CONSTRUCTORS) + " \\\\");
      out.println("KLOC incl comments & " + getKLOC(dataJava) + " & " + getKLOC(dataKotlin) + " & "
        + getTotalKLOC() + " \\\\");
      out.println("\\hline");
      out.println("\\end{tabular}");
      out.println("\\end{table}");
    }
    LOGGER.info("Latex table with results created at " + latex.getAbsolutePath());
  }

  private static String get(Map<String, Integer> data, SetStatsDataKeys key) {
    String value = "-";
    if (data.get(key.getKey()) != null) {
      value = NF.format(data.get(key.getKey()));
    }
    return value;
  }

  private static String getTotal(SetStatsDataKeys key) {
    Integer value = 0;
    if (dataJava.get(key.getKey()) != null) {
      value += dataJava.get(key.getKey());
    }
    if (dataKotlin.get(key.getKey()) != null) {
      value += dataKotlin.get(key.getKey());
    }
    return NF.format(value);
  }

  private static String getKLOC(Map<String, Integer> data) {
    Integer value = 0;
    if (data.get(LOC.getKey()) != null) {
      value = data.get(LOC.getKey()) / 1000;
    }
    return NF.format(value);
  }

  private static String getTotalKLOC() {
    Integer value = 0;
    if (dataJava.get(LOC.getKey()) != null) {
      value += dataJava.get(LOC.getKey());
    }
    if (dataKotlin.get(LOC.getKey()) != null) {
      value += dataKotlin.get(LOC.getKey());
    }
    value = value / 1000;
    return NF.format(value);
  }

  private static void outputErrorsToFile(List<String> errorCuNames)
    throws IOException {
    File dataSetErrors = ArtefactFactory.USAGE_DATASET_ERRORS;
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

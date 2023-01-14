package contractstudy.scripts;

import com.google.common.base.Preconditions;
import contractstudy.collectDatasetStats.DataCollectionAcrossVersionsExtractor;
import contractstudy.config.Logging;
import contractstudy.config.Preferences;
import contractstudy.model.ProgramVersion;
import contractstudy.scripts.model.ArtefactFactory;
import contractstudy.scripts.model.Experiment;
import contractstudy.scripts.model.ExperimentArtefact;
import contractstudy.utils.LanguageUtils;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
import static contractstudy.constants.SetStatsDataKeys.PUBLIC_CONSTRUCTORS;
import static contractstudy.constants.SetStatsDataKeys.PUBLIC_METHODS;

/**
 * Script used to collect some stats on program versions in the dataset. Output is written to a csv
 * file.
 *
 * @author jens dietrich
 */
public class CollectProgramVersionStats implements Experiment {

  final static File DATA_FOLDER = new File(Preferences.getDataFolder());
  private static final Logger LOGGER = Logging.getLogger(CollectProgramVersionStats.class);

  public static void main(String[] args) throws Exception {

    Preconditions.checkArgument(DATA_FOLDER.exists(),
      "Cannot find data in " + DATA_FOLDER.getAbsolutePath());

    int THREAD_COUNT = Preferences.getThreadCount();
    long startTime = System.currentTimeMillis();
    Map<ProgramVersion, Map<String, Integer>> data = new ConcurrentHashMap<>();
    List<String> errorCuNames = Collections.synchronizedList(new ArrayList<>());
    Collection<File> zips = FileUtils.listFiles(DATA_FOLDER, new String[]{"zip"}, true);
    ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);
    AtomicInteger counter = new AtomicInteger(0);

    for (File f : zips) {

      Runnable task = new Runnable() {
        @Override
        public void run() {
          try {
            LOGGER.info(
              "Analysing " + counter.incrementAndGet() + "/" + zips.size() + " - " + f.getName());
            collectStats(f, data, errorCuNames);
          } catch (Exception e) {
            LOGGER.warn("Cannot parse file: " + f, e);
          }
        }
      };
      executor.submit(task);
    }

    executor.shutdown();
    executor.awaitTermination(1, TimeUnit.DAYS);

    outputResultsToCSVFile(data);

    long endTime = System.currentTimeMillis();
    LOGGER.info("Done");
    LOGGER.info("\ttime: " + (endTime - startTime) + " ms");

  }

  private static void collectStats(
    File folder,
    Map<ProgramVersion, Map<String, Integer>> data,
    List<String> errorCuNames
  ) throws IOException {
    DataCollectionAcrossVersionsExtractor extractor = new DataCollectionAcrossVersionsExtractor();
    ZipFile zip = new ZipFile(folder);
    Enumeration<? extends ZipEntry> en = zip.entries();
    Map<String, Integer> dataForProgramVersion = new HashMap<>();
    ProgramVersion pv = ProgramVersion.getOrCreateFromFile(folder);
    while (en.hasMoreElements()) {
      ZipEntry e = en.nextElement();
      String name = e.getName();
      LanguageUtils.Language language = LanguageUtils.getLanguageFromNameExtension(name);
      if (language == LanguageUtils.Language.JAVA || language == LanguageUtils.Language.KOTLIN) {
        try (InputStream in = zip.getInputStream(e)) {
          try {
            extractor.analyse(e.getName(), in, dataForProgramVersion);
            data.put(pv, dataForProgramVersion);
          } catch (Exception t) {
            dataForProgramVersion.compute(COMPILATION_UNITS_PARSING_FAILED.getKey(),
              (k, v) -> v == null ? 1 : v + 1);
            errorCuNames.add(zip.getName() + ", " + name + ", Error: " + t.getMessage());
          }
        }
      }
    }

  }

  private static void outputResultsToCSVFile(Map<ProgramVersion, Map<String, Integer>> data)
    throws IOException {
    File csv = ArtefactFactory.EVOLUTION_VERSION_STATS;
    char SEP = ',';
    Map<String, Map<String, Long>> programTotals = new HashMap<>();

    try (PrintWriter out = new PrintWriter(new FileWriter(csv))) {

      out.println(
        "name,version,loc,cus,classes,all methods, all constructors, pub. + prot. methods, pub. + prot. constr, id");

      for (Map.Entry<ProgramVersion, Map<String, Integer>> e : data.entrySet()) {
        if (!programTotals.containsKey(e.getKey().getName())) {
          programTotals.put(e.getKey().getName(), new HashMap<>());
        }

        Map<String, Long> p = programTotals.get(e.getKey().getName());
        out.print(e.getKey().getName());
        out.print(SEP);
        out.print(e.getKey().getVersion());
        out.print(SEP);

        int locCount = Optional.ofNullable(e.getValue().get(LOC.getKey())).orElse(0);
        out.print(locCount);
        p.compute(LOC.getKey(), (k, v) -> v == null ? (long) locCount : v + locCount);
        out.print(SEP);

        int compilationUnitsCount = Optional.ofNullable(
          e.getValue().get(COMPILATION_UNITS.getKey())).orElse(0);
        out.print(compilationUnitsCount);
        p.compute(COMPILATION_UNITS.getKey(),
          (k, v) -> v == null ? (long) compilationUnitsCount : v + compilationUnitsCount);
        out.print(SEP);

        int classesCount = Optional.ofNullable(e.getValue().get(CLASSES.getKey())).orElse(0);
        out.print(classesCount);
        p.compute(CLASSES.getKey(), (k, v) -> v == null ? (long) classesCount : v + classesCount);
        out.print(SEP);

        int allMethodsCount = Optional.ofNullable(e.getValue().get(ALL_METHODS.getKey())).orElse(0);
        out.print(allMethodsCount);
        p.compute(ALL_METHODS.getKey(),
          (k, v) -> v == null ? (long) allMethodsCount : v + allMethodsCount);
        out.print(SEP);

        int allConstructorsMethods = Optional.ofNullable(
          e.getValue().get(ALL_CONSTRUCTORS.getKey())).orElse(0);
        out.print(allConstructorsMethods);
        p.compute(ALL_CONSTRUCTORS.getKey(),
          (k, v) -> v == null ? (long) allConstructorsMethods : v + allConstructorsMethods);
        out.print(SEP);

        int publicMethodsCount = Optional.ofNullable(e.getValue().get(PUBLIC_METHODS.getKey()))
          .orElse(0);
        out.print(publicMethodsCount);
        p.compute(PUBLIC_METHODS.getKey(),
          (k, v) -> v == null ? (long) publicMethodsCount : v + publicMethodsCount);
        out.print(SEP);

        int publicConstructorsCount = Optional.ofNullable(
          e.getValue().get(PUBLIC_CONSTRUCTORS.getKey())).orElse(0);
        out.print(publicConstructorsCount);
        p.compute(PUBLIC_CONSTRUCTORS.getKey(),
          (k, v) -> v == null ? (long) publicConstructorsCount : v + publicConstructorsCount);
        out.print(SEP);

        out.print(e.getKey().getName() + "-" + e.getKey().getVersion());
        out.println();
      }

      //TODO: Wouldn't it be better to output total to a separate file?
      for (Map.Entry<String, Map<String, Long>> e : programTotals.entrySet()) {
        out.print(e.getKey());
        out.print(SEP);
        out.print("TOTALS");
        out.print(SEP);
        out.print(e.getValue().get(LOC.getKey()));
        out.print(SEP);
        out.print(e.getValue().get(COMPILATION_UNITS.getKey()));
        out.print(SEP);
        out.print(e.getValue().get(CLASSES.getKey()));
        out.print(SEP);
        out.print(e.getValue().get(ALL_METHODS.getKey()));
        out.print(SEP);
        out.print(e.getValue().get(ALL_CONSTRUCTORS.getKey()));
        out.print(SEP);
        out.print(e.getValue().get(PUBLIC_METHODS.getKey()));
        out.print(SEP);
        out.print(e.getValue().get(PUBLIC_CONSTRUCTORS.getKey()));
        out.print(SEP);
        out.println();
      }
    }
  }

  @Override
  public void invoke() throws Exception {
    if (provides().exists()) {
      LOGGER.info("Skipping already performed experiment: " + provides().getName());
      return;
    }
    CollectProgramVersionStats.main(new String[]{});
  }

  @Override
  public ExperimentArtefact[] requires() {
    return new ExperimentArtefact[]{
      ArtefactFactory.inputSrcZipFiles()
    };
  }

  @Override
  public ExperimentArtefact provides() {
    return ArtefactFactory.programVersionStatistics();
  }


}

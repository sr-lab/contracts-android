package contractstudy.scripts;

import contractstudy.model.ProgramVersion;
import contractstudy.config.Logging;
import contractstudy.config.Preferences;
import contractstudy.hierarchy.SuperCallSiteExtractor.SuperCallSiteExtractor;
import contractstudy.hierarchy.model.SuperCallSite;
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
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Analyse whether method invoke overridden methods via super. Results will be written to a csv
 * file.
 *
 * @author jens dietrich
 */
public class CollectInvocationViaSuper implements Experiment {

  private static final Logger LOGGER = Logging.getLogger(CollectInvocationViaSuper.class);

  public static void main(String[] args) throws Exception {
    File DATA_FOLDER = new File(Preferences.getDataFolder());
    int THREAD_COUNT = Preferences.getThreadCount();
    Collection<File> zips = FileUtils.listFiles(DATA_FOLDER, new String[]{"zip"}, true);
    AtomicInteger counter = new AtomicInteger(0);
    List<SuperCallSite> superCallSites = Collections.synchronizedList(new ArrayList<>());

    ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);
    long startTime = System.currentTimeMillis();

    for (File f : zips) {
      ProgramVersion pv = ProgramVersion.getOrCreateFromFile(f);
      Runnable task = new Runnable() {
        @Override
        public void run() {
          try {
            findSuperCallSites(counter, zips, f, superCallSites, pv);
          } catch (Exception e) {
            LOGGER.warn("Cannot parse file: " + f, e);
          }
        }
      };
      executor.submit(task);
    }

    executor.shutdown();
    executor.awaitTermination(1, TimeUnit.DAYS);

    outputResultsToCSV(superCallSites);

    long endTime = System.currentTimeMillis();
    LOGGER.info("Done");
    LOGGER.info("\ttime: " + (endTime - startTime) + " ms");
  }

  private static void findSuperCallSites(AtomicInteger counter, Collection<File> zips, File f,
    List<SuperCallSite> superCallSites, ProgramVersion pv)
    throws IOException {
    LOGGER.info(
      "Analysing " + counter.incrementAndGet() + "/" + zips.size() + " - " + f.getName());
    SuperCallSiteExtractor extractor = new SuperCallSiteExtractor();
    ZipFile zip = new ZipFile(f);
    Enumeration<? extends ZipEntry> en = zip.entries();
    while (en.hasMoreElements()) {
      ZipEntry e = en.nextElement();
      String name = e.getName();
      LanguageUtils.Language language = LanguageUtils.getLanguageFromNameExtension(name);
      if (language == LanguageUtils.Language.JAVA || language == LanguageUtils.Language.KOTLIN) {
        try (InputStream in = zip.getInputStream(e)) {
          try {
            extractor.analyse(in, superCallSites, pv, name);
          } catch (Exception t) {
            LOGGER.warn("It was not possible to analyse " + name + "in version" + pv.getVersion());
          }
        }
      }
    }
  }

  private static void outputResultsToCSV(List<SuperCallSite> superCallSites)
    throws IOException {
    File outputFile = getOutputFile();
    LOGGER.info("Analysis done, exporting results to  " + outputFile.getAbsolutePath());
    LOGGER.info("\tSuper call sites found:  " + superCallSites.size());
    char SEP = ',';
    try (PrintWriter out = new PrintWriter(new FileWriter(outputFile))) {
      out.println("program,version,cu,declaration,kind");
      for (SuperCallSite scs : superCallSites) {
        out.print(scs.programVersion.getName().replaceAll(",", " "));
        out.print(SEP);
        out.print(scs.programVersion.getVersion().replaceAll(",", " "));
        out.print(SEP);
        out.print(scs.cu);
        out.print(SEP);
        out.print(scs.methodDeclaration.replaceAll(",", " "));
        out.print(SEP);
        out.print(scs.isMethod ? "method" : "constructor");
        out.println();
      }
    }
  }

  private static File getOutputFile() throws IOException {
    File OUTPUT_FOLDER = new File(Preferences.getOutputFolder());
    FileUtils.forceMkdir(OUTPUT_FOLDER);
    return new File(Preferences.getOutputFolder(), "supercallsites.csv");
  }

  @Override
  public void invoke() throws Exception {
    if (provides().exists()) {
      LOGGER.info("Skipping already performed experiment: " + provides().getName());
      return;
    }
    CollectInvocationViaSuper.main(new String[]{});
  }

  @Override
  public ExperimentArtefact[] requires() {
    return new ExperimentArtefact[]{
      ArtefactFactory.inputSrcZipFiles()
    };
  }

  @Override
  public ExperimentArtefact provides() {
    return ArtefactFactory.superCalls();
  }

}

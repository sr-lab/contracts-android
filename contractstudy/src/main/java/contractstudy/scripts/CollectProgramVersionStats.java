package contractstudy.scripts;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.google.common.base.Preconditions;
import contractstudy.ProgramVersion;
import contractstudy.config.Logging;
import contractstudy.config.Preferences;
import contractstudy.scripts.engine.ArtefactFactory;
import contractstudy.scripts.engine.Experiment;
import contractstudy.scripts.engine.ExperimentArtefact;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
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


  private static final Logger LOGGER = Logging.getLogger(CollectProgramVersionStats.class);

  public static void main(String[] args) throws Exception {

    File DATA_FOLDER = new File(Preferences.getDataFolder());
    Preconditions.checkArgument(DATA_FOLDER.exists(),
      "Cannot find data in " + DATA_FOLDER.getAbsolutePath());

    int THREAD_COUNT = Preferences.getThreadCount();
    File RESULTS_FOLDER = new File(Preferences.getResultsFolder());

    long startTime = System.currentTimeMillis();

    Map<ProgramVersion, Map<String, Integer>> data = new ConcurrentHashMap<>();
    Collection<File> zips = FileUtils.listFiles(DATA_FOLDER, new String[]{"zip"}, true);
    ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);
    AtomicInteger counter = new AtomicInteger(0);

    for (File f : zips) {

      ProgramVersion pv = ProgramVersion.getOrCreateFromFile(f);
      Runnable task = new Runnable() {
        @Override
        public void run() {
          try {
            LOGGER.info(
              "Analysing " + counter.incrementAndGet() + "/" + zips.size() + " - " + f.getName());
            ZipFile zip = new ZipFile(f);
            Enumeration<? extends ZipEntry> en = zip.entries();
            Map<String, Integer> dataForPV = new HashMap<>();

            while (en.hasMoreElements()) {
              ZipEntry e = en.nextElement();
              String name = e.getName();
              if (name.endsWith(".java")) {
                try (InputStream in = zip.getInputStream(e)) {
                  try {
                    CompilationUnit cu = StaticJavaParser.parse(in);
                    dataForPV.compute(COMPILATION_UNITS.getKey(), (k, v) -> v == null ? 1 : v + 1);
                    int size = cu.getEnd().get().line - cu.getBegin().get().line;
                    dataForPV.compute(LOC.getKey(), (k, v) -> v == null ? size : v + size);
                    Map<String, Integer> tmp = new HashMap<>();
                    data.put(pv, tmp);
                    new DataCollectionVisitor(tmp).visit(cu, null);
                    dataForPV.compute(PUBLIC_METHODS.getKey(),
                      (k, v) -> v == null ? getCounter(tmp, PUBLIC_METHODS.getKey())
                        : v + getCounter(tmp, PUBLIC_METHODS.getKey()));
                    dataForPV.compute(PUBLIC_CONSTRUCTORS.getKey(),
                      (k, v) -> v == null ? getCounter(tmp, PUBLIC_CONSTRUCTORS.getKey())
                        : v + getCounter(tmp, PUBLIC_CONSTRUCTORS.getKey()));
                    dataForPV.compute(ALL_METHODS.getKey(),
                      (k, v) -> v == null ? getCounter(tmp, ALL_METHODS.getKey())
                        : v + getCounter(tmp, ALL_METHODS.getKey()));
                    dataForPV.compute(ALL_CONSTRUCTORS.getKey(),
                      (k, v) -> v == null ? getCounter(tmp, ALL_CONSTRUCTORS.getKey())
                        : v + getCounter(tmp, ALL_CONSTRUCTORS.getKey()));
                    dataForPV.compute(CLASSES.getKey(),
                      (k, v) -> v == null ? getCounter(tmp, CLASSES.getKey())
                        : v + getCounter(tmp, CLASSES.getKey()));
                  } catch (Exception t) {
                    LOGGER.warn("Cannot parse cu " + pv + " / " + name);
                    t.printStackTrace(); // JFF: added this
                  }
                }

              }
            }
            data.put(pv, dataForPV);
          } catch (Exception e) {
            // log errors and continue with next files
            LOGGER.warn("Cannot parse file: " + f, e);
            e.printStackTrace();
          }
        }
      };
      executor.submit(task);
    }
    executor.shutdown();
    executor.awaitTermination(1, TimeUnit.DAYS);

    char SEP = '\t';
    File csv = new File(RESULTS_FOLDER, "programversion_stats.csv");

    Map<String, Map<String, Long>> programTotals = new HashMap<String, Map<String, Long>>();
    long val;

    try (PrintWriter out = new PrintWriter(new FileWriter(csv))) {
      out.println(
        "prg. name,prg. version,loc,cus,classes,all methods, all constructors, pub. + prot. methods, pub. + prot. constr, id");
      for (Map.Entry<ProgramVersion, Map<String, Integer>> e : data.entrySet()) {
        if (!programTotals.containsKey(e.getKey().getName())) {
          programTotals.put(e.getKey().getName(), new HashMap<String, Long>());
        }
        // JFF: FIXME
        if (e.getKey().getName().equals("Rudloff-openvegemap_cordova") && e.getKey().getVersion()
          .equals("2.0.0")) {
          continue;
        }
        Map<String, Long> p = programTotals.get(e.getKey().getName());
        out.print(e.getKey().getName());
        out.print(SEP);
        out.print(e.getKey().getVersion());
        out.print(SEP);
        out.print(e.getValue().get(LOC.getKey()));
        p.compute(LOC.getKey(),
          (k, v) -> v == null ? (long) e.getValue().get(LOC.getKey())
            : v + e.getValue().get(LOC.getKey()));
        out.print(SEP);
        out.print(e.getValue().get(COMPILATION_UNITS.getKey()));
        p.compute(COMPILATION_UNITS.getKey(),
          (k, v) -> v == null ? (long) e.getValue().get(COMPILATION_UNITS.getKey())
            : v + e.getValue().get(COMPILATION_UNITS.getKey()));
        out.print(SEP);
        out.print(e.getValue().get(CLASSES.getKey()));
        p.compute(CLASSES.getKey(),
          (k, v) -> v == null ? (long) e.getValue().get(CLASSES.getKey())
            : v + e.getValue().get(CLASSES.getKey()));
        out.print(SEP);
        out.print(e.getValue().get(ALL_METHODS.getKey()));
        p.compute(ALL_METHODS.getKey(),
          (k, v) -> v == null ? (long) e.getValue().get(ALL_METHODS.getKey())
            : v + e.getValue().get(ALL_METHODS.getKey()));
        out.print(SEP);
        out.print(e.getValue().get(ALL_CONSTRUCTORS.getKey()));
        p.compute(ALL_CONSTRUCTORS.getKey(),
          (k, v) -> v == null ? (long) e.getValue().get(ALL_CONSTRUCTORS.getKey())
            : v + e.getValue().get(ALL_CONSTRUCTORS.getKey()));
        out.print(SEP);
        out.print(e.getValue().get(PUBLIC_METHODS.getKey()));
        p.compute(PUBLIC_METHODS.getKey(),
          (k, v) -> v == null ? (long) e.getValue().get(PUBLIC_METHODS.getKey())
            : v + e.getValue().get(PUBLIC_METHODS.getKey()));
        out.print(SEP);
        out.print(e.getValue().get(PUBLIC_CONSTRUCTORS.getKey()));
        p.compute(PUBLIC_CONSTRUCTORS.getKey(),
          (k, v) -> v == null ? (long) e.getValue().get(PUBLIC_CONSTRUCTORS.getKey())
            : v + e.getValue().get(PUBLIC_CONSTRUCTORS.getKey()));
        // entry key, for sorting
        out.print(SEP);
        out.print(e.getKey().getName() + "-" + e.getKey().getSanitizedVersion());
        out.println();
      }
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
    long endTime = System.currentTimeMillis();
    LOGGER.info("Done");
    LOGGER.info("\ttime: " + (endTime - startTime) + " ms");

  }

  private static int getCounter(Map<String, Integer> values, String key) {
    Integer v = values.get(key);
    return v == null ? 0 : v;
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

  static class DataCollectionVisitor extends VoidVisitorAdapter<Object> {

    private Map<String, Integer> data = null;

    public DataCollectionVisitor(Map<String, Integer> data) {
      super();
      this.data = data;
    }

    // control the methods being visited
    @Override
    public void visit(MethodDeclaration methodDeclr, Object arg) {
      //int modifiers = methodDeclr.getModifiers();
      NodeList<Modifier> modifiers = methodDeclr.getModifiers();
      //if (ModifierSet.isPublic(modifiers) || ModifierSet.isProtected(modifiers)) {
      if (modifiers.contains(Modifier.publicModifier()) || modifiers.contains(
        Modifier.protectedModifier())) {
        data.compute(PUBLIC_METHODS.getKey(), (k, v) -> v == null ? 1 : v + 1);
      }
      data.compute(ALL_METHODS.getKey(), (k, v) -> v == null ? 1 : v + 1);
      super.visit(methodDeclr, arg);
    }

    @Override
    public void visit(ConstructorDeclaration constructorDeclr, Object arg) {
      //int modifiers = constructorDeclr.getModifiers();
      NodeList<Modifier> modifiers = constructorDeclr.getModifiers();
      //if (ModifierSet.isPublic(modifiers) || ModifierSet.isProtected(modifiers)) {
      if (modifiers.contains(Modifier.publicModifier()) || modifiers.contains(
        Modifier.protectedModifier())) {
        data.compute(PUBLIC_CONSTRUCTORS.getKey(), (k, v) -> v == null ? 1 : v + 1);
      }
      data.compute(ALL_CONSTRUCTORS.getKey(), (k, v) -> v == null ? 1 : v + 1);
      super.visit(constructorDeclr, arg);
    }

    @Override
    public void visit(ClassOrInterfaceDeclaration n, Object arg) {
      super.visit(n, arg);
      data.compute(CLASSES.getKey(), (k, v) -> v == null ? 1 : v + 1);
    }
  }
}

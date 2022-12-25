package contractstudy.scripts;

import contractstudy.ProgramVersion;
import contractstudy.config.Logging;
import contractstudy.config.Preferences;
import contractstudy.constants.ClassCoordinatesKeysEnum;
import contractstudy.model.ClassAndVersion;
import contractstudy.hierarchy.model.InheritanceResolved;
import contractstudy.hierarchy.ProjectVersionHierarchyExtractor.ProjectVersionHierarchyExtractor;
import contractstudy.hierarchy.model.ClassCoordinates;
import contractstudy.hierarchy.model.ClassParents;
import contractstudy.scripts.engine.ArtefactFactory;
import contractstudy.scripts.engine.Experiment;
import contractstudy.scripts.engine.ExperimentArtefact;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static contractstudy.maven.CorpusUtils.listJsons;
import static contractstudy.maven.CorpusUtils.listProjects;
import static contractstudy.maven.CorpusUtils.parseVersion;

/**
 * @author Kamil Jezek [kamil.jezek@verifalabs.com]
 */
public class ComputeInheritanceHierarchy implements Experiment {

  private static final File ROOT = new File(Preferences.getOutputStructureFolder());
  private static final ProjectVersionHierarchyExtractor extractor = new ProjectVersionHierarchyExtractor();
  private static final Logger LOGGER = Logging.getLogger(ComputeInheritanceHierarchy.class);

  public static void main(String[] args) throws Exception {

    //testSaveOpenJDK(args);

    long startTime = System.currentTimeMillis();
    ExecutorService executor = Executors.newFixedThreadPool(Preferences.getThreadCount());

    for (File project : listProjects(new File(Preferences.getDataFolder()))) {

      LOGGER.info("Processing: " + project);

      for (File json : listJsons(project)) {

        Runnable task = new Runnable() {
          @Override
          public void run() {
            try {
              ProgramVersion version = parseVersion(project, json);
              Map<ClassCoordinates, ClassParents> classesMap = new HashMap<>();

              // TODO skip all dependencies for now
              List<ProgramVersion> deps = new ArrayList<>(); // parseDepsProgramVersion(project, json);

              extractor.analyse(version, deps, new InheritanceResolved() {
                @Override
                public void notify(ClassParents parents) {
                  // it may replace already stored class, but it does not matter
                  // as it will be still the same class due to internal caching
                  classesMap.put(parents, parents);
                }

                @Override
                public void notify(ClassCoordinates classCoordinates) {
                  // only class encountered
                  classesMap.put(classCoordinates, null);
                }
              });

              File file = new File(new File(ROOT, project.getName()), fileName(json));
              saveResultsToFile(file, classesMap);
            } catch (Exception e) {
              LOGGER.info("Skipping incompatible source-code version for " + json);
            }
          }
        };
        executor.submit(task);
      }
    }

    executor.shutdown();
    executor.awaitTermination(10, TimeUnit.DAYS);
    long endTime = System.currentTimeMillis();

    LOGGER.info("Done!");
    LOGGER.info("\ttime: " + (endTime - startTime) + " ms");
    LOGGER.info("\tthreads used: " + Preferences.getThreadCount());

  }

  private static void testSaveOpenJDK(String[] args) throws Exception {
    // do not forget to add this folder!
    File jdkDir = new File(Preferences.getDataFolder(), "../jdk-data/open-jdk");
    File jdk = new File(jdkDir, "open-jdk-8.zip");

    if (args.length > 0 && args[0].equals("--skip-jdk")) {
      LOGGER.warn(jdk + " skipped");
    } else {
      Map<ClassCoordinates, ClassParents> classesMap = new HashMap<>();
      extractor.addGlobal(ProgramVersion.getOrCreateFromFile(jdk), new InheritanceResolved() {
        @Override
        public void notify(ClassParents parents) {
          classesMap.put(parents, parents);
        }

        @Override
        public void notify(ClassCoordinates classCoordinates) {
          classesMap.put(classCoordinates, null);
        }
      });
      File file = new File(new File(ROOT, jdkDir.getName()), "open-jdk-8-struct.json");
      saveResultsToFile(file, classesMap);
    }
  }

  private static void saveResultsToFile(
    final File file,
    final Map<ClassCoordinates, ClassParents> parents) throws IOException {

    file.getParentFile().mkdirs();
    JSONArray a = new JSONArray();

    for (ClassCoordinates c : parents.keySet()) {
      Set<ClassCoordinates> withInnerSet = new HashSet<>();
      withInnerSet.add(c);
      withInnerSet.addAll(c.getInnerClasses());
      ClassParents classParents = parents.get(c);
      for (ClassCoordinates cc : withInnerSet) {
        a.put(getClassCoordinate(classParents, cc));
      }
    }
    IOUtils.write(a.toString(), Files.newOutputStream(file.toPath()), "utf-8");
  }

  private static JSONObject getClassCoordinate(ClassParents classParents, ClassCoordinates cc) {
    JSONObject classCoordinates = new JSONObject();
    classCoordinates.put(ClassCoordinatesKeysEnum.CLASS_NAME.getKeyword(), cc.getClassName());
    classCoordinates.put(ClassCoordinatesKeysEnum.CU_NAME.getKeyword(), cc.getCuName());
    classCoordinates.put(ClassCoordinatesKeysEnum.METHODS.getKeyword(), cc.getMethods());
    JSONArray aa = new JSONArray();
    if (classParents != null) {
      for (ClassAndVersion parent : classParents.getParents(cc.getClassName())) {
        aa.put(new JSONObject(parent.toJson()));
      }
    }
    classCoordinates.put(ClassCoordinatesKeysEnum.PARENTS.getKeyword(), aa);
    return classCoordinates;
  }


  private static String fileName(File version) {
    String jsonName = version.getName();
    String name = jsonName.substring(0, jsonName.lastIndexOf("-"));
    return name + "-struct.json";
  }

  @Override
  public void invoke() throws Exception {
    if (provides().exists()) {
      LOGGER.info("Skipping already performed experiment: " + provides().getName());
      return;
    }
    ComputeInheritanceHierarchy.main(new String[]{});
  }

  @Override
  public ExperimentArtefact[] requires() {
    return new ExperimentArtefact[]{
      ArtefactFactory.inputSrcZipFiles(),
      ArtefactFactory.inputDepFiles()
    };
  }

  @Override
  public ExperimentArtefact provides() {
    return ArtefactFactory.classStructure();
  }
}

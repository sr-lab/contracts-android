package contractstudy.scripts;

import contractstudy.config.Logging;
import contractstudy.config.Preferences;
import contractstudy.constants.ClassCoordinatesKeysEnum;
import contractstudy.inheritance.ProjectVersionHierarchyExtractor.ProjectVersionHierarchyExtractor;
import contractstudy.inheritance.model.ClassCoordinates;
import contractstudy.inheritance.model.ClassParents;
import contractstudy.inheritance.model.InheritanceResolved;
import contractstudy.model.ClassAndVersion;
import contractstudy.model.ProgramVersion;
import contractstudy.scripts.model.ArtefactFactory;
import contractstudy.scripts.model.Experiment;
import contractstudy.scripts.model.ExperimentArtefact;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 *
 */
public class ComputeInheritanceHierarchy implements Experiment {

  private static final File INPUT_SOURCE_CODE = new File(Preferences.getDataFolder());
  private static final File INPUT_CONTRACTS_FOUND = ArtefactFactory.USAGE_CONTRACTS_FOLDER;
  private static final File OUTPUT_ROOT_FOLDER = ArtefactFactory.INHERITANCE_STRUCTURE_FOLDER;
  private static final ProjectVersionHierarchyExtractor extractor = new ProjectVersionHierarchyExtractor();
  private static final Logger LOGGER = Logging.getLogger(ComputeInheritanceHierarchy.class);

  public static void main(String[] args) throws Exception {

    //createAndSaveStructFileForJDK();

    long startTime = System.currentTimeMillis();
    ExecutorService executor = Executors.newFixedThreadPool(Preferences.getThreadCount());
    Collection<File> sourceCodeZips = FileUtils.listFiles(INPUT_SOURCE_CODE, new String[]{"zip"}, true);
    Collection<File> foundContractsJsonFiles = FileUtils.listFiles(INPUT_CONTRACTS_FOUND, new String[]{"json"}, true);

    for (File project : sourceCodeZips) {
      LOGGER.info("Processing: " + project);
      File contractsJsonFile;

      try {
        contractsJsonFile = getContractsFoundJsonFileAssociatedWithProjectZip(foundContractsJsonFiles, project);
      } catch (IndexOutOfBoundsException exception) {
        LOGGER.info("No Contracts JSON file was found for project " + project.getName());
        continue;
      }

      Runnable task = () -> {
        try {
          ProgramVersion version = createProgramVersionFromSourceCodeAndContractsFoundJsonFile(project, contractsJsonFile);
          Map<ClassCoordinates, ClassParents> classesMap = new HashMap<>();

          extractor.analyse(version, new InheritanceResolved() {
            @Override
            public void notify(ClassParents parents) {
              classesMap.put(parents, parents);
            }
            @Override
            public void notify(ClassCoordinates classCoordinates) {
              classesMap.put(classCoordinates, null);
            }
          });

          File outputFolder = new File(OUTPUT_ROOT_FOLDER, project.getName());
          File outputFile = new File(outputFolder, fileName(contractsJsonFile));
          saveResultsToFile(outputFile, classesMap);

        } catch (Exception e) {
          e.printStackTrace();
          LOGGER.info("Skipping incompatible source-code version for " + contractsJsonFile);
        }
      };

      executor.submit(task);

    }

    executor.shutdown();
    executor.awaitTermination(10, TimeUnit.DAYS);
    long endTime = System.currentTimeMillis();

    LOGGER.info("Done!");
    LOGGER.info("\ttime: " + (endTime - startTime) + " ms");
    LOGGER.info("\tthreads used: " + Preferences.getThreadCount());

  }

  private static void createAndSaveStructFileForJDK() throws Exception {
    File jdk = Preferences.getJDKZip();
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

    File outputFolder = new File(OUTPUT_ROOT_FOLDER, "open-jdk-8");
    File outputFile = new File(outputFolder, "open-jdk-8-struct.json");
    saveResultsToFile(outputFile, classesMap);
  }

  private static File getContractsFoundJsonFileAssociatedWithProjectZip(
    Collection<File> foundContractsJsonFile, File sourceCodeZip) throws
    IndexOutOfBoundsException {
    return foundContractsJsonFile.stream().filter(
        contractFile -> doesContractsJsonFileBelongToProjectInSourceCode(contractFile, sourceCodeZip))
      .collect(
        Collectors.toList()).get(0);
  }

  private static boolean doesContractsJsonFileBelongToProjectInSourceCode(File contractJsonFile,
    File sourceCodeZip) {
    String contractFileName = contractJsonFile.getName()
      .substring(0, contractJsonFile.getName().lastIndexOf("."));
    String sourceCodeZipName = sourceCodeZip.getName()
      .substring(0, sourceCodeZip.getName().lastIndexOf("."));
    return contractFileName.equals(sourceCodeZipName);
  }

  private static ProgramVersion createProgramVersionFromSourceCodeAndContractsFoundJsonFile(
    File sourceCodeZip, File foundContractsJsonFile) {
    String jsonFileName = foundContractsJsonFile.getName();
    String projectName = foundContractsJsonFile.getName()
      .substring(0, jsonFileName.lastIndexOf("-"));
    String version = foundContractsJsonFile.getName()
      .substring(jsonFileName.lastIndexOf("-") + 1, jsonFileName.lastIndexOf("."));
    ProgramVersion programVersion = ProgramVersion.getOrCreate(
      projectName,
      version);
    return programVersion.withFile(sourceCodeZip);
  }

  private static void saveResultsToFile(
    final File file,
    final Map<ClassCoordinates, ClassParents> parents
  ) throws Exception {

    file.getParentFile().mkdirs();
    JSONArray a = new JSONArray();
    HashMap<String, String> seenClasses = new HashMap<>();

    for (ClassCoordinates c : parents.keySet()) {
      Set<ClassCoordinates> withInnerSet = new HashSet<>();
      withInnerSet.add(c);
      withInnerSet.addAll(c.getInnerClasses());
      ClassParents classParents = parents.get(c);
      for (ClassCoordinates cc : withInnerSet) {
        if (isClassAlreadySeen(cc, seenClasses)) {
          continue;
        }
        a.put(getClassCoordinate(classParents, cc));
        seenClasses.put(cc.getClassName(), cc.getCuName());
      }
    }
    IOUtils.write(a.toString(), Files.newOutputStream(file.toPath()), "utf-8");
  }

  private static boolean isClassAlreadySeen(ClassCoordinates currentClass,
    HashMap<String, String> pastClasses) {
    return Objects.equals(pastClasses.get(currentClass.getClassName()), currentClass.getCuName());
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
    };
  }

  @Override
  public ExperimentArtefact provides() {
    return ArtefactFactory.classStructure();
  }
}

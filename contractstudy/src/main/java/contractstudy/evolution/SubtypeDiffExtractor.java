package contractstudy.evolution;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimap;
import contractstudy.config.Logging;
import contractstudy.constants.constraint.ConstraintCategory;
import contractstudy.constants.constraint.ContractElement;
import contractstudy.evolution.constants.SubtypeDiffKeys;
import contractstudy.evolution.model.DiffExtractor;
import contractstudy.evolution.model.DiffRecord;
import contractstudy.evolution.model.diffRules.Utils;
import contractstudy.inheritance.model.SuperCallSite;
import contractstudy.model.ClassAndVersion;
import contractstudy.model.ProgramVersion;
import contractstudy.scripts.model.ArtefactFactory;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static contractstudy.utils.CorpusUtils.listJsons;
import static contractstudy.utils.CorpusUtils.listProjects;

/**
 * @author Kamil Jezek [kamil.jezek@verifalabs.com]
 */
public class SubtypeDiffExtractor implements DiffExtractor {

  private static final Logger LOGGER = Logging.getLogger(SubtypeDiffExtractor.class);
  List<ContractElement> contractElements = new ArrayList<>();
  int[] numberRemoved = new int[]{0};

  private static String buildIndexKey(ProgramVersion pv, String cu, String methodDecl) {
    return pv.getName() + "-" + pv.getVersion() + '#' + cu + '/' + (methodDecl == null ? ""
      : methodDecl);
  }

  /**
   * This creates index for the same constraints, which differ only in applied versions. In other words, it captures if the
   * same constraints is added again and again to different versions
   *
   * @return
   */
  private static String getIndexSameConstrDifferentVersion(final ContractElement pc) {
    ProgramVersion pv = pc.getProgramVersion();
    String cu = pc.getCuName();
    String methodDecl = pc.getMethodDeclaration();
    return pv.getName() + '#' + cu + '/' + (methodDecl == null ? "" : methodDecl) + " c:"
      + pc.getCondition() + "," + pc.getKind();
  }

  public static void readInheritanceCSV(
    final HashMultimap<ClassAndVersion, ClassAndVersion> classesThatOverridesMap,
    final Map<ClassAndVersion, Set<String>> methods
  ) throws IOException {
    for (File project : listProjects(ArtefactFactory.INHERITANCE_STRUCTURE_FOLDER)) {
      for (File projectStructFiles : listJsons(project)) {
        loopThroughProjectFilesToCollectMethodsAndParents(classesThatOverridesMap, methods, project, projectStructFiles);
      }
    }
    propagateInheritedMethods(classesThatOverridesMap, methods);
  }

  private static void loopThroughProjectFilesToCollectMethodsAndParents(
    final HashMultimap<ClassAndVersion, ClassAndVersion> inheritanceMap,
    final Map<ClassAndVersion, Set<String>> methods,
    File projectZipFolder,
    File projectStructFiles
  ) throws IOException {
    JSONArray arr = new JSONArray(
      IOUtils.toString(Files.newInputStream(projectStructFiles.toPath()), StandardCharsets.UTF_8));
    String projectName = getProjectNameFromStructFolder(projectZipFolder);
    String versionName = getProjectVersionFromStructFolder(projectZipFolder);

    for (Object anArr : arr) {
      JSONObject o = (JSONObject) anArr;
      ClassAndVersion subTypeTmp = ClassAndVersion.create(
        projectName,
        versionName,
        SubtypeDiffKeys.EMPTY_CLASS_NAME.getKey(),
        o.getString("cuName")
      );

      collectMethods(o, subTypeTmp, methods);
      collectParents(o, subTypeTmp, inheritanceMap);
    }
  }

  private static String getProjectNameFromStructFolder(File projectZipFolder) {
    return projectZipFolder.getName()
      .substring(0, projectZipFolder.getName().lastIndexOf("-"));
  }

  private static String getProjectVersionFromStructFolder(File projectZipFolder) {
    return projectZipFolder.getName()
      .substring(projectZipFolder.getName().lastIndexOf("-") + 1,
        projectZipFolder.getName().lastIndexOf(".zip"));
  }

  private static void collectMethods(
    JSONObject o,
    ClassAndVersion subTypeTmp,
    Map<ClassAndVersion, Set<String>> methods
  ) {
    try {
      JSONArray mm = o.getJSONArray("methods");
      Iterator<Object> itM = mm.iterator();
      Set<String> meth = new HashSet<>();
      while (itM.hasNext()) {
        meth.add(itM.next().toString());
      }
      methods.put(subTypeTmp, meth);
    } catch (JSONException ignored) {
    }
  }

  private static void collectParents(
    JSONObject o,
    ClassAndVersion subTypeTmp,
    HashMultimap<ClassAndVersion, ClassAndVersion> inheritanceMap
  ) {
    JSONArray pp = o.getJSONArray("parents");
    for (Object aPp : pp) {
      JSONObject ppO = (JSONObject) aPp;
      ClassAndVersion superType = ClassAndVersion.fromJson(ppO.toString());
      ClassAndVersion superTypeTmp = new ClassAndVersion(
        SubtypeDiffKeys.EMPTY_CLASS_NAME.getKey(),
        superType.getCuName(),
        superType.getProgramVersion()
      );
      inheritanceMap.put(subTypeTmp, superTypeTmp);
    }
  }


  /**
   * Extend the input map so that each key is enriched with methods inherited from super classes.
   *
   * @param inheritanceMap key - sub class, value - parent class
   * @param methods        key a class, value methods including inherited ones.
   */
  private static void propagateInheritedMethods(
    final HashMultimap<ClassAndVersion, ClassAndVersion> inheritanceMap,
    final Map<ClassAndVersion, Set<String>> methods) {
    for (ClassAndVersion key : methods.keySet()) {
      Set<String> currentMethods = methods.get(key);
      Set<ClassAndVersion> finished = new HashSet<>();
      propagateInheritedMethods(inheritanceMap, methods, key, currentMethods, finished);
    }
  }

  /**
   * Extend the input map so that each key is enriched with methods inherited from super classes.
   */
  private static void propagateInheritedMethods(
    final HashMultimap<ClassAndVersion, ClassAndVersion> inheritanceMap,
    final Map<ClassAndVersion, Set<String>> methods,
    final ClassAndVersion currentType,
    final Set<String> currentMethods,
    final Set<ClassAndVersion> finished
  ) {
    for (ClassAndVersion parent : inheritanceMap.get(currentType)) {
      if (finished.contains(currentType)) {
        LOGGER.debug("The same type already processed, skipping" + currentType);
        continue;
      }

      // Value of inheritanceMap is incorrect.

      finished.add(currentType);
      Set<String> parentMethods = methods.get(parent);

      propagateInheritedMethods(inheritanceMap, methods, parent, parentMethods, finished);

      if (parentMethods != null) {
        currentMethods.addAll(parentMethods);
      }
    }
  }

  @Override
  public List<DiffRecord> extract() throws Exception {
    int total;
    HashMultimap<ClassAndVersion, ClassAndVersion> inheritanceMap = HashMultimap.create();
    Map<ClassAndVersion, Set<String>> methodsMap = new HashMap<>();
    Set<String> processedIndexes = new HashSet<>();
    Set<String> alreadyAnalyzed = new HashSet<>();
    Multimap<String, ContractElement> removed = ArrayListMultimap.create();
    List<DiffRecord> results = new ArrayList<>();

    readInheritanceCSV(inheritanceMap, methodsMap);
    Collection<File> contractsPerProgram = getContractsInProgramsFiles();
    total = filterContracts(contractsPerProgram, removed);
    contractElements.sort(Comparator.comparing(pc -> pc.getProgramVersion().toString()));
    ListMultimap<String, ContractElement> constraintIndex = getConstraintsIndexByMethodOrClass(contractElements);

    for (ContractElement contract : contractElements) {

      String programMethodIdentifier = buildIndexKey(
        contract.getProgramVersion(),
        contract.getCuName(),
        contract.getMethodDeclaration()
      );

      if (alreadyAnalyzed.add(programMethodIdentifier)) {

        ClassAndVersion subClass = ClassAndVersion.fromContractElement(contract);
        Set<ClassAndVersion> parents = inheritanceMap.get(subClass); //getParentsOfSubclass(inheritanceMap, subClass);

        List<ContractElement> constraints2 = constraintIndex.get(programMethodIdentifier);

        if (wasInputConstraintsNotProcessedYet(constraints2, processedIndexes)) {

          for (ClassAndVersion parent : parents) {
            Set<String> methods = methodsMap.get(parent);

            if (methods != null && methods.contains(contract.getMethodDeclaration())) {

              String parentKey = buildIndexKey(parent.getProgramVersion(), parent.getCuName(),
                contract.getMethodDeclaration());
              List<ContractElement> constraints1 = constraintIndex.get(parentKey);

              DiffRecord record = new DiffRecord(
                constraints1,
                parent.getProgramVersion(),
                parent.getCuName(),
                contract.getMethodDeclaration(),
                constraints2,
                contract.getProgramVersion(),
                contract.getCuName(),
                contract.getMethodDeclaration()
              );

              results.add(record);
            }
          }
        }
      }
    }

    outputResultsToConsole(total, contractElements, numberRemoved, removed);
    return results;
  }

  private Collection<File> getContractsInProgramsFiles() {
    return FileUtils.listFiles(
      (ArtefactFactory.USAGE_CONTRACTS_FOLDER),
      new String[]{"json"},
      false
    );
  }

  private int filterContracts(
    Collection<File> contractsPerProgram,
    Multimap<String, ContractElement> removed
  ) throws IOException {
    int total = 0;
    Set<SuperCallSite> superCallSites = collectMethodsWithSuper();
    Set<String> superCallSitesWithClassAndMethodName = new HashSet<>();

    superCallSites.forEach(i -> superCallSitesWithClassAndMethodName.add(i.getCu() + "/" + i.getMethodDeclaration()));

    for (File contractsInProgram : contractsPerProgram) {
      String data = FileUtils.readFileToString(contractsInProgram, StandardCharsets.UTF_8);
      JSONArray all = new JSONArray(data);
      all.forEach(e -> {
        ContractElement c = ContractElement.fromJSON((JSONObject) e);
        if (!toBeExcludedDueToFilter(c, removed, superCallSitesWithClassAndMethodName)) {
          contractElements.add(c);
        } else {
          numberRemoved[0]++;
        }
      });
      total += all.length();
    }

    return total;
  }

  private boolean toBeExcludedDueToFilter(
    final ContractElement contractElement,
    final Multimap<String, ContractElement> removed,
    final Set<String> superCallSites
  ) {
    boolean r = false;
    if (Utils.cannotSort(contractElement.getProgramVersion())) {
      removed.put(SubtypeDiffKeys.REMOVED_SORT.getKey(), contractElement);
      r = true;
    } else if (contractElement.isMethodAbstract()) {
      removed.put(SubtypeDiffKeys.REMOVED_ABSTRACT.getKey(), contractElement);
      r = true;
    } else if (superCallSites.contains(contractElement.getCuName() + "/" + contractElement.getMethodDeclaration())) {
      removed.put(SubtypeDiffKeys.REMOVED_SUPER.getKey(), contractElement);
      r = false;
    } else if (contractElement.getKind().getGroup().getCategory() == ConstraintCategory.ANNOTATION) {
      removed.put(SubtypeDiffKeys.REMOVED_ANNOTATIONS.getKey(), contractElement);
      r = true;
    }
    return r;
  }

  private Set<ClassAndVersion> getParentsOfSubclass(
    HashMultimap<ClassAndVersion, ClassAndVersion> classesThatOverridesMap,
    ClassAndVersion subClass
  ) {
    for (ClassAndVersion inheritanceKey : classesThatOverridesMap.keys()) {
      if (inheritanceKey.getCuName().endsWith(subClass.getCuName()) && Objects.equals(subClass.getClassName(),
        inheritanceKey.getClassName())) {
        return classesThatOverridesMap.get(inheritanceKey);
      }
    }
    return new HashSet<>();
  }

  private ListMultimap<String, ContractElement> getConstraintsIndexByMethodOrClass(
    List<ContractElement> contractElements) {
    ListMultimap<String, ContractElement> constraintIndex = ArrayListMultimap.create();
    for (ContractElement pc : contractElements) {
      constraintIndex.put(
        buildIndexKey(pc.getProgramVersion(), pc.getCuName(), pc.getMethodDeclaration()), pc);
    }
    return constraintIndex;
  }

  private boolean wasInputConstraintsNotProcessedYet(List<ContractElement> contractElements,
    Set<String> processedIndexes) {
    boolean canProcess = false;
    for (ContractElement c : contractElements) {
      String index = getIndexSameConstrDifferentVersion(c);
      canProcess |= !processedIndexes.contains(index);
      processedIndexes.add(index);
    }
    return canProcess;
  }

  private Set<SuperCallSite> collectMethodsWithSuper() throws IOException {
    Set<SuperCallSite> callSites = new HashSet<>();
    File file = ArtefactFactory.INHERITANCE_SUPER_CALL_SITE;
    LineIterator it = IOUtils.lineIterator(Files.newInputStream(file.toPath()), "utf-8");
    it.next();
    while (it.hasNext()) {
      String line = it.next();
      String[] lineComponents = line.split(",");
      if (lineComponents.length == 5) {
        callSites.add(SuperCallSite.fromCSV(lineComponents));
      }
    }
    return callSites;
  }

  private double percent(int total, int value) {
    return (double) value / total * 100;
  }

  private void outputResultsToConsole(int total, List<ContractElement> contractElements,
    int[] numberRemoved, Multimap<String, ContractElement> removed) {
    LOGGER.info("Number of total constraints: " + total);
    LOGGER.info("Number of used constraints: " + contractElements.size());
    LOGGER.info("Number of removed constrains: " + numberRemoved[0]);
    LOGGER.info("\tDetail: ");
    for (String key : removed.keySet()) {
      int value = removed.get(key).size();
      LOGGER.info("\t\t" + key + ": " + value + " (" + percent(total, value) + "%)");
    }
  }

}

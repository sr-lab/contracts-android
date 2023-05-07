package contractstudy.collectContracts.annotation.java;

import contractstudy.constants.constraint.ConstraintCollector;
import contractstudy.constants.constraint.ConstraintType;
import contractstudy.constants.constraint.ConstraintedArtefact;
import contractstudy.constants.constraint.ContractElement;
import contractstudy.usage.collectContracts.annotation.AndroidAnnotationExtractor;
import contractstudy.usage.collectContracts.annotation.JSR303Extractor;
import contractstudy.utils.Utils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


public class TestAbstractAnnotationExtractor {

  private static final File TEST_DATA_FOLDER = new File(
    Utils.getBasePathTestFolder() + "collectContracts/annotation/java/testData");

  private static Stream<Arguments> generatorJSR305Extractor() {
    return Stream.of(
      Arguments.of(ConstraintType.JSR303Min, "AnnotationsMultiple.java", 1),
      Arguments.of(ConstraintType.JSR303Max, "AnnotationsMultiple.java", 1),
      Arguments.of(ConstraintType.JSR303NotNull, "AnnotationsMultiple.java", 3),
      Arguments.of(ConstraintType.JSR303Size, "AnnotationsMultiple.java", 1));
  }

  private static Stream<Arguments> generatorJSR305ExtractorWithWildCardImport() {
    return Stream.of(
      Arguments.of(ConstraintType.JSR303Min, "AnnotationsWildCard.java", 1),
      Arguments.of(ConstraintType.JSR303Max, "AnnotationsWildCard.java", 1),
      Arguments.of(ConstraintType.JSR303NotNull, "AnnotationsWildCard.java", 3),
      Arguments.of(ConstraintType.JSR303Size, "AnnotationsWildCard.java", 1));
  }

  private static Stream<Arguments> generateInputForJSR303ConstraintArtefactTest() {
    return Stream.of(
      Arguments.of(ConstraintType.JSR303NotNull, "AnnotationsArtefact.java", ConstraintedArtefact.CLASS),
      Arguments.of(ConstraintType.JSR303Size, "AnnotationsArtefact.java", ConstraintedArtefact.METHOD),
      Arguments.of(ConstraintType.JSR303Max, "AnnotationsArtefact.java", ConstraintedArtefact.METHOD_PARAMETER)
    );
  }

  private static Stream<Arguments> generateInputForAndroidConstraintArtefactTest() {
    return Stream.of(
      Arguments.of(ConstraintType.AndroidSuppressLint, "AnnotationsArtefact.java", ConstraintedArtefact.METHOD)
    );
  }

  @ParameterizedTest
  @MethodSource("generatorJSR305Extractor")
  public void testJSR305Extractor_whenAnnotationExists_expectListOfAnnotations(
    ConstraintType constraintType, String fileName, int count) throws Exception {
    File file = new File(TEST_DATA_FOLDER, fileName);
    ConstraintCollector collector = new ConstraintCollector();
    JSR303Extractor jSR303Extractor = new JSR303Extractor();

    jSR303Extractor.analyse(Utils.getInputStream(file), "test", "<no version>", file.getName(),
      collector);

    List<ContractElement> contractsFound = collector
      .getContractElements()
      .stream()
      .filter(c -> c.getKind().equals(constraintType))
      .collect(Collectors.toList());

    assertNotNull(contractsFound);
    assertEquals(count, contractsFound.size());
  }

  @ParameterizedTest
  @MethodSource("generatorJSR305Extractor")
  public void testJSR305Extractor_whenAnnotationExistsWithWildCardImport_expectListOfAnnotations(
    ConstraintType constraintType, String fileName, int count) throws Exception {

    File file = new File(TEST_DATA_FOLDER, fileName);
    ConstraintCollector collector = new ConstraintCollector();
    JSR303Extractor jSR303Extractor = new JSR303Extractor();

    jSR303Extractor.analyse(Utils.getInputStream(file), "test", "<no version>", file.getName(),
      collector);

    List<ContractElement> contractsFound = collector
      .getContractElements()
      .stream()
      .filter(c -> c.getKind().equals(constraintType))
      .collect(Collectors.toList());

    assertNotNull(contractsFound);
    assertEquals(count, contractsFound.size());
  }

  @Test
  public void testJSR305Extractor_whenAnnotationNotExists_expectEmptyList() throws Exception {
    File file = new File(TEST_DATA_FOLDER, "AnnotationsMultiple.java");
    ConstraintCollector collector = new ConstraintCollector();
    JSR303Extractor jSR303Extractor = new JSR303Extractor();

    jSR303Extractor.analyse(Utils.getInputStream(file), "test", "<no version>", file.getName(),
      collector);

    List<ContractElement> contractsFound = collector
      .getContractElements()
      .stream()
      .filter(c -> c.getKind().equals(ConstraintType.JSR303Null))
      .collect(Collectors.toList());

    assertNotNull(contractsFound);
    assertEquals(0, contractsFound.size());
  }

  @ParameterizedTest
  @MethodSource("generatorJSR305ExtractorWithWildCardImport")
  public void testJSR305Extractor_whenAnnotationExistsWithWildCard_expectListOfAnnotations(
    ConstraintType constraintType,
    String fileName,
    int count
  ) throws Exception {
    File file = new File(TEST_DATA_FOLDER, fileName);
    ConstraintCollector collector = new ConstraintCollector();
    JSR303Extractor jSR303Extractor = new JSR303Extractor();

    jSR303Extractor.analyse(Utils.getInputStream(file), "test", "<no version>", file.getName(),
      collector);

    List<ContractElement> contractsFound = collector
      .getContractElements()
      .stream()
      .filter(c -> c.getKind().equals(constraintType))
      .collect(Collectors.toList());

    assertNotNull(contractsFound);
    assertEquals(count, contractsFound.size());
  }

  @Test
  public void testJSR305Extractor_whenStaticImportExists_expectEmptyList() throws Exception {
    File file = new File(TEST_DATA_FOLDER, "AnnotationsStaticImport.java");
    ConstraintCollector collector = new ConstraintCollector();
    JSR303Extractor jSR303Extractor = new JSR303Extractor();

    jSR303Extractor.analyse(Utils.getInputStream(file), "test", "<no version>", file.getName(),
      collector);

    List<ContractElement> contractsFound = collector
      .getContractElements()
      .stream()
      .filter(c -> c.getKind().equals(ConstraintType.JSR303Null))
      .collect(Collectors.toList());

    assertNotNull(contractsFound);
    assertEquals(0, contractsFound.size());
  }

  @ParameterizedTest
  @MethodSource("generateInputForJSR303ConstraintArtefactTest")
  public void testConstraintArtefact_whenJSR303ExtractorAnnotations_expectCorrectArtefactAssociation(
    ConstraintType constraintType,
    String fileName,
    ConstraintedArtefact constraintedArtefact
  ) throws Exception {
    File file = new File(TEST_DATA_FOLDER, fileName);
    ConstraintCollector collector = new ConstraintCollector();
    JSR303Extractor jSR303Extractor = new JSR303Extractor();

    jSR303Extractor.analyse(Utils.getInputStream(file), "test", "<no version>", file.getName(),
      collector);

    List<ContractElement> contractsFound = collector
      .getContractElements()
      .stream()
      .filter(c -> c.getKind().equals(constraintType))
      .collect(Collectors.toList());

    assertNotNull(contractsFound.get(0));
    assertEquals(constraintedArtefact, contractsFound.get(0).getConstraintedArtefact());
  }

  @ParameterizedTest
  @MethodSource("generateInputForAndroidConstraintArtefactTest")
  public void testConstraintArtefact_whenAndroidExtractorAnnotations_expectCorrectArtefactAssociation(
    ConstraintType constraintType,
    String fileName,
    ConstraintedArtefact constraintedArtefact
  ) throws Exception {
    File file = new File(TEST_DATA_FOLDER, fileName);
    ConstraintCollector collector = new ConstraintCollector();
    AndroidAnnotationExtractor androidAnnotationExtractor = new AndroidAnnotationExtractor();

    androidAnnotationExtractor.analyse(Utils.getInputStream(file), "test", "<no version>", file.getName(),
      collector);

    List<ContractElement> contractsFound = collector
      .getContractElements()
      .stream()
      .filter(c -> c.getKind().equals(constraintType))
      .collect(Collectors.toList());

    assertNotNull(contractsFound.get(0));
    assertEquals(constraintedArtefact, contractsFound.get(0).getConstraintedArtefact());
  }

}

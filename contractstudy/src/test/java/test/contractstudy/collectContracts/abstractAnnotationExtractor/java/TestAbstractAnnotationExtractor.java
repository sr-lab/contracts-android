package test.contractstudy.collectContracts.abstractAnnotationExtractor.java;

import contractstudy.collectContracts.annotation.JSR303Extractor;
import contractstudy.constants.constraint.ConstraintCollector;
import contractstudy.constants.constraint.ConstraintType;
import contractstudy.constants.constraint.ContractElement;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import test.contractstudy.utils.Utils;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


public class TestAbstractAnnotationExtractor {

  private static final File TEST_DATA_FOLDER = new File(
    Utils.getBasePathTestFolder() + "collectContracts/abstractAnnotationExtractor/java/testData");

  private static Stream<Arguments> generatorJSR305Extractor() {
    return Stream.of(
      Arguments.of(ConstraintType.JSR303Min, "AnnotationsMultiple.java", 1),
      Arguments.of(ConstraintType.JSR303Max, "AnnotationsMultiple.java", 1),
      Arguments.of(ConstraintType.JSR303NotNull, "AnnotationsMultiple.java", 2),
      Arguments.of(ConstraintType.JSR303Size, "AnnotationsMultiple.java", 1));
  }

  private static Stream<Arguments> generatorJSR305ExtractorWithWildCardImport() {
    return Stream.of(
      Arguments.of(ConstraintType.JSR303Min, "AnnotationsWildCard.java", 1),
      Arguments.of(ConstraintType.JSR303Max, "AnnotationsWildCard.java", 1),
      Arguments.of(ConstraintType.JSR303NotNull, "AnnotationsWildCard.java", 2),
      Arguments.of(ConstraintType.JSR303Size, "AnnotationsWildCard.java", 1));
  }

  @ParameterizedTest
  @MethodSource("generatorJSR305Extractor")
  public void testJSR305Extractor_whenAnnotationExists_expectListOfAnnotations(
    ConstraintType constraintType, String fileName, int count) throws Exception {
    //given
    File file = new File(TEST_DATA_FOLDER, fileName);
    ConstraintCollector collector = new ConstraintCollector();
    JSR303Extractor jSR303Extractor = new JSR303Extractor();

    //when
    jSR303Extractor.analyse(Utils.getInputStream(file), "test", "<no version>", file.getName(),
      collector);

    //assert
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

    //given
    File file = new File(TEST_DATA_FOLDER, fileName);
    ConstraintCollector collector = new ConstraintCollector();
    JSR303Extractor jSR303Extractor = new JSR303Extractor();

    //when
    jSR303Extractor.analyse(Utils.getInputStream(file), "test", "<no version>", file.getName(),
      collector);

    //assert
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
    //given
    File file = new File(TEST_DATA_FOLDER, "AnnotationsMultiple.java");
    ConstraintCollector collector = new ConstraintCollector();
    JSR303Extractor jSR303Extractor = new JSR303Extractor();

    //when
    jSR303Extractor.analyse(Utils.getInputStream(file), "test", "<no version>", file.getName(),
      collector);

    //assert
    List<ContractElement> contractsFound = collector
      .getContractElements()
      .stream()
      .filter(c -> c.getKind().equals(ConstraintType.JSR303Null))
      .collect(Collectors.toList());

    assertNotNull(contractsFound);
    assertEquals(0, contractsFound.size());
  }

  @Test
  public void testJSR305Extractor_whenAnnotationExistsWithWildCard_expectListOfAnnotations()
    throws Exception {
    //given
    File file = new File(TEST_DATA_FOLDER, "AnnotationsWildCard.java");
    ConstraintCollector collector = new ConstraintCollector();
    JSR303Extractor jSR303Extractor = new JSR303Extractor();

    //when
    jSR303Extractor.analyse(Utils.getInputStream(file), "test", "<no version>", file.getName(),
      collector);

    //assert
    List<ContractElement> contractsFound = collector
      .getContractElements()
      .stream()
      .filter(c -> c.getKind().equals(ConstraintType.JSR303Min))
      .collect(Collectors.toList());

    assertNotNull(contractsFound);
    assertNotEquals(0, contractsFound.size());
  }


  @Test
  public void testJSR305Extractor_whenStaticImportExists_expectEmptyList() throws Exception {
    //given
    File file = new File(TEST_DATA_FOLDER, "AnnotationsStaticImport.java");
    ConstraintCollector collector = new ConstraintCollector();
    JSR303Extractor jSR303Extractor = new JSR303Extractor();

    //when
    jSR303Extractor.analyse(Utils.getInputStream(file), "test", "<no version>", file.getName(),
      collector);

    //assert
    List<ContractElement> contractsFound = collector
      .getContractElements()
      .stream()
      .filter(c -> c.getKind().equals(ConstraintType.JSR303Null))
      .collect(Collectors.toList());

    assertNotNull(contractsFound);
    assertEquals(0, contractsFound.size());
  }


}

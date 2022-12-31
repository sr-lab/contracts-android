package contractstudy.hierarchy.SuperCallSiteExtractor.java;

import contractstudy.model.ProgramVersion;
import contractstudy.hierarchy.SuperCallSiteExtractor.SuperCallSiteExtractor;
import contractstudy.hierarchy.model.SuperCallSite;
import contractstudy.utils.Utils;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class TestSuperCallSiteExtractor {

  private static final File TEST_DATA_FOLDER = new File(
    Utils.getBasePathTestFolder() + "hierarchy/SuperCallSiteExtractor/java/testData");

  private static Stream<Arguments> generateFiles() {
    return Stream.of(
      Arguments.of("SubClass1.java", 2, 1));
  }

  @ParameterizedTest
  @MethodSource("generateFiles")
  public void testDataCollectionVisitorExtractor(String fileName, int expectedMethods, int expectedConstructors) throws Exception {
    File file = new File(TEST_DATA_FOLDER, fileName);
    InputStream in = Utils.getInputStream(file);
    SuperCallSiteExtractor extractor = new SuperCallSiteExtractor();
    ProgramVersion programVersion = new ProgramVersion("test", "test", file);
    List<SuperCallSite> superCallSites = new ArrayList<>();

    extractor.analyse(in, superCallSites, programVersion, file.getName());

    assertEquals(expectedMethods + expectedConstructors, superCallSites.size());
    assertEquals(expectedMethods, superCallSites.stream().filter(x -> x.isMethod).count());
    assertEquals(expectedConstructors, superCallSites.stream().filter(x -> !x.isMethod).count());
  }
}
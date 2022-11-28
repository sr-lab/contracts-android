package test.contractstudy.utilsTest;

import contractstudy.utils.StringUtils;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class StringUtilsTest {


  private static Stream<Arguments> generateStringsWithComments() {
    return Stream.of(
      Arguments.of("import java.method.*", "import java.method.*"),
      Arguments.of("import java.method.* // nice", "import java.method.*"),
      Arguments.of("import java.method.* //nice", "import java.method.*"),
      Arguments.of("import /*cool*/ java.method.*", "import  java.method.*"));
  }

  @ParameterizedTest
  @MethodSource("generateStringsWithComments")
  public void testRemoveComments(String originalStr, String expectedStr) {
    //when
    String result = StringUtils.removeComments(originalStr);
    //assert
    assertNotNull(result);
    assertEquals(expectedStr, result);
  }

}

package contractstudy.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

public class Utils {

  public static String getBasePathTestFolder() {
    return "src/test/java/contractstudy/";
  }

  public static InputStream getInputStream(File file) throws IOException {
    return Files.newInputStream(file.toPath());
  }
}

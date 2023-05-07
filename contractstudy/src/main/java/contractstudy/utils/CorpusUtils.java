package contractstudy.utils;

import java.io.File;

public class CorpusUtils {

  public static File[] listProjects(File root) {
    return root.listFiles(File::isDirectory);
  }

  public static File[] listJsons(File project) {
    return project.listFiles(pathname -> pathname.getName().endsWith(".json"));
  }

}

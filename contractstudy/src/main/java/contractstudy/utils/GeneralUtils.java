package contractstudy.utils;

import java.util.ArrayList;
import java.util.List;

public class GeneralUtils {

  static public List<String> getImplicitImportsForPackageName(String packageName) {
    return new ArrayList<>() {{
      add(packageName + ".*");
      add("kotlin.*");
      add("java.lang.*");
    }};
  }

}

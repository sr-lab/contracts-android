package contractstudy.constants;

public enum SetStatsDataKeys {
  PUBLIC_METHODS("public and protected methods"),
  PUBLIC_CONSTRUCTORS("public and protected constructors"),
  COMPILATION_UNITS("compilation units"),
  COMPILATION_UNITS_PARSING_FAILED("compilation units (parsing failed)"),
  CLASSES("classes"),
  ALL_METHODS("methods"),
  ALL_CONSTRUCTORS("constructors"),
  PROGRAMS("programs"),
  VERSIONS("versions"),
  LOC("lines of code");

  private final String key;

  SetStatsDataKeys(String key) {
    this.key = key;
  }

  public String getKey() {
    return key;
  }
}

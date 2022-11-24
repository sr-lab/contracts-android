package contractstudy.utils;

public final class LanguageUtils {

  public static Language getLanguageFromNameExtension(String name) {
    for (Language language : Language.values()) {
      if (name.endsWith(language.getExtension())) {
        return language;
      }
    }
    return Language.INVALID;
  }

  public static Boolean isLanguageSupportedFromNameExtension(String name) {
    return getLanguageFromNameExtension(name) != Language.INVALID;
  }

  public enum Language {
    KOTLIN(".kt"),
    JAVA(".java"),
    INVALID("");
    private final String extension;

    Language(String extension) {
      this.extension = extension;
    }

    public String getExtension() {
      return extension;
    }
  }

}

package contractstudy.constants;

/**
 * Used to replace boolean logic where the state is sometimes unknown. TVL - three-values logic.
 *
 * @author jens dietrich
 */
public enum VisibilityModifier {
  PRIVATE("private"), PROTECTED("protected"), INTERNAL("internal"), PUBLIC("public"), NONE("none");
  private final String keyword;

  VisibilityModifier(String keyword) {
    this.keyword = keyword;
  }

  public static VisibilityModifier getVisibilityModifierFromKeyword(String keyword) {
    if (keyword == null) {
      return NONE;
    }
    switch (keyword) {
      case "private":
        return PRIVATE;
      case "protected":
        return PROTECTED;
      case "internal":
        return INTERNAL;
      case "public":
        return PUBLIC;
    }
    return NONE;
  }

}

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
    //FIXME: An override method can either be protected or public.
    //FIXME: In Kotlin, the default visibility for a method in an inner interface is protected.
    if (keyword == null) {
      return NONE;
    }
    if (keyword.endsWith("private")) {
      return PRIVATE;
    }
    if (keyword.endsWith("protected")) {
      return PROTECTED;
    }
    if (keyword.endsWith("override")) {
      return PROTECTED;
    }
    if (keyword.endsWith("internal")) {
      return INTERNAL;
    }
    return PUBLIC;
  }

}

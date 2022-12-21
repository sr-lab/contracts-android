package contractstudy.evolution;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Kamil Jezek [kamil.jezek@verifalabs.com]
 */
public class ASTState {

  /**
   * Methods.
   */
  private final Set<String> methods = new HashSet<>();

  /**
   * Result with parents.
   */
  private final Set<ClassAndVersion> parents = new HashSet<>();

  public Set<String> getMethods() {
    return methods;
  }

  public Set<ClassAndVersion> getParents() {
    return parents;
  }
}

package contractstudy.inheritance.model;

import contractstudy.model.ClassAndVersion;
import lombok.Getter;

import java.util.HashSet;
import java.util.Set;

@Getter
public class ASTState {

  /**
   * Methods.
   */
  private final Set<String> methods = new HashSet<>();

  /**
   * Result with parents.
   */
  private final Set<ClassAndVersion> parents = new HashSet<>();

}

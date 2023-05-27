package contractstudy.inheritance.model;

import contractstudy.model.ProgramVersion;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class SuperCallSite {

  public ProgramVersion programVersion;
  public String cu;
  public String methodDeclaration;
  /**
   * True if method. False if constructor.
   */
  public boolean isMethod = true;

  public static SuperCallSite fromCSV(String[] lineComponents) {
    ProgramVersion v = ProgramVersion.getOrCreate(lineComponents[0], lineComponents[1]);
    String cu = lineComponents[2];
    boolean isMethod = lineComponents[4].equals("method");
    String methodDec = lineComponents[3];
    return new SuperCallSite(v, cu, methodDec, isMethod);
  }

}

package contractstudy.hierarchy.model;

import contractstudy.ProgramVersion;
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

  public static SuperCallSite fromCSV(String line) {
    String[] items = line.split(",");
    ProgramVersion v = ProgramVersion.getOrCreate(items[0], items[1]);
    String cu = items[2];
    boolean isMethod = items[4].equals("method");
    String methodDec = items[3];
    return new SuperCallSite(v, cu, methodDec, isMethod);
  }

}

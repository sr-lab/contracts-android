package contractstudy.hierarchy.model;

import contractstudy.ProgramVersion;

public class SuperCallSite {

  public ProgramVersion programVersion = null;
  public String cu = null;
  public String methodDecl = null;
  public boolean isMethod = true; // constructor if false

  public SuperCallSite(ProgramVersion programVersion, String cu, String methodDecl,
    boolean isMethod) {
    this.programVersion = programVersion;
    this.cu = cu;
    this.methodDecl = methodDecl;
    this.isMethod = isMethod;
  }

  public static SuperCallSite fromCSV(String line) {
    String[] items = line.split("\t");
    ProgramVersion v = ProgramVersion.getOrCreate(items[0], items[1]);
    String cu = items[2];
    boolean isMethod = items[4].equals("method");
    String methodDec = items[3];

    return new SuperCallSite(v, cu, methodDec, isMethod);
  }

  public String getMethodDecl() {
    return methodDecl;
  }

  public String getCu() {
    return cu;
  }
}

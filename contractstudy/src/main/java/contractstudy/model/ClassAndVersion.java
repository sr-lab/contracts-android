package contractstudy.model;

import contractstudy.ProgramVersion;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Objects;

/**
 * @author Kamil Jezek [kamil.jezek@verifalabs.com]
 */
public class ClassAndVersion {

  private final String className;
  private final String cuName;
  private final ProgramVersion programVersion;

  public ClassAndVersion(
    final ProgramVersion programVersion,
    final String className,
    final String cuName) {

    this.className = className;
    this.cuName = cuName;
    this.programVersion = programVersion;
  }


  public static ClassAndVersion create(
    final String programName,
    final String programVersion,
    final String className,
    final String cuName) {

    ProgramVersion v = ProgramVersion.getOrCreate(programName, programVersion);
    return new ClassAndVersion(v, className, cuName);
  }

  public static ClassAndVersion fromJson(String json) throws IOException {
    JSONObject o = new JSONObject(json);

    return create(
      o.getString("programName"),
      o.getString("programVersion"),
      o.getString("className"),
      o.getString("cuName")
    );
  }

  public String getClassName() {
    return className;
  }

  public ProgramVersion getProgramVersion() {
    return programVersion;
  }

  public String getCuName() {
    return cuName;
  }

  public String toJson() {
    JSONObject o = new JSONObject();
    o.put("className", className);
    o.put("cuName", cuName);
    o.put("programName", programVersion.getName());
    o.put("programVersion", programVersion.getVersion());

    return o.toString();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof ClassAndVersion)) {
      return false;
    }

    ClassAndVersion that = (ClassAndVersion) o;

    if (!Objects.equals(className, that.className)) {
      return false;
    }
    if (!Objects.equals(cuName, that.cuName)) {
      return false;
    }
    return Objects.equals(programVersion, that.programVersion);

  }

  @Override
  public int hashCode() {
    int result = className != null ? className.hashCode() : 0;
    result = 31 * result + (cuName != null ? cuName.hashCode() : 0);
    result = 31 * result + (programVersion != null ? programVersion.hashCode() : 0);
    return result;
  }

  @Override
  public String toString() {
    return "" + className +
      ", (" + cuName + ") " +
      ", v: " + programVersion;
  }


}

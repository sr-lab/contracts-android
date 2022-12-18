package contractstudy.hierarchy.SuperCallSiteExtractor;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import contractstudy.ProgramVersion;
import contractstudy.hierarchy.SuperCallSiteExtractor.visitor.MethodVisitorToCollectOverride;
import contractstudy.hierarchy.model.SuperCallSite;
import contractstudy.utils.LanguageUtils;

import java.io.InputStream;
import java.util.List;

public class SuperCallSiteExtractor extends VoidVisitorAdapter<Object> {

  public void analyse(
    InputStream in,
    List<SuperCallSite> superCallSites,
    ProgramVersion programVersion,
    String cuName
  ) {
    try {
      switch (LanguageUtils.getLanguageFromNameExtension(cuName)) {
        case JAVA:
          analyseJava(in, superCallSites, programVersion, cuName);
          break;
        case KOTLIN:
          analyseKotlin(in, superCallSites, programVersion, cuName);
          break;
        default:
      }
    } catch (Exception t) {
      System.out.println("Error"); // TODO: Improve.
    }
  }

  public void analyseJava(
    InputStream in,
    List<SuperCallSite> superCallSites,
    ProgramVersion programVersion,
    String cuName) {
    CompilationUnit cu = StaticJavaParser.parse(in);
    new MethodVisitorToCollectOverride(cuName, programVersion, superCallSites).visit(cu, null);
  }

  public void analyseKotlin(
    InputStream in,
    List<SuperCallSite> superCallSites,
    ProgramVersion programVersion,
    String cuName) {
    //TODO: For Kotlin.
  }


}

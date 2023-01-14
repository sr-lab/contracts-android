package contractstudy.inheritance.SuperCallSiteExtractor;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import contractstudy.inheritance.SuperCallSiteExtractor.visitor.MethodVisitorToCollectSuperCall;
import contractstudy.inheritance.SuperCallSiteExtractor.visitor.MethodVisitorToCollectSuperCallKotlin;
import contractstudy.inheritance.model.SuperCallSite;
import contractstudy.model.ProgramVersion;
import contractstudy.utils.InputStreamToStringConversion;
import contractstudy.utils.LanguageUtils;
import contractstudy.utils.kotlinParser.KotlinParser;
import org.jetbrains.kotlin.com.intellij.psi.PsiFile;

import java.io.IOException;
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
      System.out.println("Error while analysing SuperCallSiteExtract for " + cuName);
    }
  }

  public void analyseJava(
    InputStream in,
    List<SuperCallSite> superCallSites,
    ProgramVersion programVersion,
    String cuName) {
    CompilationUnit cu = StaticJavaParser.parse(in);
    new MethodVisitorToCollectSuperCall(cuName, programVersion, superCallSites).visit(cu, null);
  }

  public void analyseKotlin(
    InputStream in,
    List<SuperCallSite> superCallSites,
    ProgramVersion programVersion,
    String cuName) throws IOException {
    String src = new InputStreamToStringConversion(in).getResult();
    PsiFile psiFile = new KotlinParser().createKtFile(cuName, src);
    MethodVisitorToCollectSuperCallKotlin visitor = new MethodVisitorToCollectSuperCallKotlin(
      cuName, programVersion, superCallSites);
    psiFile.accept(visitor);
  }


}

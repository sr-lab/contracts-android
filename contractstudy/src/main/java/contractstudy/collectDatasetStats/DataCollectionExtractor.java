package contractstudy.collectDatasetStats;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import contractstudy.collectDatasetStats.visitor.DataCollectionVisitor;
import contractstudy.collectDatasetStats.visitor.DataCollectionVisitorKotlin;
import contractstudy.kotlinParser.KotlinParser;
import contractstudy.kotlinParser.KotlinParserUtils;
import contractstudy.utils.InputStreamToStringConversion;
import contractstudy.utils.LanguageUtils;
import org.jetbrains.kotlin.com.intellij.psi.PsiFile;

import java.io.InputStream;
import java.util.Map;

import static contractstudy.constants.SetStatsDataKeys.COMPILATION_UNITS;
import static contractstudy.constants.SetStatsDataKeys.LOC;

public class DataCollectionExtractor {

  public void analyse(
    final String cuName,
    final InputStream in,
    final Map<String, Integer> data) throws Exception {
    switch (LanguageUtils.getLanguageFromNameExtension(cuName)) {
      case JAVA:
        analyseJava(in, data);
        break;
      case KOTLIN:
        analyseKotlin(in, data);
        break;
      default:
    }
  }

  private void analyseJava(
    final InputStream in,
    final Map<String, Integer> data) throws Exception {
    CompilationUnit cu = StaticJavaParser.parse(in);
    data.compute(COMPILATION_UNITS.getKey(), (k, v) -> v == null ? 1 : v + 1);
    int size = cu.getEnd().get().line - cu.getBegin().get().line;
    data.compute(LOC.getKey(), (k, v) -> v == null ? size : v + size);
    new DataCollectionVisitor(data).visit(cu, null);
  }

  private void analyseKotlin(
    final InputStream in,
    final Map<String, Integer> data) throws Exception {
    String src = new InputStreamToStringConversion(in).getResult();
    PsiFile psiFile = new KotlinParser().createKtFile("", src);
    data.compute(COMPILATION_UNITS.getKey(), (k, v) -> v == null ? 1 : v + 1);
    int size = KotlinParserUtils.getElementLineCount(psiFile);
    data.compute(LOC.getKey(), (k, v) -> v == null ? size : v + size);
    DataCollectionVisitorKotlin visitor = new DataCollectionVisitorKotlin(data);
    psiFile.accept(visitor);
  }
}

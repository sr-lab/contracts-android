package contractstudy.kotlinParser;

import org.jetbrains.kotlin.com.intellij.openapi.editor.Document;
import org.jetbrains.kotlin.com.intellij.psi.FileViewProvider;
import org.jetbrains.kotlin.com.intellij.psi.PsiFile;
import org.jetbrains.kotlin.psi.KtElement;

public class KotlinParserUtils {

  public static int getElementBeginLine(KtElement ktElement) {
    PsiFile containingFile = ktElement.getContainingFile();
    FileViewProvider fileViewProvider = containingFile.getViewProvider();
    Document document = fileViewProvider.getDocument();
    int textOffset = ktElement.getTextOffset();
    return document.getLineNumber(textOffset) + 1;
  }
}

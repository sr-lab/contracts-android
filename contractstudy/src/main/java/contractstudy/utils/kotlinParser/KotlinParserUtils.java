package contractstudy.utils.kotlinParser;

import contractstudy.constants.VisibilityModifier;
import org.jetbrains.kotlin.com.intellij.openapi.editor.Document;
import org.jetbrains.kotlin.com.intellij.psi.FileViewProvider;
import org.jetbrains.kotlin.com.intellij.psi.PsiFile;
import org.jetbrains.kotlin.psi.KtElement;
import org.jetbrains.kotlin.psi.KtImportDirective;
import org.jetbrains.kotlin.psi.KtModifierList;

import java.util.Objects;

public class KotlinParserUtils {


  public static int getElementLineCount(KtElement ktElement) {
    int startLine = getElementBeginLine(ktElement);
    Document document = getDocument(ktElement);
    int textLength = ktElement.getTextLength();
    return document.getLineNumber(textLength) + 1 + startLine;
  }

  public static int getElementBeginLine(KtElement ktElement) {
    Document document = getDocument(ktElement);
    int textOffset = ktElement.getTextOffset();
    return document.getLineNumber(textOffset) + 1;
  }

  public static int getElementLineCount(PsiFile psiFile) {
    int startLine = getElementBeginLine(psiFile);
    Document document = getDocument(psiFile);
    int textLength = psiFile.getTextLength();
    return document.getLineNumber(textLength) + startLine;
  }

  public static int getElementBeginLine(PsiFile psiFile) {
    Document document = getDocument(psiFile);
    int textOffset = psiFile.getTextOffset();
    return document.getLineNumber(textOffset) + 1;
  }

  private static Document getDocument(KtElement ktElement) {
    PsiFile containingFile = ktElement.getContainingFile();
    return getDocument(containingFile);
  }

  private static Document getDocument(PsiFile psiFile) {
    FileViewProvider fileViewProvider = psiFile.getViewProvider();
    return fileViewProvider.getDocument();
  }

  public static VisibilityModifier getVisibilityModifier(KtModifierList ktModifierList) {
    String visibilityKeyword = "public";
    if (ktModifierList != null && ktModifierList.getText() != null) {
      visibilityKeyword = ktModifierList.getText();
    }
    return VisibilityModifier.getVisibilityModifierFromKeyword(visibilityKeyword);
  }

  public static boolean doesImportDirectiveContainsWildCard(KtImportDirective importDirective) {
    try {
      return Objects.requireNonNull(importDirective.getImportPath()).getPathStr().contains(".*");
    } catch (NullPointerException exception) {
      return false;
    }
  }

  public static String getImportedName(KtImportDirective importDirective) {
    String importedName = null;
    try {
      importedName = Objects.requireNonNull(importDirective.getImportedName()).toString();
    } catch (NullPointerException e) {
      if (KotlinParserUtils.doesImportDirectiveContainsWildCard(importDirective)) {
        importedName = ".*";
      }
    }
    return importedName;
  }
}

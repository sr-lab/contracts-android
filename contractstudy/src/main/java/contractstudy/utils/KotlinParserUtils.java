package contractstudy.utils;

import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.NodeList;
import contractstudy.config.Preferences;
import contractstudy.constants.VisibilityModifier;
import org.jetbrains.kotlin.com.intellij.openapi.editor.Document;
import org.jetbrains.kotlin.com.intellij.psi.FileViewProvider;
import org.jetbrains.kotlin.com.intellij.psi.PsiFile;
import org.jetbrains.kotlin.psi.KtElement;
import org.jetbrains.kotlin.psi.KtImportDirective;
import org.jetbrains.kotlin.psi.KtModifierList;
import org.jetbrains.kotlin.psi.KtParameter;

import java.util.List;
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

  public static boolean isMethodVisibilityAccepted(KtModifierList ktModifierList) {
    VisibilityModifier visibility = getVisibilityModifier(ktModifierList);
    return (
      visibility == VisibilityModifier.PROTECTED ||
        visibility == VisibilityModifier.PUBLIC ||
        visibility == VisibilityModifier.INTERNAL ||
        (Preferences.includePrivateMethods() && visibility == VisibilityModifier.PRIVATE)
    );
  }

  public static boolean isJavaMethodVisibilityAccepted(NodeList<Modifier> modifiers) {
    return (
      modifiers.contains(Modifier.publicModifier()) ||
        modifiers.contains(Modifier.protectedModifier()) ||
        (Preferences.includePrivateMethods() && modifiers.contains(Modifier.privateModifier()))
    );
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
    String importedName;
    String importText = importDirective.getText();
    if (importText.endsWith(".*")) {
      importedName = importText.substring(0, importText.lastIndexOf("."));
      importedName = importedName.substring(importedName.lastIndexOf(".")+1);
      importedName += ".*";
    } else {
      importedName = importText.substring(importText.lastIndexOf(".")+1);
    }
    return importedName;
  }

  public static String getDeclaration(String expressionName, List<KtParameter> parameters) {
    StringBuilder declaration = new StringBuilder(expressionName);
    declaration.append("(");
    for (int i = 0; i < parameters.size(); i++) {
      String parameterString = parameters.get(i).getText();
      declaration.append(parameterString, parameterString.indexOf(":") + 1, parameterString.length());
      if (i + 1 != parameters.size()) {
        declaration.append(",");
      }
    }
    declaration.append(")");
    return declaration.toString().replace(" ", "");
  }
}

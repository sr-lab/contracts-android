package contractstudy.usage.collectContracts.cre.JavaCREExtractor.visitor;

import contractstudy.constants.constraint.ConstraintType;
import contractstudy.constants.constraint.ContractElement;
import contractstudy.model.ExtractionListener;
import contractstudy.model.ProgramVersion;
import contractstudy.usage.collectContracts.common.AbstractMethodVisitor.AbstractMethodVisitorKotlin;
import contractstudy.usage.collectContracts.cre.JavaCREExtractor.constants.JavaLangCRE;
import contractstudy.utils.KotlinParserUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.kotlin.com.intellij.psi.PsiElement;
import org.jetbrains.kotlin.psi.KtBlockExpression;
import org.jetbrains.kotlin.psi.KtIfExpression;
import org.jetbrains.kotlin.psi.KtThrowExpression;

import java.util.Objects;

/**
 * Visitor for method nodes in the AST.
 */
@SuppressWarnings("rawtypes")
public class MethodVisitorToCollectJavaCREThrowsKotlin extends AbstractMethodVisitorKotlin {

  public MethodVisitorToCollectJavaCREThrowsKotlin(
    ExtractionListener<ContractElement> consumer,
    String programName, String version, String cuName
  ) {
    super(consumer, programName, version, cuName);
  }


  @Override
  public void visitThrowExpression(@NotNull KtThrowExpression expression) {
    if (!isCRE(expression)) {
      return;
    }

    String exceptionName = getExceptionName(expression);
    ConstraintType kind = JavaLangCRE.getPreconditionTypeFromExceptionName(exceptionName);

    if (kind != null) {
      String ifStatementCondition = extractIfStatementArguments(expression);
      String throwArguments = extractThrowArguments(expression);

      ContractElement p = initConstraint();
      p.setProgramVersion(ProgramVersion.getOrCreate(programName, this.version));
      p.setCuName(this.cuName);
      p.setMethodDeclaration(this.methodDeclaration);
      p.setCondition(ifStatementCondition);
      p.setKind(kind);
      p.setLineNo(KotlinParserUtils.getElementBeginLine(expression));
      p.setAdditionalInfo(throwArguments);

      consumer.constraintFound(p);
    }
    super.visitThrowExpression(expression);
  }


  /**
   * Look for the following pattern: if (<condition>) throw new <exception>(<args>); or if (<condition>) { throw new
   * <exception>(<args>) };
   */
  private boolean isCRE(KtThrowExpression n) {
    PsiElement parent = n.getParent();
    PsiElement grandParent = parent.getParent();
    PsiElement grandGrandParent = grandParent.getParent();
    return (
      (grandParent instanceof KtIfExpression) ||
        (parent instanceof KtBlockExpression && grandGrandParent instanceof KtIfExpression)
    );
  }

  private String getExceptionName(KtThrowExpression n) {
    String name = n.getNode().getLastChildNode().getText();
    if (n.getNode().getLastChildNode().getText().contains("(")) {
      name = n.getNode().getLastChildNode().getText()
        .substring(0, n.getNode().getLastChildNode().getText().indexOf("("));
    }
    if (n.getNode().getLastChildNode().getText().contains(".")) {
      name = n.getNode().getLastChildNode().getText()
        .substring(n.getNode().getLastChildNode().getText().indexOf("."), name.length());
    }
    return name.replace(".", "");
  }

  private String extractThrowArguments(KtThrowExpression expression) {
    String throwArguments = expression.getText().substring(
      expression.getText().indexOf("(") + 1,
      expression.getText().lastIndexOf(")")
    );
    return throwArguments;
  }

  private String extractIfStatementArguments(KtThrowExpression expression) {
    PsiElement element = expression.getParent();
    while (element != null) {
      if (element instanceof KtIfExpression) {
        KtIfExpression ifExpression = (KtIfExpression) element;
        try {
          return Objects.requireNonNull(ifExpression.getCondition()).getText();
        } catch (NullPointerException exception) {
          return "ERROR WHILE GETTING IF CONDITION";
        }
      }
      element = element.getParent();
    }
    return "NONE";
  }

}

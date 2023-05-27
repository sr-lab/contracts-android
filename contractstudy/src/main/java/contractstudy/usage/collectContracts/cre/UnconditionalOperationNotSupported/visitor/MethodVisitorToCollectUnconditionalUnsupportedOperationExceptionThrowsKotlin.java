package contractstudy.usage.collectContracts.cre.UnconditionalOperationNotSupported.visitor;

import contractstudy.constants.constraint.ConstraintType;
import contractstudy.constants.constraint.ContractElement;
import contractstudy.model.ExtractionListener;
import contractstudy.model.ProgramVersion;
import contractstudy.usage.collectContracts.common.AbstractMethodVisitor.AbstractMethodVisitorKotlin;
import contractstudy.utils.KotlinParserUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.kotlin.com.intellij.psi.PsiElement;
import org.jetbrains.kotlin.psi.KtBlockExpression;
import org.jetbrains.kotlin.psi.KtIfExpression;
import org.jetbrains.kotlin.psi.KtThrowExpression;

import java.util.Objects;

/**
 * Visitor for method nodes in the AST, used to extract unconditional throws of UnsupportedOperationException.
 *
 * @author jens dietrich
 */
@SuppressWarnings("rawtypes")
public class MethodVisitorToCollectUnconditionalUnsupportedOperationExceptionThrowsKotlin extends
  AbstractMethodVisitorKotlin {

  public MethodVisitorToCollectUnconditionalUnsupportedOperationExceptionThrowsKotlin(
    ExtractionListener<ContractElement> consumer,
    String programName,
    String version,
    String cuName
  ) {
    super(consumer, programName, version, cuName);
  }


  @Override
  public void visitThrowExpression(@NotNull KtThrowExpression expression) {
    if (!isCRE(expression)) {
      return;
    }

    String exceptionName = getExceptionName(expression);
    ConstraintType kind = getPreconditionTypeFromExceptionName(exceptionName);

    if (kind != null) {

      String condition = extractIfStatementArguments(expression);
      String throwArguments = extractThrowArguments(expression);

      ContractElement p = initConstraint();

      p.setProgramVersion(ProgramVersion.getOrCreate(programName, this.version));
      p.setCuName(this.cuName);
      p.setMethodDeclaration(this.methodDeclaration);
      p.setCondition(condition);
      p.setKind(kind);
      p.setLineNo(KotlinParserUtils.getElementBeginLine(expression));
      p.setAdditionalInfo(throwArguments);

      consumer.constraintFound(p);
    }

  }

  /**
   * look for the following pattern: if (<condition>) throw new <exception>(<args>);
   */
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
    try {
      String name = n.getNode().getLastChildNode().getText();
      if (name.contains("(")) {
        name = name.substring(0, n.getNode().getLastChildNode().getText().indexOf("("));
      }
      if (name.contains(".")) {
        name = name.substring(name.indexOf("."));
      }
      return name.replace(".", "");
    } catch (StringIndexOutOfBoundsException exc) {
      return "";
    }
  }

  private ConstraintType getPreconditionTypeFromExceptionName(String exceptionName) {
    if (exceptionName.endsWith(UnsupportedOperationException.class.getSimpleName())) {
      return ConstraintType.UCREUnsupportedOperationException;
    }
    return null;
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

  private String extractThrowArguments(KtThrowExpression expression) {
    String throwArguments = expression.getText().substring(
      expression.getText().indexOf("(") + 1,
      expression.getText().lastIndexOf(")")
    );
    return throwArguments;
  }

}

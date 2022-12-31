package contractstudy.collectContracts.cre.UnconditionalOperationNotSupported.visitor;

import contractstudy.model.ExtractionListener;
import contractstudy.model.ProgramVersion;
import contractstudy.collectContracts.common.AbstractMethodVisitor.AbstractMethodVisitorKotlin;
import contractstudy.constants.constraint.ConstraintType;
import contractstudy.constants.constraint.ContractElement;
import contractstudy.utils.kotlinParser.KotlinParserUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.kotlin.psi.KtBlockExpression;
import org.jetbrains.kotlin.psi.KtIfExpression;
import org.jetbrains.kotlin.psi.KtThrowExpression;

/**
 * Visitor for method nodes in the AST, used to extract unconditional throws of
 * UnsupportedOperationException.
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
    String cuName) {
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

      //TODO: Get condition in if-statement.
      // TODO: String additionalInfo = extractArguments(objCreationNode);

      ContractElement p = initConstraint();

      p.setProgramVersion(ProgramVersion.getOrCreate(programName, this.version));
      p.setCuName(this.cuName);
      p.setMethodDeclaration(this.methodDeclaration);
      p.setCondition("TODO:condition"); //TODO
      p.setKind(kind);
      p.setLineNo(KotlinParserUtils.getElementBeginLine(expression));
      p.setAdditionalInfo("TODO:additionalInfo"); //TODO

      consumer.constraintFound(p);
    }

  }

  /**
   * look for the following pattern: if (<condition>) throw new <exception>(<args>);
   */
  private boolean isCRE(KtThrowExpression n) {
    boolean flag = (isObjectCreationExpr(n) &&
      (isParentAnIfStatement(n) || (isParentAnBlockStatement(n) && isGrandFatherAnIfStatement(n))));
    return flag;
  }

  private boolean isObjectCreationExpr(KtThrowExpression n) {
    return n.getNode().getElementType().getDebugName().equals("THROW"); //TODO: CHECK?
  }

  private boolean isParentAnIfStatement(KtThrowExpression n) {
    return n.getNode().getTreeParent() instanceof KtIfExpression; //TODO: CHECK?
  }

  private boolean isParentAnBlockStatement(KtThrowExpression n) {
    return n.getNode().getTreeParent() instanceof KtBlockExpression;
  }

  private boolean isGrandFatherAnIfStatement(KtThrowExpression n) {
    return n.getNode().getTreeParent().getTreeParent().getTreeParent().getElementType()
      .getDebugName().equals("IF"); //TODO: CHECK?
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
    return name; //TODO: Check.
  }

  private ConstraintType getPreconditionTypeFromExceptionName(String exceptionName) {
    if (exceptionName.endsWith(UnsupportedOperationException.class.getSimpleName())) {
      return ConstraintType.UCREUnsupportedOperationException;
    }
    return null;
  }

}

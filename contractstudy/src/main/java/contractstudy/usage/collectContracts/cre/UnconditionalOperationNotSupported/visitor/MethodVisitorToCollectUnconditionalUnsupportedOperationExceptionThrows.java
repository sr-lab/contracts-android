package contractstudy.usage.collectContracts.cre.UnconditionalOperationNotSupported.visitor;

import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.ThrowStmt;
import contractstudy.constants.constraint.ConstraintType;
import contractstudy.constants.constraint.ContractElement;
import contractstudy.model.ExtractionListener;
import contractstudy.model.ProgramVersion;
import contractstudy.usage.collectContracts.common.AbstractMethodVisitor.AbstractMethodVisitor;

/**
 * Visitor for method nodes in the AST, used to extract unconditional throws of
 * UnsupportedOperationException.
 *
 * @author jens dietrich
 */
@SuppressWarnings("rawtypes")
public class MethodVisitorToCollectUnconditionalUnsupportedOperationExceptionThrows extends
  AbstractMethodVisitor {

  public MethodVisitorToCollectUnconditionalUnsupportedOperationExceptionThrows(
    ExtractionListener<ContractElement> consumer,
    String programName,
    String version,
    String cuName) {
    super(consumer, programName, version, cuName);
  }

  @Override
  public void visit(ThrowStmt n, Object arg) {

    if (!isCRE(n)) {
      return;
    }

    IfStmt condNode = getConditionNode(n);
    String condition = extractCondition(condNode);

    ObjectCreationExpr objCreationNode = (ObjectCreationExpr) n.getExpression();  // JFF
    String excTypeName = objCreationNode.getType().getName().getIdentifier(); // JFF: FIXME
    ConstraintType kind = getPreconditionTypeFromExceptionName(excTypeName);

    if (kind != null) {

      String additionalInfo = extractArguments(objCreationNode);

      ContractElement p = initConstraint();

      p.setProgramVersion(ProgramVersion.getOrCreate(programName, this.version));
      p.setCuName(this.cuName);
      p.setMethodDeclaration(this.methodDeclaration);
      p.setCondition(condition);
      p.setKind(kind);
      p.setLineNo(n.getBegin().get().line); // JFF
      p.setAdditionalInfo(additionalInfo);

      consumer.constraintFound(p);
    }

    super.visit(n, arg);
  }

  /**
   * look for the following pattern: if (<condition>) throw new <exception>(<args>);
   */
  private boolean isCRE(ThrowStmt n) {
    return (isObjectCreationExpr(n) &&
      (isParentAnIfStatement(n) || (isParentAnBlockStatement(n) && isGrandFatherAnIfStatement(n))));
  }

  private boolean isObjectCreationExpr(ThrowStmt n) {
    return n.getExpression() instanceof ObjectCreationExpr;
  }

  private boolean isParentAnIfStatement(ThrowStmt n) {
    return n.getParentNode().get() instanceof IfStmt;
  }

  private boolean isParentAnBlockStatement(ThrowStmt n) {
    return n.getParentNode().get() instanceof BlockStmt;
  }

  private boolean isGrandFatherAnIfStatement(ThrowStmt n) {
    return n.getParentNode().get().getParentNode().get() instanceof IfStmt;
  }

  private IfStmt getConditionNode(ThrowStmt n) {
    return n.getParentNode().get() instanceof BlockStmt ?
      (IfStmt) n.getParentNode().get().getParentNode().get()
      : (IfStmt) n.getParentNode().get(); // JFF: FIXME? added get()
  }

  private String extractCondition(IfStmt condNode) {
    String cond = condNode.getCondition().removeComment().toString();

    // if the parent is another conditional, prepend this
    if (condNode.getParentNode().isPresent() && condNode.getParentNode().get() instanceof BlockStmt
      && condNode.getParentNode().get().getParentNode().get() instanceof IfStmt) { // JFF
      String pcond = extractCondition(
        ((IfStmt) condNode.getParentNode().get().getParentNode().get())); // JFF
      cond = pcond + " && " + cond;
    }

    return cond;
  }

  private String extractArguments(ObjectCreationExpr objCreationNode) {
    StringBuilder b = new StringBuilder();
    if (objCreationNode.getArguments() != null) { // JFF
      for (Expression expr : objCreationNode.getArguments()) {
        if (b.length() > 0) {
          b.append(',');
        }
        b.append(expr.toString());
      }
    }
    return b.toString();
  }

  private ConstraintType getPreconditionTypeFromExceptionName(String exceptionName) {
    if (exceptionName.endsWith(UnsupportedOperationException.class.getSimpleName())) {
      return ConstraintType.UCREUnsupportedOperationException;
    }
    return null;
  }

}

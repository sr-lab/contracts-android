package contractstudy.collectContracts.cre.JavaCREExtractor.visitor;

import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import contractstudy.model.ExtractionListener;
import contractstudy.model.ProgramVersion;
import contractstudy.collectContracts.common.AbstractMethodVisitor.AbstractMethodVisitorKotlin;
import contractstudy.collectContracts.cre.JavaCREExtractor.constants.JavaLangCRE;
import contractstudy.constants.constraint.ConstraintType;
import contractstudy.constants.constraint.ContractElement;
import contractstudy.utils.kotlinParser.KotlinParserUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.kotlin.psi.KtBlockExpression;
import org.jetbrains.kotlin.psi.KtIfExpression;
import org.jetbrains.kotlin.psi.KtThrowExpression;

/**
 * Visitor for method nodes in the AST.
 *
 * @author jens dietrich
 */
@SuppressWarnings("rawtypes")
public class MethodVisitorToCollectJavaCREThrowsKotlin extends
  AbstractMethodVisitorKotlin {

  public MethodVisitorToCollectJavaCREThrowsKotlin(
    ExtractionListener<ContractElement> consumer,
    String programName, String version, String cuName) {
    super(consumer, programName, version, cuName);
  }


  @Override
  public void visitThrowExpression(@NotNull KtThrowExpression expression) {

    if (!isCRE(expression)) {
      return;
    }

    String exceptionName = getExceptionName(expression);
    ConstraintType kind = JavaLangCRE.getPreconditionTypeFromExceptionName(
      exceptionName);

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

  private KtIfExpression getConditionNode(KtThrowExpression n) {
    return n.getNode().getTreeParent() instanceof KtBlockExpression ?
      (KtIfExpression) n.getNode().getTreeParent().getTreeParent()
      : (KtIfExpression) n.getNode().getTreeParent(); // TODO: Fix
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

}

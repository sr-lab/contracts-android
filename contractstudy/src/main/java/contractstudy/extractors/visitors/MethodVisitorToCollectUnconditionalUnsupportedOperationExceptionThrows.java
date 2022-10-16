package contractstudy.extractors.visitors;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.ThrowStmt;
import contractstudy.ConstraintType;
import contractstudy.ContractElement;
import contractstudy.ExtractionListener;
import contractstudy.ProgramVersion;

/**
 * Visitor for method nodes in the AST, used to extract unconditional throws of
 * UnsupportedOperationException.
 *
 * @author jens dietrich
 */
@SuppressWarnings("rawtypes")
public class MethodVisitorToCollectUnconditionalUnsupportedOperationExceptionThrows extends AbstractMethodVisitor {

    public MethodVisitorToCollectUnconditionalUnsupportedOperationExceptionThrows(
            ExtractionListener<ContractElement> consumer,
            String programName,
            String version,
            String cuName) {
        super(consumer, programName, version, cuName);
    }

    @Override
    public void visit(ThrowStmt n, Object arg) {

        ObjectCreationExpr objCreationNode = (ObjectCreationExpr) n.getExpression(); // JFF
        StringBuffer b = new StringBuffer();
        if (objCreationNode.getArguments() != null) { // JFF
            for (Expression expr : objCreationNode.getArguments()) { // JFF
                if (b.length() > 0) b.append(',');
                b.append(expr.toString());
            }
        }
        String additionalInfo = b.toString();

        String excTypeName = objCreationNode.getType().getName().getIdentifier(); // JFF


        if (excTypeName.endsWith(UnsupportedOperationException.class.getSimpleName()) && n.getExpression() instanceof ObjectCreationExpr && (n.getParentNode().get() instanceof IfStmt || (n.getParentNode().get() instanceof BlockStmt && n.getParentNode().get().getParentNode().get() instanceof MethodDeclaration))) { // JFF
            ContractElement p = initConstraint();
            p.setProgramVersion(ProgramVersion.getOrCreate(programName, this.version));
            p.setCuName(this.cuName);
            p.setCondition(null);
            p.setKind(ConstraintType.UCREUnsupportedOperationException);
            p.setLineNo(n.getBegin().get().line); // JFF
            p.setAdditionalInfo(additionalInfo);
            consumer.constraintFound(p);
        }
        super.visit(n, arg);
    }

}

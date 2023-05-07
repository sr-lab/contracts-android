package contractstudy.usage.collectContracts.asserts.JavaAssert;

import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.stmt.AssertStmt;
import contractstudy.constants.constraint.ConstraintType;
import contractstudy.constants.constraint.ContractElement;
import contractstudy.model.ExtractionListener;
import contractstudy.model.ProgramVersion;
import contractstudy.usage.collectContracts.common.AbstractMethodVisitor.AbstractMethodVisitor;

/**
 * @author Kamil Jezek [kamil.jezek@verifalabs.com]
 */
public class JavaAssertVisitor extends AbstractMethodVisitor {

  public JavaAssertVisitor(String programName, String version, String cuName,
    ExtractionListener<ContractElement> consumer) {
    super(consumer, programName, version, cuName);
  }

  @Override
  public void visit(AssertStmt n, Object arg) {
    String condition = n.getCheck().removeComment().toString();
    Expression message = n.getMessage().orElse(null);
    String info = null;
    if (message != null) {
      info = message.removeComment().toString();
    }

    ContractElement p = initConstraint();
    p.setProgramVersion(ProgramVersion.getOrCreate(programName, version));
    p.setCuName(cuName);
    p.setCondition(condition);
    p.setKind(ConstraintType.JavaAssert);
    p.setLineNo(n.getBegin().get().line);
    p.setAdditionalInfo(info);

    consumer.constraintFound(p);

    super.visit(n, arg);
  }
}

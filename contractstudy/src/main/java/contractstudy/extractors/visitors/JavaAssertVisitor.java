package contractstudy.extractors.visitors;

import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.stmt.AssertStmt;
import contractstudy.ConstraintType;
import contractstudy.ContractElement;
import contractstudy.ExtractionListener;
import contractstudy.ProgramVersion;

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

    //String condition = n.getCheck().toStringWithoutComments();
    String condition = n.getCheck().removeComment().toString(); // JFF: FIXME?
    Expression message = n.getMessage().orElse(null);
    String info = null;
    if (message != null) {
      info = message.removeComment().toString(); // JFF: FIXME? .toStringWithoutComments();
    }

    ContractElement p = initConstraint();

    p.setProgramVersion(ProgramVersion.getOrCreate(programName, version));
    p.setCuName(cuName);
    p.setCondition(condition);
    p.setKind(ConstraintType.JavaAssert);
    //p.setLineNo(n.getBeginLine());
    p.setLineNo(n.getBegin().get().line); // JFF: FIXME?
    p.setAdditionalInfo(info);

    consumer.constraintFound(p);

    super.visit(n, arg);
  }
}

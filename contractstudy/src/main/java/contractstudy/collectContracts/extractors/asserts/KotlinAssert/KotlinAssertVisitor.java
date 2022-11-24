package contractstudy.collectContracts.extractors.asserts.KotlinAssert;

import contractstudy.ContractElement;
import contractstudy.ExtractionListener;
import contractstudy.collectContracts.extractors.common.AbstractMethodVisitor.AbstractMethodVisitorKotlin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.kotlin.psi.KtSimpleNameExpression;

/**
 * @author Kamil Jezek [kamil.jezek@verifalabs.com]
 */
public class KotlinAssertVisitor extends AbstractMethodVisitorKotlin {

  public KotlinAssertVisitor(String programName, String version, String cuName,
    ExtractionListener<ContractElement> consumer) {
    super(consumer, programName, version, cuName);
  }

  /*@Override
  public void visitClass(@NotNull KtClass klass) {
    System.out.println(klass.getName());

    super.visitClass(klass, null);
  }*/

  /*@Override
  public void visitNamedFunction(@NotNull KtNamedFunction function) {
    System.out.println(function.getName());

    //super.visitNamedFunction(function, null);
  }*/

  @Override
  public void visitSimpleNameExpression(@NotNull KtSimpleNameExpression expression) {

    System.out.println(expression.getReferencedName());

    System.out.println("Nice");

    //super.visitSimpleNameExpression(expression, null);
  }



  /*public void visit(AssertStmt n, Object arg) {

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
  }*/
}

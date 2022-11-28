package contractstudy.collectContracts.extractors.asserts.KotlinAssert;

import contractstudy.ExtractionListener;
import contractstudy.ProgramVersion;
import contractstudy.collectContracts.extractors.common.AbstractMethodVisitor.AbstractMethodVisitorKotlin;
import contractstudy.constants.constraint.ConstraintType;
import contractstudy.constants.constraint.ContractElement;
import contractstudy.kotlinParser.KotlinParserUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.kotlin.psi.KtCallExpression;
import org.jetbrains.kotlin.psi.KtValueArgument;

import java.util.List;
import java.util.Objects;

/**
 * @author Kamil Jezek [kamil.jezek@verifalabs.com]
 */
public class KotlinAssertVisitor extends AbstractMethodVisitorKotlin {

  public KotlinAssertVisitor(String programName, String version, String cuName,
    ExtractionListener<ContractElement> consumer) {
    super(consumer, programName, version, cuName);
  }

  @Override
  public void visitCallExpression(@NotNull KtCallExpression expression) {
    if (!isExpressionAKotlinAssert(expression)) {
      return;
    }

    List<KtValueArgument> arguments = expression.getValueArguments();

    ContractElement p = initConstraint();

    p.setProgramVersion(ProgramVersion.getOrCreate(programName, version));
    p.setCuName(cuName);
    p.setCondition("condition"); // TODO: GET condition.
    p.setKind(ConstraintType.KotlinAssert);
    p.setLineNo(KotlinParserUtils.getElementBeginLine(expression));
    p.setAdditionalInfo("info"); // TODO: Get assert message.

    consumer.constraintFound(p);
  }

  private boolean isExpressionAKotlinAssert(KtCallExpression expression) {
    // TODO: It is not distinguishing between Kotlin assert or method named assert.

    try {
      String name = Objects.requireNonNull(expression.getCalleeExpression()).getText();
      return Objects.equals(name, "assert");
    } catch (NullPointerException nullPointerException) {
      return false;
    }
  }

}

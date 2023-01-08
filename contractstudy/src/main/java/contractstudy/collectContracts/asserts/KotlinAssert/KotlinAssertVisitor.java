package contractstudy.collectContracts.asserts.KotlinAssert;

import contractstudy.collectContracts.common.AbstractMethodVisitor.AbstractMethodVisitorKotlin;
import contractstudy.constants.constraint.ContractElement;
import contractstudy.model.ExtractionListener;
import contractstudy.model.ProgramVersion;
import contractstudy.utils.kotlinParser.KotlinParserUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.kotlin.psi.KtCallExpression;
import org.jetbrains.kotlin.psi.KtValueArgument;

import java.util.List;
import java.util.Objects;

/**
 * Extracts constraints for Kotlin stdlib expressions: assert(), require(), requireNotNull(),
 * check() and checkNotNull().
 * <p>
 * Those are not reserved keywords in Kotlin. //TODO: Currently, we don't distinguish between a call
 * to sdtlib method or a call to a custom method with same name.
 */
public class KotlinAssertVisitor extends AbstractMethodVisitorKotlin {

  public KotlinAssertVisitor(String programName, String version, String cuName,
    ExtractionListener<ContractElement> consumer) {
    super(consumer, programName, version, cuName);
  }

  @Override
  public void visitCallExpression(@NotNull KtCallExpression expression) {
    KotlinAssertExpression assertExpression = getKotlinAssertExpression(expression);
    if (assertExpression == KotlinAssertExpression.NONE) {
      return;
    }

    String lambdaMessage = getLambdaMessage(expression);
    List<KtValueArgument> arguments = expression.getValueArguments();

    if (arguments.size() > 0) {
      ContractElement p = initConstraint();
      p.setProgramVersion(ProgramVersion.getOrCreate(programName, version));
      p.setCuName(cuName);
      p.setCondition(arguments.get(0).getText());
      p.setKind(KotlinAssertExpression.getConstraintTypeBy(assertExpression));
      p.setLineNo(KotlinParserUtils.getElementBeginLine(expression));
      p.setAdditionalInfo(lambdaMessage);
      consumer.constraintFound(p);
    }

    super.visitCallExpression(expression);
  }

  private KotlinAssertExpression getKotlinAssertExpression(KtCallExpression expression) {
    try {
      String name = Objects.requireNonNull(expression.getCalleeExpression()).getText();
      return KotlinAssertExpression.getExpressionKeyBy(name);
    } catch (NullPointerException nullPointerException) {
      return KotlinAssertExpression.NONE;
    }
  }

  private String getLambdaMessage(KtCallExpression expression) {
    String message = "";
    if (expression.getLambdaArguments().size() > 0) {
      message = expression.getLambdaArguments().get(0).getText();
      message = message.replace("{", "");
      message = message.replace("}", "");
    }
    return message;
  }

}

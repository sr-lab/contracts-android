package contractstudy.usage.collectContracts.asserts.KotlinAssert;

import contractstudy.constants.constraint.ContractElement;
import contractstudy.model.ExtractionListener;
import contractstudy.model.ProgramVersion;
import contractstudy.usage.collectContracts.common.AbstractMethodVisitor.AbstractMethodVisitorKotlin;
import contractstudy.utils.KotlinParserUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.kotlin.psi.KtCallExpression;
import org.jetbrains.kotlin.psi.KtValueArgument;

import java.util.List;
import java.util.Objects;

/**
 * Extracts constraints for Kotlin stdlib expressions: assert(), require(), requireNotNull(), check() and checkNotNull().
 * <p>
 * Those are not reserved keywords in Kotlin. Therefore, there can be developer's methods with the same names.
 * <p>
 * To differentiate a real assertion from a developer's method: If there is any method/import in that file with the same name
 * as one of the assertions, and the expression contains no lambda arguments => it is not a Kotlin assertion.
 */
public class KotlinAssertVisitor extends AbstractMethodVisitorKotlin {

  private final List<KotlinAssertExpression> ambiguousAssertions;

  public KotlinAssertVisitor(String programName, String version, String cuName,
    ExtractionListener<ContractElement> consumer,
    List<KotlinAssertExpression> ambiguousAssertions) {
    super(consumer, programName, version, cuName);
    this.ambiguousAssertions = ambiguousAssertions;
  }

  @Override
  public void visitCallExpression(@NotNull KtCallExpression expression) {
    KotlinAssertExpression assertExpression = identifyKotlinAssertExpression(expression);
    if (assertExpression == KotlinAssertExpression.NONE) {
      return;
    }

    String lambdaMessage = getLambdaMessage(expression);
    List<KtValueArgument> arguments = expression.getValueArguments();

    if (arguments.size() > 0) {
      if (isIdentifiedAssertionAmbiguous(assertExpression, expression)) {
        return;
      }
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

  private KotlinAssertExpression identifyKotlinAssertExpression(KtCallExpression expression) {
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

  private boolean isIdentifiedAssertionAmbiguous(KotlinAssertExpression assertExpression,
    KtCallExpression expression) {
    return ambiguousAssertions.contains(assertExpression)
      && expression.getLambdaArguments().size() == 0;
  }

}

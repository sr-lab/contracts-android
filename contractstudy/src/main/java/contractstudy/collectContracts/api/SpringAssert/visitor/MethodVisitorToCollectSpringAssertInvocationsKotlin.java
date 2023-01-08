package contractstudy.collectContracts.api.SpringAssert.visitor;

import contractstudy.collectContracts.api.SpringAssert.constants.SpringAssertEnum;
import contractstudy.collectContracts.common.MethodVisitorToCollectInvocations.MethodVisitorToCollectInvocationsKotlin;
import contractstudy.collectContracts.common.StaticImportCollector.constants.StaticImportState;
import contractstudy.collectContracts.common.Utils;
import contractstudy.constants.constraint.ContractElement;
import contractstudy.model.ExtractionListener;
import contractstudy.model.ProgramVersion;
import contractstudy.utils.kotlinParser.KotlinParserUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.kotlin.psi.KtCallExpression;
import org.jetbrains.kotlin.psi.KtValueArgument;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * Visitor for method nodes in the AST.
 *
 * @author jens dietrich
 */
@SuppressWarnings("rawtypes")
public class MethodVisitorToCollectSpringAssertInvocationsKotlin extends
  MethodVisitorToCollectInvocationsKotlin {

  public MethodVisitorToCollectSpringAssertInvocationsKotlin(
    ExtractionListener<ContractElement> consumer,
    String programName,
    String version,
    String cuName,
    StaticImportState importState,
    Collection<String> staticallyImportedMethodNames
  ) {
    super(consumer, programName, version, cuName, importState, staticallyImportedMethodNames);
  }


  @Override
  public void visitCallExpression(@NotNull KtCallExpression expression) {
    String methodName = Objects.requireNonNull(expression.getCalleeExpression()).getText();
    int methodCallLine = KotlinParserUtils.getElementBeginLine(expression);

    //Expression expr = callExpr.getScope().orElse(null);
    //String scope = expr == null ? null : expr.toString(); //TODO: Get scope.
    List<KtValueArgument> args = expression.getValueArguments(); //TODO: Get arguments.
    String argumentMessage = Utils.getMessageFromArguments(args);

    ContractElement p = initConstraint();
    p.setProgramVersion(ProgramVersion.getOrCreate(programName, this.version));
    p.setCuName(this.cuName);
    p.setLineNo(methodCallLine);

    // TODO: args.size() > 0 && checkImports(name, scope, "Assert", "org.springframework.util.Assert")
    if (args.size() > 0) {
      SpringAssertEnum springAssert = SpringAssertEnum.getEnumValueFromMethodName(methodName);
      if (springAssert != null) {
        p.setKind(springAssert.constraintType);
        switch (springAssert) {
          case DOES_NOT_CONTAIN:
            p.setCondition(
              "!" + args.get(0).getText() + ".contains(" + args.get(1).getText() + ")");
            p.setAdditionalInfo(argumentMessage);
            break;
          case HAS_LENGTH:
            p.setCondition(
              args.get(0).getText() + "!=null && " + args.get(0).getText() + ".length()>0");
            p.setAdditionalInfo(argumentMessage);
            break;
          case HAS_TEXT:
            p.setCondition(args.get(0).getText() + ".length()>0 && contains some none-whitespaces");
            p.setAdditionalInfo(argumentMessage);
            break;
          case NOT_EMPTY:
            p.setCondition(args.get(0).getText() + ".size>0");
            p.setAdditionalInfo(argumentMessage);
            break;
          case NO_NULL_ELEMENTS:
            p.setCondition(args.get(0).getText() + " does not contain nulls");
            p.setAdditionalInfo(argumentMessage);
            break;
          case IS_NULL:
            p.setCondition(args.get(0).getText() + " == null");
            p.setAdditionalInfo(argumentMessage);
            break;
          case NOT_NULL:
            p.setCondition(args.get(0).getText() + " != null");
            p.setAdditionalInfo(argumentMessage);
            break;
          case IS_INSTANCE_OF:
            p.setCondition(args.get(1).getText() + " instanceOf " + args.get(0).getText());
            p.setAdditionalInfo(argumentMessage);
            break;
          case IS_ASSIGNABLE:
            p.setCondition("0<=" + args.get(0).getText() + "<" + args.get(1).getText());
            p.setAdditionalInfo(argumentMessage);
            break;
          case STATE:
          case IS_TRUE:
            p.setCondition(args.get(0).getText());
            p.setAdditionalInfo(argumentMessage);
            break;
        }
        consumer.constraintFound(p);
      }
    }

  }

}

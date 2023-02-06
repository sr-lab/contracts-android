package contractstudy.usage.collectContracts.api.CommonsValidate.CommonsValidate3.visitor;

import contractstudy.constants.constraint.ContractElement;
import contractstudy.model.ExtractionListener;
import contractstudy.model.ProgramVersion;
import contractstudy.usage.collectContracts.api.CommonsValidate.CommonValidateBase.MethodVisitorToCollectCommonsValidateInvocationsKotlin;
import contractstudy.usage.collectContracts.api.CommonsValidate.constants.CommonsValidate3Enum;
import contractstudy.usage.collectContracts.common.StaticImportCollector.constants.StaticImportState;
import contractstudy.usage.collectContracts.common.Utils;
import contractstudy.utils.kotlinParser.KotlinParserUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.kotlin.psi.KtCallExpression;
import org.jetbrains.kotlin.psi.KtValueArgument;

import java.util.Collection;
import java.util.List;
import java.util.Objects;


@SuppressWarnings("rawtypes")
public class MethodVisitorToCollectCommons3ValidateInvocationsKotlin extends
  MethodVisitorToCollectCommonsValidateInvocationsKotlin {

  public MethodVisitorToCollectCommons3ValidateInvocationsKotlin(
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
    String methodCallName = Objects.requireNonNull(expression.getCalleeExpression()).getText();
    int methodCallLine = KotlinParserUtils.getElementBeginLine(expression);

    //Expression expr = callExpr.getScope().orElse(null);
    //String scope = expr == null ? null : expr.toString(); //TODO: Get scope.
    List<KtValueArgument> args = expression.getValueArguments();
    String argumentMessage = Utils.getMessageFromArguments(args);

    ContractElement p = initConstraint();
    p.setProgramVersion(ProgramVersion.getOrCreate(programName, this.version));
    p.setCuName(this.cuName);
    p.setLineNo(methodCallLine);

    if (args.size() > 0) { // TODO: args.size() > 0 && checkImports(methodCallName, "TODO: Scope")
      CommonsValidate3Enum commonsValidate3 = CommonsValidate3Enum.getEnumValueFromMethodName(
        methodCallName);
      if (commonsValidate3 != null) {
        p.setKind(commonsValidate3.constraintType);
        switch (commonsValidate3) {
          case EXCLUSIVE_BETWEEN:
            p.setCondition(
              args.get(0).getText() + " < " + args.get(2).getText() + " < " + args.get(1)
                .getText());
            p.setAdditionalInfo(argumentMessage);
            break;
          case INCLUSIVE_BETWEEN:
            p.setCondition(
              args.get(0).getText() + " <= " + args.get(2).getText() + " <= " + args.get(1)
                .getText());
            p.setAdditionalInfo(argumentMessage);
            break;
          case IS_ASSIGNABLE_FROM:
            p.setCondition(args.get(1).getText() + " subtypeOf " + args.get(0).getText());
            p.setAdditionalInfo(argumentMessage);
            break;
          case MATCHES_PATTERN:
            p.setCondition(args.get(0).getText() + " matches " + args.get(1).getText());
            p.setAdditionalInfo(argumentMessage);
            break;
          case IS_INSTANCE_OF:
            p.setCondition(args.get(1).getText() + " instanceof " + args.get(0).getText());
            p.setAdditionalInfo(argumentMessage);
            break;
          case NOT_BLANK:
            p.setCondition(args.get(0).getText() + " is not blank");
            p.setAdditionalInfo(argumentMessage);
            break;
          case VALID_INDEX:
            p.setCondition(args.get(1).getText() + " is valid index in " + args.get(0).getText());
            p.setAdditionalInfo(argumentMessage);
            break;
          case VALID_STATE:
            p.setCondition(args.get(0).toString());
            p.setAdditionalInfo(argumentMessage);
            break;
        }
        consumer.constraintFound(p);
      }
    }

    super.visitCallExpression(expression);
  }

  protected boolean checkImports(String methodName, String scope) {
    return checkImports(methodName, scope, "Validate", "org.apache.commons.lang3.Validate");
  }

}

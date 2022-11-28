package contractstudy.collectContracts.extractors.api.CommonsValidate.CommonsValidate3.visitor;

import contractstudy.ExtractionListener;
import contractstudy.ProgramVersion;
import contractstudy.collectContracts.extractors.api.CommonsValidate.CommonValidateBase.MethodVisitorToCollectCommonsValidateInvocationsKotlin;
import contractstudy.collectContracts.extractors.api.CommonsValidate.constants.CommonsValidate3Enum;
import contractstudy.collectContracts.extractors.common.StaticImportCollector.constants.StaticImportState;
import contractstudy.constants.constraint.ContractElement;
import contractstudy.kotlinParser.KotlinParserUtils;
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

  // look for patterns only supported by lang2
  @Override
  public void visitCallExpression(@NotNull KtCallExpression expression) {
    String methodCallName = Objects.requireNonNull(expression.getCalleeExpression()).getText();
    int methodCallLine = KotlinParserUtils.getElementBeginLine(expression);

    //Expression expr = callExpr.getScope().orElse(null);
    //String scope = expr == null ? null : expr.toString(); //TODO: Get scope.
    List<KtValueArgument> args = expression.getValueArguments(); //TODO: Get arguments.

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
            p.setCondition(args.get(0) + " < " + args.get(2) + " < " + args.get(1));
            //TODO: p.setAdditionalInfo(encodeMessageArgs(args, 3));
            break;
          case INCLUSIVE_BETWEEN:
            p.setCondition(args.get(0) + " <= " + args.get(2) + " <= " + args.get(1));
            //TODO: p.setAdditionalInfo(encodeMessageArgs(args, 3));
            break;
          case IS_ASSIGNABLE_FROM:
            p.setCondition(args.get(1) + " subtypeOf " + args.get(0));
            //TODO:  p.setAdditionalInfo(encodeMessageArgs(args, 2));
            break;
          case MATCHES_PATTERN:
            p.setCondition(args.get(0) + " matches " + args.get(1));
            //TODO: p.setAdditionalInfo(encodeMessageArgs(args, 2));
            break;
          case IS_INSTANCE_OF:
            p.setCondition(args.get(1) + " instanceof " + args.get(0));
            //TODO: p.setAdditionalInfo(encodeMessageArgs(args, 2));
            break;
          case NOT_BLANK:
            p.setCondition(args.get(0) + " is not blank");
            //TODO: p.setAdditionalInfo(encodeMessageArgs(args, 1));
            break;
          case VALID_INDEX:
            p.setCondition(args.get(1) + " is valid index in " + args.get(0));
            //TODO: p.setAdditionalInfo(encodeMessageArgs(args, 2));
            break;
          case VALID_STATE:
            p.setCondition(args.get(0).toString());
            //TODO: p.setAdditionalInfo(encodeMessageArgs(args, 1));
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

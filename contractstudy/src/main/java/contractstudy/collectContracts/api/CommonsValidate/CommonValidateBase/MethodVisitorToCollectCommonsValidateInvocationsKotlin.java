package contractstudy.collectContracts.api.CommonsValidate.CommonValidateBase;

import contractstudy.model.ExtractionListener;
import contractstudy.model.ProgramVersion;
import contractstudy.collectContracts.api.CommonsValidate.constants.CommonsValidateCommonEnum;
import contractstudy.collectContracts.common.MethodVisitorToCollectInvocations.MethodVisitorToCollectInvocationsKotlin;
import contractstudy.collectContracts.common.StaticImportCollector.constants.StaticImportState;
import contractstudy.constants.constraint.ContractElement;
import contractstudy.utils.kotlinParser.KotlinParserUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.kotlin.psi.KtCallExpression;
import org.jetbrains.kotlin.psi.KtValueArgument;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

@SuppressWarnings("rawtypes")
public abstract class MethodVisitorToCollectCommonsValidateInvocationsKotlin extends
  MethodVisitorToCollectInvocationsKotlin {

  public MethodVisitorToCollectCommonsValidateInvocationsKotlin(
    ExtractionListener<ContractElement> consumer,
    String programName,
    String version,
    String cuName,
    StaticImportState importState,
    Collection<String> staticallyImportedMethodNames
  ) {
    super(consumer, programName, version, cuName, importState, staticallyImportedMethodNames);
  }


  // look for patterns supported by both lang2 and lang3
  @Override
  public void visitCallExpression(@NotNull KtCallExpression expression) {
    String methodCallName = Objects.requireNonNull(expression.getCalleeExpression()).getText();
    int methodCallLine = KotlinParserUtils.getElementBeginLine(expression);

    //Expression expr = callExpr.getScope().orElse(null);
    //String scope = expr == null ? null : expr.toString(); //TODO: Get scope.
    List<KtValueArgument> args = expression.getValueArguments();

    ContractElement p = initConstraint();
    p.setProgramVersion(ProgramVersion.getOrCreate(programName, this.version));
    p.setCuName(this.cuName);
    p.setLineNo(methodCallLine);

    if (args.size() > 0) { //TODO: args.size() > 0 && checkImports(methodCallName, "TODO: Scope")
      CommonsValidateCommonEnum commonsValidateCommonEnum = CommonsValidateCommonEnum.getEnumValueFromMethodName(
        methodCallName);
      if (commonsValidateCommonEnum != null) {
        p.setKind(commonsValidateCommonEnum.constraintType);
        p.setCondition(args.get(0).toString());
        //p.setAdditionalInfo(encodeMessageArgs(args, 1)); //TODO: Get additional info.
        consumer.constraintFound(p);
      }
    }
  }

  protected abstract boolean checkImports(String methodName, String scope);

}

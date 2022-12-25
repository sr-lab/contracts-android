package contractstudy.collectContracts.api.CommonsValidate.CommonsValidate2.visitor;

import contractstudy.ExtractionListener;
import contractstudy.ProgramVersion;
import contractstudy.collectContracts.api.CommonsValidate.CommonValidateBase.MethodVisitorToCollectCommonsValidateInvocationsKotlin;
import contractstudy.collectContracts.api.CommonsValidate.constants.CommonsValidate2Enum;
import contractstudy.collectContracts.common.StaticImportCollector.constants.StaticImportState;
import contractstudy.constants.constraint.ContractElement;
import contractstudy.utils.kotlinParser.KotlinParserUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.kotlin.psi.KtCallExpression;
import org.jetbrains.kotlin.psi.KtValueArgument;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * Visitor for method nodes in the AST. Used to extract API calls to
 * org.apache.commons.lang.Validate.
 *
 * @author jens dietrich
 */
@SuppressWarnings("rawtypes")
public class MethodVisitorToCollectCommons2ValidateInvocationsKotlin extends
  MethodVisitorToCollectCommonsValidateInvocationsKotlin {

  public MethodVisitorToCollectCommons2ValidateInvocationsKotlin(
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
      CommonsValidate2Enum commonsValidate2 = CommonsValidate2Enum.getEnumValueFromMethodName(
        methodCallName);
      if (commonsValidate2 != null) {
        p.setKind(commonsValidate2.constraintType);
        p.setCondition(
          "all of " + args.get(0).toString() + " instanceOf " + args.get(1)
            .toString()); //TODO: GET arguments value.
        //p.setAdditionalInfo(encodeMessageArgs(args, 2)); //TODO: Get additional info.
        consumer.constraintFound(p);
      }
    }

    super.visitCallExpression(expression);
  }

  protected boolean checkImports(String methodName, String scope) {
    return checkImports(methodName, scope, "Validate", "org.apache.commons.lang.Validate");
  }

}

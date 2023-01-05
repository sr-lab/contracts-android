package contractstudy.collectContracts.api.Guava.visitor;

import contractstudy.collectContracts.api.Guava.constants.GuavaEnum;
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
public class MethodVisitorToCollectGuavaPreconditionsInvocationsKotlin extends
  MethodVisitorToCollectInvocationsKotlin {

  public MethodVisitorToCollectGuavaPreconditionsInvocationsKotlin(
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
    List<KtValueArgument> args = expression.getValueArguments();
    String argumentMessage = Utils.getMessageFromArguments(args);

    ContractElement p = initConstraint();
    p.setProgramVersion(ProgramVersion.getOrCreate(programName, this.version));
    p.setCuName(this.cuName);
    p.setLineNo(methodCallLine);

    //TODO: args.size() > 0 && checkImports(name, scope, "Preconditions",
    //      "com.google.common.base.Preconditions")
    if (args.size() > 0) {
      GuavaEnum guava = GuavaEnum.getEnumValueFromMethodName(methodName);
      if (guava != null) {
        p.setKind(guava.constraintType);
        switch (guava) {
          case CHECK_ARGUMENT:
          case CHECK_STATE:
            p.setCondition(args.get(0).getText());
            p.setAdditionalInfo(argumentMessage);
            break;
          case ELEMENT_INDEX:
          case POSITION_INDEX:
            p.setCondition("0<=" + args.get(0).getText() + "<" + args.get(1).getText());
            p.setAdditionalInfo(argumentMessage);
            break;
          case NOT_NULL:
            p.setCondition(args.get(0).getText() + "!=null");
            p.setAdditionalInfo(argumentMessage);
            break;
          case POSITION_INDEXES:
            p.setCondition("0<=" + args.get(0).getText() + "<=" + args.get(1).getText() + "<=" + args.get(2).getText());
            p.setAdditionalInfo("");
            break;
        }
        consumer.constraintFound(p);
      }
    }
  }
}

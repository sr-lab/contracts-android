package contractstudy.collectContracts.extractors.api.Guava.visitor;

import contractstudy.ExtractionListener;
import contractstudy.ProgramVersion;
import contractstudy.collectContracts.extractors.api.Guava.constants.GuavaEnum;
import contractstudy.collectContracts.extractors.common.MethodVisitorToCollectInvocations.MethodVisitorToCollectInvocationsKotlin;
import contractstudy.collectContracts.extractors.common.StaticImportCollector.constants.StaticImportState;
import contractstudy.constants.constraint.ContractElement;
import contractstudy.kotlinParser.KotlinParserUtils;
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
    List<KtValueArgument> args = expression.getValueArguments(); //TODO: Get arguments values.

    ContractElement p = initConstraint();
    p.setProgramVersion(ProgramVersion.getOrCreate(programName, this.version));
    p.setCuName(this.cuName);
    p.setLineNo(methodCallLine);

    //TODO: args.size() > 0 && checkImports(name, scope, "Preconditions",
    //      "com.google.common.base.Preconditions")
    if (args.size() > 0) {
      GuavaEnum guava = GuavaEnum.getEnumValueFromMethodName(methodName);
      p.setKind(guava.constraintType);
      switch (guava) {
        case CHECK_ARGUMENT:
        case CHECK_STATE:
          p.setCondition(args.get(0).toString());
          //TODO: p.setAdditionalInfo(encodeMessageArgs(args, 1));
          break;
        case ELEMENT_INDEX:
        case POSITION_INDEX:
          p.setCondition("0<=" + args.get(0) + "<" + args.get(1));
          //TODO: p.setAdditionalInfo(encodeMessageArgs(args, 2));
          break;
        case NOT_NULL:
          p.setCondition(args.get(0) + "!=null");
          //TODO: p.setAdditionalInfo(encodeMessageArgs(args, 1));
          break;
        case POSITION_INDEXES:
          p.setCondition("0<=" + args.get(0) + "<=" + args.get(1) + "<=" + args.get(2));
          p.setAdditionalInfo("");
          break;
      }
      consumer.constraintFound(p);
    }

  }

}

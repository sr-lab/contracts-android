package contractstudy.collectContracts.extractors.api.SpringAssert.visitor;

import contractstudy.ExtractionListener;
import contractstudy.ProgramVersion;
import contractstudy.collectContracts.extractors.api.SpringAssert.constants.SpringAssertEnum;
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
            p.setCondition("!" + args.get(0) + ".contains(" + args.get(1) + ")");
            //TODO: p.setAdditionalInfo(encodeMessageArgs(args, 2));
            break;
          case HAS_LENGTH:
            p.setCondition(args.get(0) + "!=null && " + args.get(0) + ".length()>0");
            //TODO: p.setAdditionalInfo(encodeMessageArgs(args, 1));
            break;
          case HAS_TEXT:
            p.setCondition(args.get(0) + ".length()>0 && contains some none-whitespaces");
            //TODO: p.setAdditionalInfo(encodeMessageArgs(args, 1));
            break;
          case NOT_EMPTY:
            p.setCondition(args.get(0) + ".size>0");
            //TODO: p.setAdditionalInfo(encodeMessageArgs(args, 1));
            break;
          case NO_NULL_ELEMENTS:
            p.setCondition(args.get(0) + " does not contain nulls");
            //TODO: p.setAdditionalInfo(encodeMessageArgs(args, 1));
            break;
          case IS_NULL:
            p.setCondition(args.get(0) + " == null");
            //TODO: p.setAdditionalInfo(encodeMessageArgs(args, 1));
            break;
          case NOT_NULL:
            p.setCondition(args.get(0) + " != null");
            //TODO: p.setAdditionalInfo(encodeMessageArgs(args, 1));
            break;
          case IS_INSTANCE_OF:
            p.setCondition(args.get(1) + " instanceOf " + args.get(0));
            //TODO: p.setAdditionalInfo(encodeMessageArgs(args, 2));
            break;
          case IS_ASSIGNABLE:
            p.setCondition("0<=" + args.get(0) + "<" + args.get(1));
            //TODO: p.setAdditionalInfo(encodeMessageArgs(args, 2));
            break;
          case STATE:
          case IS_TRUE:
            p.setCondition(args.get(0).toString());
            //TODO: p.setAdditionalInfo(encodeMessageArgs(args, 1));
            break;
        }
        consumer.constraintFound(p);
      }
    }

  }

}

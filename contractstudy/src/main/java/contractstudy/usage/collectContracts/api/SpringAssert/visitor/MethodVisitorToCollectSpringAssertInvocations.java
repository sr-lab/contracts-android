package contractstudy.usage.collectContracts.api.SpringAssert.visitor;

import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import contractstudy.constants.constraint.ContractElement;
import contractstudy.model.ExtractionListener;
import contractstudy.model.ProgramVersion;
import contractstudy.usage.collectContracts.api.SpringAssert.constants.SpringAssertEnum;
import contractstudy.usage.collectContracts.common.MethodVisitorToCollectInvocations.MethodVisitorToCollectInvocations;
import contractstudy.usage.collectContracts.common.StaticImportCollector.constants.StaticImportState;

import java.util.Collection;
import java.util.List;

import static contractstudy.usage.collectContracts.common.Utils.encodeMessageArgs;

/**
 * Visitor for method nodes in the AST.
 *
 * @author jens dietrich
 */
@SuppressWarnings("rawtypes")
public class MethodVisitorToCollectSpringAssertInvocations extends
  MethodVisitorToCollectInvocations {

  public MethodVisitorToCollectSpringAssertInvocations(
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
  public void visit(MethodCallExpr callExpr, Object arg) {
    String name = callExpr.getName().getIdentifier();
    Expression expr = callExpr.getScope().orElse(null);
    String scope = expr == null ? null : expr.toString();
    List<Expression> args = callExpr.getArguments();

    ContractElement p = initConstraint();
    p.setProgramVersion(ProgramVersion.getOrCreate(programName, this.version));
    p.setCuName(this.cuName);
    p.setLineNo(callExpr.getBegin().get().line);

    if (args.size() > 0 && checkImports(name, scope, "Assert", "org.springframework.util.Assert")) {
      SpringAssertEnum springAssert = SpringAssertEnum.getEnumValueFromMethodName(name);

      if (springAssert != null) {
        p.setKind(springAssert.constraintType);
        switch (springAssert) {
          case DOES_NOT_CONTAIN:
            p.setCondition("!" + args.get(0) + ".contains(" + args.get(1) + ")");
            p.setAdditionalInfo(encodeMessageArgs(args, 2));
            break;
          case HAS_LENGTH:
            p.setCondition(args.get(0) + "!=null && " + args.get(0) + ".length()>0");
            p.setAdditionalInfo(encodeMessageArgs(args, 1));
            break;
          case HAS_TEXT:
            p.setCondition(args.get(0) + ".length()>0 && contains some none-whitespaces");
            p.setAdditionalInfo(encodeMessageArgs(args, 1));
            break;
          case NOT_EMPTY:
            p.setCondition(args.get(0) + ".size>0");
            p.setAdditionalInfo(encodeMessageArgs(args, 1));
            break;
          case NO_NULL_ELEMENTS:
            p.setCondition(args.get(0) + " does not contain nulls");
            p.setAdditionalInfo(encodeMessageArgs(args, 1));
            break;
          case IS_NULL:
            p.setCondition(args.get(0) + " == null");
            p.setAdditionalInfo(encodeMessageArgs(args, 1));
            break;
          case NOT_NULL:
            p.setCondition(args.get(0) + " != null");
            p.setAdditionalInfo(encodeMessageArgs(args, 1));
            break;
          case IS_INSTANCE_OF:
            p.setCondition(args.get(1) + " instanceOf " + args.get(0));
            p.setAdditionalInfo(encodeMessageArgs(args, 2));
            break;
          case IS_ASSIGNABLE:
            p.setCondition("0<=" + args.get(0) + "<" + args.get(1));
            p.setAdditionalInfo(encodeMessageArgs(args, 2));
            break;
          case STATE:
          case IS_TRUE:
            p.setCondition(args.get(0).toString());
            p.setAdditionalInfo(encodeMessageArgs(args, 1));
            break;
        }
        consumer.constraintFound(p);
      }
    }
    super.visit(callExpr, arg);
  }

}

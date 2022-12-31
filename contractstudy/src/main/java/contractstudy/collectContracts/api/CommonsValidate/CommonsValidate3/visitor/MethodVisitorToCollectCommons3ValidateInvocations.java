package contractstudy.collectContracts.api.CommonsValidate.CommonsValidate3.visitor;

import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import contractstudy.collectContracts.api.CommonsValidate.CommonValidateBase.MethodVisitorToCollectCommonsValidateInvocations;
import contractstudy.collectContracts.api.CommonsValidate.constants.CommonsValidate3Enum;
import contractstudy.collectContracts.common.StaticImportCollector.constants.StaticImportState;
import contractstudy.constants.constraint.ContractElement;
import contractstudy.model.ExtractionListener;
import contractstudy.model.ProgramVersion;

import java.util.Collection;
import java.util.List;

import static contractstudy.collectContracts.common.Utils.encodeMessageArgs;

/**
 * Visitor for method nodes in the AST. Used to extract API calls to
 * org.apache.commons.lang3.Validate.
 *
 * @author jens dietrich
 */
@SuppressWarnings("rawtypes")
public class MethodVisitorToCollectCommons3ValidateInvocations extends
  MethodVisitorToCollectCommonsValidateInvocations {

  public MethodVisitorToCollectCommons3ValidateInvocations(
    ExtractionListener<ContractElement> consumer,
    String programName,
    String version,
    String cuName,
    StaticImportState importState,
    Collection<String> staticallyImportedMethodNames
  ) {
    super(consumer, programName, version, cuName, importState, staticallyImportedMethodNames);

  }

  // look for patterns in lang3.
  @Override
  public void visit(MethodCallExpr callExpr, Object arg) {
    String name = callExpr.getName().getIdentifier(); // JFF
    Expression expr = callExpr.getScope().orElse(null); // JFF
    String scope = expr == null ? null : expr.toString();
    List<Expression> args = callExpr.getArguments(); //JFF
    ContractElement p = initConstraint();
    p.setProgramVersion(ProgramVersion.getOrCreate(programName, this.version));
    p.setCuName(this.cuName);
    p.setLineNo(callExpr.getBegin().get().line); // JFF

    if (args.size() > 0 && checkImports(name, scope)) {
      CommonsValidate3Enum commonsValidate3 = CommonsValidate3Enum.getEnumValueFromMethodName(name);
      if (commonsValidate3 != null) {
        p.setKind(commonsValidate3.constraintType);
        switch (commonsValidate3) {
          case EXCLUSIVE_BETWEEN:
            p.setCondition(args.get(0) + " < " + args.get(2) + " < " + args.get(1));
            p.setAdditionalInfo(encodeMessageArgs(args, 3));
            break;
          case INCLUSIVE_BETWEEN:
            p.setCondition(args.get(0) + " <= " + args.get(2) + " <= " + args.get(1));
            p.setAdditionalInfo(encodeMessageArgs(args, 3));
            break;
          case IS_ASSIGNABLE_FROM:
            p.setCondition(args.get(1) + " subtypeOf " + args.get(0));
            p.setAdditionalInfo(encodeMessageArgs(args, 2));
            break;
          case MATCHES_PATTERN:
            p.setCondition(args.get(0) + " matches " + args.get(1));
            p.setAdditionalInfo(encodeMessageArgs(args, 2));
            break;
          case IS_INSTANCE_OF:
            p.setCondition(args.get(1) + " instanceof " + args.get(0));
            p.setAdditionalInfo(encodeMessageArgs(args, 2));
            break;
          case NOT_BLANK:
            p.setCondition(args.get(0) + " is not blank");
            p.setAdditionalInfo(encodeMessageArgs(args, 1));
            break;
          case VALID_INDEX:
            p.setCondition(args.get(1) + " is valid index in " + args.get(0));
            p.setAdditionalInfo(encodeMessageArgs(args, 2));
            break;
          case VALID_STATE:
            p.setCondition(args.get(0).toString());
            p.setAdditionalInfo(encodeMessageArgs(args, 1));
            break;
        }
        consumer.constraintFound(p);
      }
    }
    super.visit(callExpr, arg);
  }

  protected boolean checkImports(String methodName, String scope) {
    return checkImports(methodName, scope, "Validate", "org.apache.commons.lang3.Validate");
  }


}

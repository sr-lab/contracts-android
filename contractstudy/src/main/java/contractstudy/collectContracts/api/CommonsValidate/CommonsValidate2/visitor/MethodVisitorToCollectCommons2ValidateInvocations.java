package contractstudy.collectContracts.api.CommonsValidate.CommonsValidate2.visitor;

import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import contractstudy.model.ExtractionListener;
import contractstudy.model.ProgramVersion;
import contractstudy.collectContracts.api.CommonsValidate.CommonValidateBase.MethodVisitorToCollectCommonsValidateInvocations;
import contractstudy.collectContracts.api.CommonsValidate.constants.CommonsValidate2Enum;
import contractstudy.collectContracts.common.StaticImportCollector.constants.StaticImportState;
import contractstudy.constants.constraint.ContractElement;

import java.util.Collection;
import java.util.List;

import static contractstudy.collectContracts.common.Utils.encodeMessageArgs;

/**
 * Visitor for method nodes in the AST. Used to extract API calls to
 * org.apache.commons.lang.Validate.
 *
 * @author jens dietrich
 */
@SuppressWarnings("rawtypes")
public class MethodVisitorToCollectCommons2ValidateInvocations extends
  MethodVisitorToCollectCommonsValidateInvocations {

  public MethodVisitorToCollectCommons2ValidateInvocations(
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
  public void visit(MethodCallExpr callExpr, Object arg) {
    String name = callExpr.getName().getIdentifier(); // JFF: FIXME?
    Expression expr = callExpr.getScope().orElse(null); // JFF: FIXME?
    String scope = expr == null ? null : expr.toString();
    List<Expression> args = callExpr.getArguments(); //JFF
    ContractElement p = initConstraint();
    p.setProgramVersion(ProgramVersion.getOrCreate(programName, this.version));
    p.setCuName(this.cuName);
    p.setMethodDeclaration(this.methodDeclaration);
    p.setLineNo(callExpr.getBegin().get().line); //JFF

    if (args.size() > 0 && checkImports(name, scope)) {
      CommonsValidate2Enum commonsValidate2 = CommonsValidate2Enum.getEnumValueFromMethodName(name);
      if (commonsValidate2 != null) {
        p.setKind(commonsValidate2.constraintType);
        p.setCondition(
          "all of " + args.get(0).toString() + " instanceOf " + args.get(1).toString());
        p.setAdditionalInfo(encodeMessageArgs(args, 1));
        consumer.constraintFound(p);
      }
    }

    super.visit(callExpr, arg);
  }

  protected boolean checkImports(String methodName, String scope) {
    return checkImports(methodName, scope, "Validate", "org.apache.commons.lang.Validate");
  }


}

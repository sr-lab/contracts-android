package contractstudy.collectContracts.extractors.api.CommonsValidate.CommonValidateBase;

import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import contractstudy.ExtractionListener;
import contractstudy.ProgramVersion;
import contractstudy.collectContracts.extractors.api.CommonsValidate.constants.CommonsValidateCommonEnum;
import contractstudy.collectContracts.extractors.common.MethodVisitorToCollectInvocations.MethodVisitorToCollectInvocations;
import contractstudy.collectContracts.extractors.common.StaticImportCollector.constants.StaticImportState;
import contractstudy.constants.constraint.ContractElement;

import java.util.Collection;
import java.util.List;

import static contractstudy.collectContracts.extractors.common.Utils.encodeMessageArgs;

/**
 * Visitor for method nodes in the AST.
 *
 * @author jens dietrich
 */
@SuppressWarnings("rawtypes")
public abstract class MethodVisitorToCollectCommonsValidateInvocations extends
  MethodVisitorToCollectInvocations {

  public MethodVisitorToCollectCommonsValidateInvocations(
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
  public void visit(MethodCallExpr callExpr, Object arg) {
    //String name = callExpr.getName();
    String name = callExpr.getName().getIdentifier(); // JFF: FIXME?
    //Expression expr = callExpr.getScope();
    Expression expr = callExpr.getScope().orElse(null);
    String scope = expr == null ? null : expr.toString();
    List<Expression> args = callExpr.getArguments(); // JFF
    ContractElement p = initConstraint();
    p.setProgramVersion(ProgramVersion.getOrCreate(programName, this.version));
    p.setCuName(this.cuName);
    p.setLineNo(callExpr.getBegin().get().line); // JFF

    if (args.size() > 0 && checkImports(name, scope)) {
      CommonsValidateCommonEnum commonsValidateCommonEnum = CommonsValidateCommonEnum.getEnumValueFromMethodName(
        name);
      if (commonsValidateCommonEnum != null) {
        p.setKind(commonsValidateCommonEnum.constraintType);
        p.setCondition(args.get(0).toString());
        p.setAdditionalInfo(encodeMessageArgs(args, 1));
        consumer.constraintFound(p);
      }
    }

    super.visit(callExpr, arg);
  }

  protected abstract boolean checkImports(String methodName, String scope);

}

package contractstudy.collectContracts.api.Guava.visitor;

import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import contractstudy.model.ExtractionListener;
import contractstudy.model.ProgramVersion;
import contractstudy.collectContracts.api.Guava.constants.GuavaEnum;
import contractstudy.collectContracts.common.MethodVisitorToCollectInvocations.MethodVisitorToCollectInvocations;
import contractstudy.collectContracts.common.StaticImportCollector.constants.StaticImportState;
import contractstudy.constants.constraint.ContractElement;

import java.util.Collection;
import java.util.List;

import static contractstudy.collectContracts.common.Utils.encodeMessageArgs;

/**
 * Visitor for method nodes in the AST.
 *
 * @author jens dietrich
 */
@SuppressWarnings("rawtypes")
public class MethodVisitorToCollectGuavaPreconditionsInvocations extends
  MethodVisitorToCollectInvocations {

  public MethodVisitorToCollectGuavaPreconditionsInvocations(
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
    String name = callExpr.getName().getIdentifier(); // JFF
    Expression expr = callExpr.getScope().orElse(null); // JFF
    String scope = expr == null ? null : expr.toString();
    List<Expression> args = callExpr.getArguments(); // JFF
    ContractElement p = initConstraint();
    p.setProgramVersion(ProgramVersion.getOrCreate(programName, this.version));
    p.setCuName(this.cuName);
    p.setMethodDeclaration(this.methodDeclaration);
    p.setLineNo(callExpr.getBegin().get().line); // JFF

    if (args.size() > 0 && checkImports(name, scope, "Preconditions",
      "com.google.common.base.Preconditions")) {
      GuavaEnum guava = GuavaEnum.getEnumValueFromMethodName(name);
      p.setKind(guava.constraintType);
      switch (guava) {
        case CHECK_ARGUMENT:
        case CHECK_STATE:
          p.setCondition(args.get(0).toString());
          p.setAdditionalInfo(encodeMessageArgs(args, 1));
          break;
        case ELEMENT_INDEX:
        case POSITION_INDEX:
          p.setCondition("0<=" + args.get(0) + "<" + args.get(1));
          p.setAdditionalInfo(encodeMessageArgs(args, 2));
          break;
        case NOT_NULL:
          p.setCondition(args.get(0) + "!=null");
          p.setAdditionalInfo(encodeMessageArgs(args, 1));
          break;
        case POSITION_INDEXES:
          p.setCondition("0<=" + args.get(0) + "<=" + args.get(1) + "<=" + args.get(2));
          p.setAdditionalInfo("");
          break;
      }
      consumer.constraintFound(p);
    }
    super.visit(callExpr, arg);
  }

}

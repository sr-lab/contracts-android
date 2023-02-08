package contractstudy.usage.collectContracts.other.KotlinContract;

import contractstudy.constants.constraint.ConstraintType;
import contractstudy.constants.constraint.ContractElement;
import contractstudy.model.ExtractionListener;
import contractstudy.model.ProgramVersion;
import contractstudy.usage.collectContracts.common.AbstractMethodVisitor.AbstractMethodVisitorKotlin;
import contractstudy.utils.kotlinParser.KotlinParserUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.kotlin.psi.KtCallExpression;

public class KotlinContractVisitor extends AbstractMethodVisitorKotlin {

  final String REGEX_VALIDATE_KOTLIN_CONTRACT = "(contract(\\s)*\\{(\\s)*returns(.)*implies(.*)\\})+";

  public KotlinContractVisitor(
    String programName,
    String version,
    String cuName,
    ExtractionListener<ContractElement> consumer
  ) {
    super(consumer, programName, version, cuName);
  }

  @Override
  public void visitCallExpression(@NotNull KtCallExpression expression) {
    boolean isKotlinContract = isCallExpressionAKotlinContract(expression);
    if (isKotlinContract) {
      ContractElement p = initConstraint();
      p.setProgramVersion(ProgramVersion.getOrCreate(programName, version));
      p.setCuName(cuName);
      p.setCondition(expression.getLambdaArguments().get(0).getText());
      p.setKind(ConstraintType.KotlinContract);
      p.setLineNo(KotlinParserUtils.getElementBeginLine(expression));
      p.setAdditionalInfo("");
      consumer.constraintFound(p);
    }
    super.visitCallExpression(expression);
  }

  private boolean isCallExpressionAKotlinContract(KtCallExpression expression) {
    return expression.getText().matches(REGEX_VALIDATE_KOTLIN_CONTRACT);
  }


}

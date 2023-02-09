package contractstudy.usage.collectContracts.asserts.KotlinAssert;

import contractstudy.constants.constraint.ContractElement;
import contractstudy.model.ExtractionListener;
import contractstudy.usage.collectContracts.common.AbstractMethodVisitor.AbstractMethodVisitorKotlin;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.kotlin.psi.KtImportDirective;
import org.jetbrains.kotlin.psi.KtNamedFunction;

import java.util.List;
import java.util.Objects;

/**
 * Searches for methods/imports in the file with names equal to
 * any Kotlin assertion: assert(), require(), requireNotNull(), check() and checkNotNull().
 *
 * Adds those occurrences to a list.
 *
 * Those are not reserved keywords in the Kotlin language.
 * Therefore, if there is a method/import with one of those names,
 * a callExpression with that name can either be the assertion or the custom method.
 */
@Getter
public class KotlinPotentialAmbiguousAssertionsVisitor extends AbstractMethodVisitorKotlin {

  private List<KotlinAssertExpression> ambiguousExpressions;

  public KotlinPotentialAmbiguousAssertionsVisitor(String programName, String version, String cuName,
    ExtractionListener<ContractElement> consumer, List<KotlinAssertExpression> ambiguousExpressions) {
    super(consumer, programName, version, cuName);
    this.ambiguousExpressions = ambiguousExpressions;
  }

  public void visitNamedFunction(@NotNull KtNamedFunction function) {
    String functionName = function.getName();
    addNameEqualToKotlinAssertionToList(functionName);
    super.visitNamedFunction(function);
  }

  @Override
  public void visitImportDirective(@NotNull KtImportDirective importDirective) {
    String importName = Objects.requireNonNull(importDirective.getImportedName()).getIdentifier();
    addNameEqualToKotlinAssertionToList(importName);
    super.visitImportDirective(importDirective);
  }

  private void addNameEqualToKotlinAssertionToList(String expression) {
    KotlinAssertExpression expressionWithSameNameAsAssertion = KotlinAssertExpression.getExpressionKeyBy(expression);
    if (expressionWithSameNameAsAssertion != KotlinAssertExpression.NONE) {
      ambiguousExpressions.add(expressionWithSameNameAsAssertion);
    }
  }

}

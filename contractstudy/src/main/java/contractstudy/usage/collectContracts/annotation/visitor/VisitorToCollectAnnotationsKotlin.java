package contractstudy.usage.collectContracts.annotation.visitor;

import contractstudy.constants.constraint.ConstraintType;
import contractstudy.constants.constraint.ConstraintedArtefact;
import contractstudy.constants.constraint.ContractElement;
import contractstudy.model.ExtractionListener;
import contractstudy.model.ProgramVersion;
import contractstudy.usage.collectContracts.common.AbstractMethodVisitor.AbstractMethodVisitorKotlin;
import contractstudy.usage.collectContracts.common.StaticImportCollector.constants.StaticImportState;
import contractstudy.utils.KotlinParserUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.kotlin.com.intellij.psi.PsiElement;
import org.jetbrains.kotlin.com.intellij.psi.PsiWhiteSpace;
import org.jetbrains.kotlin.com.intellij.psi.impl.source.tree.PsiWhiteSpaceImpl;
import org.jetbrains.kotlin.psi.KtAnnotationEntry;
import org.jetbrains.kotlin.psi.KtModifierList;
import org.jetbrains.kotlin.psi.KtNamedFunction;
import org.jetbrains.kotlin.psi.KtParameter;
import org.jetbrains.kotlin.psi.KtProperty;
import org.jetbrains.kotlin.psi.KtValueArgument;
import org.jetbrains.kotlin.psi.stubs.elements.KtFunctionElementType;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class VisitorToCollectAnnotationsKotlin extends AbstractMethodVisitorKotlin {

  private final Map<String, ConstraintType> annotationsMap;
  private final StaticImportState importState;

  public VisitorToCollectAnnotationsKotlin(
    ExtractionListener<ContractElement> consumer,
    String programName,
    String version,
    String cuName,
    Map<String, ConstraintType> annotationsMap,
    StaticImportState importState
  ) {
    super(consumer, programName, version, cuName);
    this.annotationsMap = annotationsMap;
    this.importState = importState;
  }

  @Override
  public void visitAnnotationEntry(@NotNull KtAnnotationEntry annotationEntry) {
    ConstraintType constraintType = getConstraintTypeString(annotationEntry);
    if (constraintType != null && importState == StaticImportState.CLASS) {
      String condition = getAnnotationCondition(annotationEntry);
      ConstraintedArtefact artefact = getConstraintArtefact(annotationEntry);
      int beginLine = KotlinParserUtils.getElementBeginLine(annotationEntry);
      ContractElement p = create(
        ProgramVersion.getOrCreate(programName, version),
        cuName,
        constraintType,
        condition,
        beginLine,
        artefact);
      consumer.constraintFound(p);
    }
  }

  private String getAnnotationCondition(KtAnnotationEntry annotationEntry) {
    StringBuilder condition;
    try {
      List<KtValueArgument> arguments = Objects.requireNonNull(
        annotationEntry.getValueArgumentList()).getArguments();
      condition = new StringBuilder("[");
      for (KtValueArgument argument : arguments) {
        condition.append(" ").append(argument.getText());
      }
      condition.append(" ]");
    } catch (NullPointerException exception) {
      condition = new StringBuilder("NONE");
    }
    return condition.toString();
  }

  private ConstraintType getConstraintTypeString(KtAnnotationEntry annotationEntry) {
    String name = annotationEntry.getShortName().getIdentifier();
    ConstraintType constraintType = annotationsMap.get(name);
    if (constraintType == null) {
      constraintType = annotationsMap.get(name.replace('.', '_'));
    }
    return constraintType;
  }

  private ConstraintedArtefact getConstraintArtefact(KtAnnotationEntry annotationEntry) {
    try {
      PsiElement node = annotationEntry.getContext().getParent();
      if (node instanceof KtFunctionElementType) {
        return ConstraintedArtefact.METHOD_PARAMETER;
      } else if (checkIfAnnotationIsMethodParameter(node)) {
        return ConstraintedArtefact.METHOD_PARAMETER;
      } else if (checkIfNextSiblingIsFunction(annotationEntry) && !(node instanceof KtProperty)) {
        return ConstraintedArtefact.METHOD;
      } else {
        return ConstraintedArtefact.CLASS;
      }
    } catch (NullPointerException ignored) {
      return ConstraintedArtefact.CLASS;
    }
  }

  private boolean checkIfAnnotationIsMethodParameter(PsiElement node) {
    return (node instanceof KtParameter &&
      (node.getParent().getContext() instanceof KtNamedFunction ||
        node.getParent().getContext() instanceof KtFunctionElementType)
    );
  }

  private boolean checkIfNextSiblingIsFunction(KtAnnotationEntry annotationEntry) {
    PsiElement nextParentSibling = annotationEntry.getContext().getParent();
    try {
      do {
        nextParentSibling = nextParentSibling.getNextSibling();
      } while (!(nextParentSibling.getNextSibling() instanceof PsiWhiteSpaceImpl));
    } catch (Exception ignored) {
    }

    boolean isMethod = nextParentSibling instanceof KtNamedFunction || nextParentSibling instanceof KtFunctionElementType;
    if (isMethod) {
      return true;
    }

    return checkIfAssociatedWithMethodExtendingClass(annotationEntry);
  }

  private boolean checkIfAssociatedWithMethodExtendingClass(PsiElement node) {
    PsiElement iterator = node.getContext();
    if (!(iterator instanceof KtModifierList)) {
      return false;
    }
    while ((iterator.getNextSibling() instanceof PsiWhiteSpace)) {
      iterator = iterator.getNextSibling();
    }
    iterator = iterator.getNextSibling().getContext();
    return (iterator instanceof KtNamedFunction || iterator instanceof KtFunctionElementType);
  }

  private ContractElement create(ProgramVersion programVersion, String cuName,
    ConstraintType constraintType,
    String condition, int beginLine, ConstraintedArtefact artefact) {
    ContractElement p = initConstraint();
    p.setProgramVersion(programVersion);
    p.setCuName(cuName);
    p.setCondition(condition);
    p.setKind(constraintType);
    p.setLineNo(beginLine);
    p.setAdditionalInfo(null);
    p.setConstraintedArtefact(artefact);
    return p;
  }

}

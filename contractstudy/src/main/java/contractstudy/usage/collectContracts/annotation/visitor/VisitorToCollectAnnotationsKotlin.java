package contractstudy.usage.collectContracts.annotation.visitor;

import contractstudy.constants.constraint.ConstraintType;
import contractstudy.constants.constraint.ConstraintedArtefact;
import contractstudy.constants.constraint.ContractElement;
import contractstudy.model.ExtractionListener;
import contractstudy.model.ProgramVersion;
import contractstudy.usage.collectContracts.common.AbstractMethodVisitor.AbstractMethodVisitorKotlin;
import contractstudy.usage.collectContracts.common.StaticImportCollector.constants.StaticImportState;
import contractstudy.utils.kotlinParser.KotlinParserUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.kotlin.com.intellij.psi.PsiElement;
import org.jetbrains.kotlin.psi.KtAnnotationEntry;
import org.jetbrains.kotlin.psi.KtValueArgument;
import org.jetbrains.kotlin.psi.stubs.elements.KtFunctionElementType;
import org.jetbrains.kotlin.psi.stubs.elements.KtParameterElementType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class VisitorToCollectAnnotationsKotlin extends AbstractMethodVisitorKotlin {

  private StaticImportState importState = null;
  private Map<String, ConstraintType> map = new HashMap<>();

  public VisitorToCollectAnnotationsKotlin(
    ExtractionListener<ContractElement> consumer,
    String programName,
    String version,
    String cuName,
    StaticImportState importState,
    Map<String, ConstraintType> map) {
    super(consumer, programName, version, cuName);
    this.importState = importState;
    this.map = map;
  }


  @Override
  public void visitAnnotationEntry(@NotNull KtAnnotationEntry annotationEntry) {
    ConstraintType constraintType = getConstraintTypeString(annotationEntry);
    if (constraintType
      != null) { //TODO: Fixme? (constraintType != null && importState == StaticImportState.CLASS)
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
    ConstraintType constraintType = map.get(name);
    if (constraintType == null) {
      constraintType = map.get(name.replace('.', '_'));
    }
    return constraintType;
  }

  private ConstraintedArtefact getConstraintArtefact(KtAnnotationEntry annotationEntry) {
    PsiElement node = annotationEntry.getParent();
    if (node instanceof KtFunctionElementType) {
      return ConstraintedArtefact.METHOD;
    } else if (node instanceof KtParameterElementType) {
      return ConstraintedArtefact.METHOD_PARAMETER;
    } else {
      return ConstraintedArtefact.CLASS;
    }
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

package contractstudy.usage.collectContracts.annotation.visitor;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.expr.MarkerAnnotationExpr;
import com.github.javaparser.ast.expr.NormalAnnotationExpr;
import com.github.javaparser.ast.expr.SingleMemberAnnotationExpr;
import contractstudy.constants.constraint.ConstraintType;
import contractstudy.constants.constraint.ConstraintedArtefact;
import contractstudy.constants.constraint.ContractElement;
import contractstudy.model.ExtractionListener;
import contractstudy.model.ProgramVersion;
import contractstudy.usage.collectContracts.common.AbstractMethodVisitor.AbstractMethodVisitor;
import contractstudy.usage.collectContracts.common.StaticImportCollector.constants.StaticImportState;

import java.util.Map;

/**
 * Generic visitor to collect annotations. Based on the visitor developed by Kamil for JSR303 extraction.
 *
 * @author kamil jezek
 */
@SuppressWarnings("rawtypes")
public class VisitorToCollectAnnotations extends AbstractMethodVisitor {

  private final StaticImportState importState;
  private final Map<String, ConstraintType> map;

  public VisitorToCollectAnnotations(
    ExtractionListener<ContractElement> consumer,
    String programName, String version,
    String cuName,
    Map<String, ConstraintType> map,
    StaticImportState importState
  ) {
    super(consumer, programName, version, cuName);
    this.map = map;
    this.importState = importState;
  }

  @Override
  public void visit(NormalAnnotationExpr n, Object arg) {
    String name = n.getName().getIdentifier();
    ConstraintType constraintType = map.get(name);
    if (constraintType != null && importState == StaticImportState.CLASS) {
      String condition = n.getPairs().toString();
      ConstraintedArtefact artefact = getConstraintArtefact(n);
      ContractElement p = create(
        ProgramVersion.getOrCreate(programName, version),
        cuName,
        constraintType,
        condition,
        n.getBegin().get().line,
        artefact
      );
      consumer.constraintFound(p);
    }
    super.visit(n, arg);
  }

  @Override
  public void visit(SingleMemberAnnotationExpr n, Object arg) {
    String name = n.getName().getIdentifier();
    ConstraintType constraintType = map.get(name);

    if (constraintType == null) {
      constraintType = map.get(name.replace('.', '_'));
    }

    if (constraintType != null && importState == StaticImportState.CLASS) {
      String condition = n.getMemberValue().removeComment().toString();
      ConstraintedArtefact artefact = getConstraintArtefact(n);
      ContractElement p = create(
        ProgramVersion.getOrCreate(programName, version),
        cuName,
        constraintType,
        condition,
        n.getBegin().get().line,
        artefact
      );
      consumer.constraintFound(p);
    }

    super.visit(n, arg);
  }

  @Override
  public void visit(MarkerAnnotationExpr n, Object arg) {
    String name = n.getName().getIdentifier();
    ConstraintType constraintType = map.get(name);

    if (constraintType != null && importState == StaticImportState.CLASS) {
      ConstraintedArtefact artefact = getConstraintArtefact(n);
      ContractElement p = create(
        ProgramVersion.getOrCreate(programName, version),
        cuName,
        constraintType,
        "",
        n.getBegin().get().line,
        artefact
      );
      consumer.constraintFound(p);
    }

    super.visit(n, arg);
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

  private ConstraintedArtefact getConstraintArtefact(AnnotationExpr annotationNode) {
    Node node = annotationNode.getParentNode().orElse(null);
    if (node instanceof MethodDeclaration) {
      return ConstraintedArtefact.METHOD;
    } else if (node instanceof Parameter) {
      return ConstraintedArtefact.METHOD_PARAMETER;
    } else {
      return ConstraintedArtefact.CLASS;
    }
  }
}

package contractstudy.inheritance.ProjectVersionHierarchyExtractor.ProjectClassExtractorKotlin.visitor;

import contractstudy.constants.VisibilityModifier;
import contractstudy.inheritance.model.ASTState;
import contractstudy.inheritance.model.ClassCoordinates;
import contractstudy.usage.collectContracts.common.AbstractMethodVisitor.AbstractMethodVisitorKotlin;
import contractstudy.utils.kotlinParser.KotlinParserUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.kotlin.psi.KtClassOrObject;
import org.jetbrains.kotlin.psi.KtElement;
import org.jetbrains.kotlin.psi.KtModifierList;
import org.jetbrains.kotlin.psi.KtNamedFunction;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 *
 */
public class ClassDefinitionVisitorKotlin extends AbstractMethodVisitorKotlin implements
  ClassCoordinates {

  /**
   * Key is a class name. Value all inner classes.
   */
  private final Map<String, ASTState> innerClassesState = new HashMap<>();
  private String classSimpleName = null;

  public ClassDefinitionVisitorKotlin(String cuName) {
    super(null, null, null, cuName);
  }

  @Override
  public void visitClassOrObject(@NotNull KtClassOrObject classOrObject) {
    init(classOrObject);
    if (classSimpleName == null) {
      classSimpleName = classOrObject.getName();
    }
    super.visitClassOrObject(classOrObject);
  }

  @Override
  public void visitNamedFunction(@NotNull KtNamedFunction function) {
    super.visitNamedFunction(function);
    KtModifierList ktModifierList = function.getModifierList();
    VisibilityModifier visibility = KotlinParserUtils.getVisibilityModifier(ktModifierList);
    boolean isAbstract = super.computeAbstractMethod();
    //TODO: Should we include internal?
    if (visibility != VisibilityModifier.PRIVATE && !isAbstract) {
      if (getState(function) != null) {
        //TODO: Why is state sometimes null?
        getState(function).getMethods().add(super.methodDeclaration);
      }
    }
  }

  private void init(KtElement element) {
    String owner = findOwner(element);
    ASTState innerClass = innerClassesState.get(owner);
    if (innerClass == null) {
      innerClass = new ASTState();
      innerClassesState.put(owner, innerClass);
    }
  }

  protected ASTState getState(KtElement element) {
    String owner = findOwner(element);
    return innerClassesState.get(owner);
  }

  protected Map<String, ASTState> getInnerClassesState() {
    return innerClassesState;
  }

  @Override
  public String getClassSimpleName() {
    return classSimpleName;
  }

  @Override
  public String getClassName() {
    String name = classSimpleName;
    if (getPackageName() != null) {
      name = getPackageName() + '.' + classSimpleName;
    }

    return name;
  }

  @Override
  public Set<ClassCoordinates> getInnerClasses() {
    return createInnerClasses(getClassName() + ".");
  }

  @Override
  public Set<String> getMethods() {
    return innerClassesState.get(getClassName()).getMethods();
  }


  @Override
  public String toString() {
    return "" + getPackageName() + "." + classSimpleName;
  }

  private Set<ClassCoordinates> createInnerClasses(String classNamePrefix) {
    Set<String> keys = new HashSet<>();
    for (String name : innerClassesState.keySet()) {
      if (name.startsWith(classNamePrefix)) {
        keys.add(name);
      }
    }
    Set<ClassCoordinates> inner = new HashSet<>();
    for (String key : keys) {
      inner.add(create(key));
    }
    return inner;
  }

  private ClassCoordinates create(String className) {
    final ClassDefinitionVisitorKotlin delegate = this;
    return new ClassCoordinates() {
      @Override
      public String getPackageName() {
        return delegate.getPackageName();
      }

      @Override
      public String getClassSimpleName() {
        return className.substring(getPackageName().length() - 1);
      }

      @Override
      public String getClassName() {
        return className;
      }

      @Override
      public String getCuName() {
        return delegate.getCuName();
      }

      @Override
      public Set<ClassCoordinates> getInnerClasses() {
        return null;  // TODO: structure is flat for now
      }

      @Override
      public Set<String> getMethods() {
        return innerClassesState.get(className).getMethods();
      }
    };
  }
}

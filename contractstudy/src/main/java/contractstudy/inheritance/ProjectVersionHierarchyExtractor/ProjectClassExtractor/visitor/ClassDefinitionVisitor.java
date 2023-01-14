package contractstudy.inheritance.ProjectVersionHierarchyExtractor.ProjectClassExtractor.visitor;

import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.EnumDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import contractstudy.collectContracts.common.AbstractMethodVisitor.AbstractMethodVisitor;
import contractstudy.inheritance.model.ASTState;
import contractstudy.inheritance.model.ClassCoordinates;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Kamil Jezek [kamil.jezek@verifalabs.com]
 */
public class ClassDefinitionVisitor extends AbstractMethodVisitor implements ClassCoordinates {

  /**
   * Key is a class name. Value all inner classes.
   */
  private final Map<String, ASTState> innerClassesState = new HashMap<>();
  private String classSimpleName = null;


  public ClassDefinitionVisitor(String cuName) {
    super(null, null, null, cuName);
  }


  @Override
  public void visit(final EnumDeclaration n, final Object arg) {
    init(n);
    if (classSimpleName == null) {
      classSimpleName = n.getName().getIdentifier();
    }
    super.visit(n, arg);
  }

  @Override
  public void visit(ClassOrInterfaceDeclaration n, Object arg) {
    init(n);
    if (classSimpleName == null) {
      classSimpleName = n.getName().getIdentifier();
    }
    super.visit(n, arg);
  }

  private void init(Node n) {
    String owner = findOwner(n);
    ASTState innerClass = innerClassesState.get(owner);
    if (innerClass == null) {
      innerClass = new ASTState();
      innerClassesState.put(owner, innerClass);
    }

  }

  @Override
  public void visit(MethodDeclaration methodDeclr, Object arg) {
    super.visit(methodDeclr, arg);
    NodeList<Modifier> modifiers = methodDeclr.getModifiers();
    boolean isAbstract = super.computeAbstractMethod();
    if (!modifiers.contains(Modifier.privateModifier()) && !isAbstract) {
      getState(methodDeclr).getMethods().add(super.methodDeclaration);
    }
  }

  protected ASTState getState(Node node) {
    String owner = findOwner(node);
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
    final ClassDefinitionVisitor delegate = this;
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

package contractstudy.usage.collectContracts.annotation;

public class AndroidSupportAnnotationExtractor extends AbstractAnnotationExtractor {

  // android.support.annotation precedes androidx.annotation, but we want to consider both as the same
  public AndroidSupportAnnotationExtractor() {
    super("AndroidX", "android.support.annotation");
  }

}

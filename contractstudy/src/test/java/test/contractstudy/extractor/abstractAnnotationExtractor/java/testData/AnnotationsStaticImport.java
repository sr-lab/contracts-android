package test.contractstudy.extractor.abstractAnnotationExtractor.java.testData;

import javax.validation.constraints.*;
import static java.lang.Math.*;

public class AnnotationsStaticImport {

  @NotNull
  private String manufacturer;

  @NotNull
  @Size(min = 2, max = 14)
  private String licensePlate;

  @Min(2)
  private int seatCount;

  public AnnotationsStaticImport(String manufacturer, String licencePlate, int seatCount) {
    this.manufacturer = manufacturer;
    this.licensePlate = licencePlate;
    this.seatCount = seatCount;
    double r = cos(PI * PI);
  }

  public String get(@Max(5) int index) {
    return "string";
  }
}

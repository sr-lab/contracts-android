package contractstudy.collectContracts.abstractAnnotationExtractor.java.testData;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import static java.lang.Math.PI;
import static java.lang.Math.cos;

public class AnnotationsStaticImport {

  @NotNull
  private final String manufacturer;

  @NotNull
  @Size(min = 2, max = 14)
  private final String licensePlate;

  @Min(2)
  private final int seatCount;

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

package test.contractstudy.extractor.abstractAnnotationExtractor.java.testData;

import javax.validation.constraints.*;

public class AnnotationsWildCard {

  @NotNull
  private String manufacturer;

  @NotNull
  @Size(min = 2, max = 14)
  private String licensePlate;

  @Min(2)
  private int seatCount;

  public AnnotationsWildCard(String manufacturer, String licencePlate, int seatCount) {
    this.manufacturer = manufacturer;
    this.licensePlate = licencePlate;
    this.seatCount = seatCount;
  }

  public String get(@Max(5) int index) {
    return "string";
  }
}

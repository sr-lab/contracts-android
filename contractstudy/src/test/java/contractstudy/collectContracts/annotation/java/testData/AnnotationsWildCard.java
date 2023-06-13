package contractstudy.collectContracts.annotation.java.testData;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class AnnotationsWildCard {

  @NotNull
  private final String manufacturer;

  @NotNull
  @Size(min = 2, max = 14)
  private final String licensePlate;

  @Min(2)
  private final int seatCount;

  public AnnotationsWildCard(String manufacturer, @NotNull String licencePlate, int seatCount) {
    this.manufacturer = manufacturer;
    this.licensePlate = licencePlate;
    this.seatCount = seatCount;
  }

  public String get(@Max(5) int index) {
    return "string";
  }
}

package contractstudy.collectContracts.annotation.java.testData;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

public class AnnotationsMultiple {

  @NotNull
  private final String manufacturer;

  @NotNull
  @Size(min = 2, max = 14)
  private final String licensePlate;

  @Min(2)
  private final int seatCount;

  public AnnotationsMultiple(String manufacturer, String licencePlate, int seatCount) {
    this.manufacturer = manufacturer;
    this.licensePlate = licencePlate;
    this.seatCount = seatCount;
  }

  @NotNull
  public String get(@Max(5) int index) {
    return "string";
  }

  @Size(min = 1)
  List<String> addProductToList(@NotNull String product) {
    List<String> list = new ArrayList<>();
    list.add(product);
    return list;
  }
}

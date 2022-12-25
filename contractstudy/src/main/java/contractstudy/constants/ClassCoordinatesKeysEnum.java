package contractstudy.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ClassCoordinatesKeysEnum {
  CLASS_NAME("className"), CU_NAME("cuName"), METHODS("methods"), PARENTS("parents");
  private final String keyword;
}

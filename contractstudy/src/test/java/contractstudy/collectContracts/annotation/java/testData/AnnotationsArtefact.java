package contractstudy.collectContracts.annotation.java.testData;

import android.annotation.SuppressLint;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

public class AnnotationsArtefact {

  @NotNull
  private final String manufacturer;

  public AnnotationsArtefact(String manufacturer) {
    this.manufacturer = manufacturer;
  }

  @SuppressLint({"SetJavaScriptEnabled", "NewApi", "ShowToast"})
  public String test123(String manufacturer) {
    return manufacturer;
  }

  public String get(@Max(5) int index) { // Method Parameter Artefact
    return "string";
  }

  @Size(min = 1)
  public List<String> addProductToList() {
    return new ArrayList<>();
  }
}

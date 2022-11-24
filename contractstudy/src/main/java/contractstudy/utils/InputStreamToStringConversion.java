package contractstudy.utils;

import com.google.common.io.CharStreams;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

public class InputStreamToStringConversion {

  private String result = null;

  public InputStreamToStringConversion(InputStream inputStream) throws IOException {
    try (Reader reader = new InputStreamReader(inputStream)) {
      result = CharStreams.toString(reader);
    }
  }

  public String getResult() {
    return result;
  }
}

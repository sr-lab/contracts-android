package contractstudy.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {

  public static String removeComments(String str) {
    Pattern regex = Pattern.compile(
      "(?s)('\"'|'\\\"'|\".*?(?<!\\\\)(?:\\\\\\\\)*\"|//[^\n]*|/\\*.*?\\*/)");
    return regex.matcher(str).replaceAll(
      m -> m.group().charAt(0) == '/' ? "" : Matcher.quoteReplacement(m.group())).trim();
  }

}

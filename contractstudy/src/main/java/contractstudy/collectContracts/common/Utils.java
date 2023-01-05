package contractstudy.collectContracts.common;

import com.github.javaparser.ast.expr.Expression;
import com.google.common.base.Preconditions;
import org.jetbrains.kotlin.psi.KtValueArgument;

import java.util.List;

/**
 * Misc useful utils.
 *
 * @author jens dietrich
 */
public class Utils {

  /**
   * Encode a message composed of several arguments into one string. This is commonly used in APIs.
   * TODO: usually this is a pattern where the first arg is a template string, and all following params are args.
   * We could encode it like this !
   */
  public static String encodeMessageArgs(List<Expression> args, int startPos) {
    Preconditions.checkArgument(startPos <= args.size());
    if (startPos == -1) {
      return null; // no message
    }
    String s = "";
    for (int i = startPos; i < args.size(); i++) {
      if (i > startPos) {
        s = s + ",";
      }
      s = s + args.get(i);
    }
    return s;
  }

  /**
   * Tries to get the message string from a list of arguments.
   * If message does not exist, returns "".
   * It assumes that message is always the last argument.
   * For example, in <<Validate.notNull(null, "Passed value is null")>>,
   * returns "Passed value is null".
   */
  public static String getMessageFromArguments(List<KtValueArgument> arguments) {
    String messageArgument = "";
    try {
      KtValueArgument potentialMessageArgument = arguments.get(arguments.size() - 1);
      if (potentialMessageArgument.getText().startsWith("\"") &&
        potentialMessageArgument.getText().endsWith("\"")) {
        messageArgument = potentialMessageArgument.getText();
      }
    } catch (IndexOutOfBoundsException ignored) {
    }
    return messageArgument;
  }

  /**
   * Trim the return type from the descriptor used to identify methods.
   */
  public static final String trimRetType(String desc) {
    return desc.split(" ")[1];
  }
}

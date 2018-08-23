package sh.okx.rankup.messages;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum Variable {
  PLAYER,
  OLD_RANK,
  OLD_RANK_NAME,
  RANK,
  RANK_NAME,
  MONEY,
  MONEY_NEEDED,
  PERCENT_DONE,
  PERCENT_LEFT;

  public static Variable getVariable(String name) {
    for(Variable variable : values()) {
      if(variable.toString().equalsIgnoreCase(name)) {
        return variable;
      }
    }
    return null;
  }

  public String replace(String message, String value) {
    Pattern pattern = Pattern.compile("\\{" + this + "}", Pattern.CASE_INSENSITIVE);
    Matcher matcher = pattern.matcher(message);
    return matcher.replaceAll(value);
  }
}

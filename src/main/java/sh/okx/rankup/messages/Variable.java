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
  AMOUNT,
  AMOUNT_NEEDED,
  PERCENT_DONE,
  PERCENT_LEFT;

  public String replace(String message, String value, String type) {
    Pattern pattern = Pattern.compile("\\{" + type + "_" + this + "}", Pattern.CASE_INSENSITIVE);
    Matcher matcher = pattern.matcher(message);
    return matcher.replaceAll(value);
  }
}

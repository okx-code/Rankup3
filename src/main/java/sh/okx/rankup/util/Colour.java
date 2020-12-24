package sh.okx.rankup.util;

import net.md_5.bungee.api.ChatColor;

public class Colour {

  private static final char altColorChar = '&';

  public static String translate(String string) {
    StringBuilder result = new StringBuilder();
    char[] b = string.toCharArray();
    for (int i = 0; i < b.length; i++) {
      if (b[i] == altColorChar && i < b.length - 1) {
        if ("0123456789AaBbCcDdEeFfKkLlMmNnOoRr".indexOf(b[i + 1]) > -1) {
          result.append(ChatColor.COLOR_CHAR).append(Character.toLowerCase(b[i + 1]));
          i ++;
        } else if ('#' == b[i + 1] && i < b.length - 7) {
          String hex = string.substring(i + 1, i + 8);
          result.append(ChatColor.of(hex));
          i += 7;
        } else {
          result.append(b[i]);
        }
      } else {
        result.append(b[i]);
      }
    }
    return result.toString();
  }
}

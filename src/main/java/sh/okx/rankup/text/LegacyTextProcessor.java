package sh.okx.rankup.text;

import java.util.Map;
import java.util.function.Function;
import sh.okx.rankup.messages.pebble.RankContext;
import sh.okx.rankup.placeholders.Placeholders;

public class LegacyTextProcessor implements TextProcessor {

  private final Map<String, Object> pebbleContext;
  private final Placeholders options;

  public LegacyTextProcessor(Map<String, Object> pebbleContext, Placeholders options) {
    this.pebbleContext = pebbleContext;
    this.options = options;
  }


  @Override
  public String process(String string) {
    StringBuilder output = new StringBuilder();
    StringBuilder buffer = new StringBuilder();
    boolean isPlaceholder = false;

    char[] chars = string.toCharArray();
    for (int i = 0; i < chars.length; i++) {
      char c = chars[i];
      if (c == '{') {
        if (i + 1 < chars.length) {
          if (chars[i + 1] != '{') {
            isPlaceholder = true;
          } else {
            output.append(c).append(chars[i + 1]);
            i++;
          }
        } else {
          output.append(c);
        }
      } else if (c == '}' && isPlaceholder) {
        output.append(replacePlaceholder(buffer.toString()));
        buffer.delete(0, buffer.length());
        isPlaceholder = false;
      } else if (isPlaceholder) {
        buffer.append(c);
      } else {
        output.append(c);
      }
    }

    return output.toString();
  }

  private String replacePlaceholder(String p) {
    if ("player".equalsIgnoreCase(p)) {
      return get("player", p);
    } else if ("old_rank".equalsIgnoreCase(p)) {
      return get("rank", p, o -> ((RankContext) o).getRank());
    } else if ("rank".equalsIgnoreCase(p)) {
      return get("next", p, o -> ((RankContext) o).getRank());
    } else if ("old_rank_name".equalsIgnoreCase(p)) {
      return get("rank", p, o -> ((RankContext) o).getName());
    } else if ("rank_name".equalsIgnoreCase(p)) {
      return get("next", p, o -> ((RankContext) o).getName());
    } else if ("money".equalsIgnoreCase(p)) {
      return get("rank", p, o -> this.options.getMoneyFormat().format(
          ((RankContext) o).getReq("money").getTotal()));
    } else if ("money_needed".equalsIgnoreCase(p)) {
      return get("rank", p, o -> this.options.getMoneyFormat().format(
          ((RankContext) o).getReq("money").getRemaining()));
    } else if (p.toLowerCase().startsWith("amount ")) {
      String requirement = p.substring("amount ".length());
      return get("rank", p, o -> this.options.getSimpleFormat()
          .format(((RankContext) o).getReq(requirement).getTotal()));
    } else if (p.toLowerCase().startsWith("amount_done ")) {
      String requirement = p.substring("amount_done ".length());
      return get("rank", p, o -> this.options.getSimpleFormat()
          .format(((RankContext) o).getReq(requirement).getProgress()));
    } else if (p.toLowerCase().startsWith("amount_needed ")) {
      String requirement = p.substring("amount_needed ".length());
      return get("rank", p, o -> this.options.getSimpleFormat()
          .format(((RankContext) o).getReq(requirement).getRemaining()));
    } else if (p.toLowerCase().startsWith("percent_done ")) {
      String requirement = p.substring("percent_done ".length());
      return get("rank", p, o -> this.options.getPercentFormat()
          .format(((RankContext) o).getReq(requirement).getPercent()));
    } else if (p.toLowerCase().startsWith("percent_left ")) {
      String requirement = p.substring("percent_left ".length());
      return get("rank", p, o -> this.options.getPercentFormat()
          .format(100 - ((RankContext) o).getReq(requirement).getPercent()));
    }

    return get(p, "{" + p + "}");
  }

  private String get(String key, String def) {
    return get(key, def, String::valueOf);
  }

  private String get(String key, String def, Function<Object, String> mapper) {
    Object val = pebbleContext.get(key);
    if (val == null) {
      return def;
    } else {
      return mapper.apply(val);
    }
  }
}

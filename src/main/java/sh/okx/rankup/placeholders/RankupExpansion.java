package sh.okx.rankup.placeholders;

import lombok.RequiredArgsConstructor;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import sh.okx.rankup.RankupPlugin;
import sh.okx.rankup.prestige.Prestige;
import sh.okx.rankup.prestige.Prestiges;
import sh.okx.rankup.ranks.Rank;
import sh.okx.rankup.ranks.Rankups;
import sh.okx.rankup.requirements.Requirement;

import java.util.Objects;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RequiredArgsConstructor
public class RankupExpansion extends PlaceholderExpansion {
  private static final Pattern PATTERN = Pattern.compile("(.*)#(.*)");

  private final RankupPlugin plugin;
  private final Placeholders placeholders;

  @Override
  public String onPlaceholderRequest(Player player, String params) {
    if (player == null) {
      return "";
    }
    params = params.toLowerCase();

    Rankups rankups = plugin.getRankups();
    Rank rank = rankups.getByPlayer(player);

    Prestiges prestiges = plugin.getPrestiges();
    Prestige prestige = null;
    if (prestiges != null) {
      prestige = prestiges.getByPlayer(player);
    }

    if (params.startsWith("requirement_")) {
      String[] parts = params.split("_", 3);
      return getPlaceholderRequirement(player, rank,
          replacePattern(parts[1]), parts.length > 2 ? parts[2] : "");
    } else if (params.startsWith("rank_requirement_")) {
      String[] parts = params.split("_", 5);
      return getPlaceholderRequirement(player, rankups.getByName(parts[2]),
          replacePattern(parts[3]), parts.length > 4 ? parts[4] : "");
//      return placeholders.getSimpleFormat().format(orElse(rankups.getByName(parts[2]).getRequirement(parts[3]), Requirement::getValueDouble, 0));
    } else if (params.startsWith("rank_money_")) {
      String[] parts = params.split("_", 4);
      double amount = Objects.requireNonNull(rankups.getByName(parts[2]), "Rankup " + parts[2] + " does not exist").getRequirement("money").getValueDouble();
      if (parts.length > 3 && parts[3].equalsIgnoreCase("left")) {
        amount = amount - plugin.getEconomy().getBalance(player);
      }
      return plugin.formatMoney(Math.max(0, amount));
    }

    switch (params) {
      case "current_prestige":
        requirePrestiging(prestiges, params);
        if (prestiges.isLast(plugin.getPermissions(), player)) {
          return prestiges.getLast();
        } else if (prestige == null || prestige.getRank() == null) {
          return getPlaceholder("no-prestige");
        } else {
          return prestige.getRank();
        }
      case "next_prestige":
        requirePrestiging(prestiges, params);
        if (prestiges.isLast(plugin.getPermissions(), player)) {
          return getPlaceholder("highest-rank");
        }
        return orElse(prestige, Prestige::getNext, prestiges.getFirst().getNext());
      case "prestige_money":
        requirePrestiging(prestiges, params);
        return String.valueOf(simplify(orElse(prestige, r -> r.isIn(player) ? r.getRequirement("money").getValueDouble() : 0, 0)));
      case "prestige_money_formatted":
        requirePrestiging(prestiges, params);
        return plugin.formatMoney(orElse(prestige, r -> r.isIn(player) ? r.getRequirement("money").getValueDouble() : 0, 0D));
      case "current_rank":
        if (rankups.isLast(plugin.getPermissions(), player)) {
          return rankups.getLast();
        } else if (rank == null) {
          return getPlaceholder("not-in-ladder");
        } else {
          return rank.getRank();
        }
      case "next_rank":
        if (rankups.isLast(plugin.getPermissions(), player)) {
          return getPlaceholder("highest-rank");
        }
        return orElsePlaceholder(rank, r -> orElsePlaceholder(rank, Rank::getNext, "highest-rank"), "not-in-ladder");
      case "money":
        return String.valueOf(getMoney(rank));
      case "money_formatted":
        return plugin.formatMoney(getMoney(rank).doubleValue());
      case "money_left":
        return String.valueOf(Math.max(0, orElse(rank, r -> simplify(plugin.getEconomy().getBalance(player) - r.getRequirement("money").getValueDouble()), 0).doubleValue()));
      case "money_left_formatted":
        return plugin.formatMoney(Math.max(0D, orElse(rank, r -> plugin.getEconomy().getBalance(player) - r.getRequirement("money").getValueDouble(), 0D)));
      case "percent_left":
        return String.valueOf(Math.max(0D, orElse(rank, r -> (1 - (plugin.getEconomy().getBalance(player) / r.getRequirement("money").getValueDouble())) * 100, 0).doubleValue()));
      case "percent_left_formatted":
        return placeholders.getPercentFormat().format(Math.max(0D, orElse(rank, r -> (1 - (plugin.getEconomy().getBalance(player) / r.getRequirement("money").getValueDouble())) * 100, 0).doubleValue()));
      case "percent_done":
        return String.valueOf(Math.min(100D, orElse(rank, r -> (plugin.getEconomy().getBalance(player) / r.getRequirement("money").getValueDouble()) * 100, 0).doubleValue()));
      case "percent_done_formatted":
        return placeholders.getPercentFormat().format(Math.min(100D, orElse(rank, r -> (plugin.getEconomy().getBalance(player) / r.getRequirement("money").getValueDouble()) * 100, 0).doubleValue()));
      case "prestige_percent_left_formatted":
        return placeholders.getPercentFormat().format(Math.max(0D, orElse(prestige, r -> (1 - (plugin.getEconomy().getBalance(player) / r.getRequirement("money").getValueDouble())) * 100, 0).doubleValue()));
      case "prestige_percent_done_formatted":
        return placeholders.getPercentFormat().format(Math.min(100D, orElse(prestige, r -> (plugin.getEconomy().getBalance(player) / r.getRequirement("money").getValueDouble()) * 100, 0).doubleValue()));
      default:
        return null;
    }
  }

  private Number getMoney(Rank rank) {
    return orElse(rank, r -> simplify(r.getRequirement("money").getValueDouble()), 0);
  }

  private void requirePrestiging(Prestiges prestiges, String params) {
    Objects.requireNonNull(prestiges, "Using %rankup_" + params + "% prestige placeholder but prestiging is disabled.");
  }

  private String getPlaceholderRequirement(Player player, Rank rank, String requirementName, String params) {
    if (rank == null) {
      return "";
    }
    Requirement requirement = rank.getRequirement(requirementName);
    switch (params) {
      case "":
        return orElse(requirement, Requirement::getValueString, "0");
      case "left":
        return placeholders.getSimpleFormat().format(orElse(requirement, r -> r.getRemaining(player), 0));
      case "done":
        return placeholders.getSimpleFormat().format(orElse(requirement, r -> r.getValueDouble() - r.getRemaining(player), 0));
      case "percent_left":
        return placeholders.getPercentFormat().format(orElse(requirement, r -> (r.getRemaining(player) / r.getValueDouble()) * 100, 0));
      case "percent_done":
        return placeholders.getPercentFormat().format(orElse(requirement, r -> (1 - (r.getRemaining(player) / r.getValueDouble())) * 100, 100));
      default:
        return null;
    }
  }

  private Number simplify(Number number) {
    if (number instanceof Float) {
      return (float) number % 1 == 0 ? number.intValue() : number;
    } else if (number instanceof Double) {
      return (double) number % 1 == 0 ? number.longValue() : number;
    } else {
      return number;
    }
  }

  private <T> String orElsePlaceholder(T t, Function<T, Object> value, Object fallback) {
    if (t == null) {
      return getPlaceholder(String.valueOf(fallback));
    }

    try {
      return String.valueOf(value.apply(t));
    } catch (NullPointerException ex) {
      return getPlaceholder(String.valueOf(fallback));
    }
  }

  private <T, R> R orElse(T t, Function<T, R> value, R fallback) {
    if (t == null) {
      return fallback;
    }

    try {
      return value.apply(t);
    } catch (NullPointerException ex) {
      return fallback;
    }
  }

  private String replacePattern(String string) {
    Matcher matcher = PATTERN.matcher(string);
    if (matcher.matches()) {
      return matcher.group(1) + "#" + matcher.group(2).replace("-", "_");
    } else {
      return string;
    }
  }

  private String getPlaceholder(String name) {
    return plugin.getConfig().getString("placeholders." + name);
  }

  @Override
  public String getIdentifier() {
    return "rankup";
  }

  @Override
  public String getAuthor() {
    return String.join(", ", plugin.getDescription().getAuthors());
  }

  @Override
  public String getVersion() {
    return plugin.getDescription().getVersion();
  }

  @Override
  public boolean persist() {
    return true;
  }

  @Override
  public boolean canRegister() {
    return true;
  }
}

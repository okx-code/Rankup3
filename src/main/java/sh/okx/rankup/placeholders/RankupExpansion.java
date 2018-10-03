package sh.okx.rankup.placeholders;

import lombok.RequiredArgsConstructor;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import sh.okx.rankup.Rankup;
import sh.okx.rankup.prestige.Prestige;
import sh.okx.rankup.prestige.Prestiges;
import sh.okx.rankup.ranks.Rank;
import sh.okx.rankup.ranks.Rankups;
import sh.okx.rankup.requirements.Requirement;

import java.util.function.Function;

@RequiredArgsConstructor
public class RankupExpansion extends PlaceholderExpansion {
  private final Rankup plugin;
  private final Placeholders placeholders;

  @Override
  public String onPlaceholderRequest(Player player, String params) {
    if (player == null) {
      return "";
    }
    params = params.toLowerCase();

    Rankups rankups = plugin.getRankups();
    Rank rank = rankups.getByPlayer(player);
    Rank nextRank = rank == null ? null : rankups.next(rank);

    Prestiges prestiges = plugin.getPrestiges();
    Prestige prestige = null;
    Prestige nextPrestige = null;
    if(prestiges != null) {
      prestige = prestiges.getByPlayer(player);
      nextPrestige = prestiges.next(prestige);
    }

    if (params.startsWith("requirement_")) {
      String[] parts = params.split("_", 3);
      return getPlaceholderRequirement(player, rank,
          parts[1], parts.length > 2 ? parts[2] : "");
    } else if (params.startsWith("rank_requirement_")) {
      String[] parts = params.split("_", 4);
      return placeholders.getSimpleFormat().format(orElse(rankups.getByName(parts[2]).getRequirement(parts[3]), Requirement::getValueDouble, 0));
    } else if (params.startsWith("rank_money_")) {
      String[] parts = params.split("_", 4);
      double amount = rankups.getByName(parts[2]).getRequirement("money").getValueDouble();
      if (parts.length > 3 && parts[3].equalsIgnoreCase("left")) {
        amount = amount - plugin.getEconomy().getBalance(player);
      }
      return plugin.formatMoney(Math.max(0, amount));
    }

    switch (params) {
      case "current_prestige":
        return prestige.getRank();
      case "current_prestige_name":
        return prestige.getName();
      case "next_prestige":
        return orElsePlaceholder(nextPrestige, Prestige::getRank, "highest-rank");
      case "next_prestige_name":
        return orElsePlaceholder(nextPrestige, Prestige::getName, "highest-rank");
      case "prestige_money":
        return String.valueOf(simplify(orElse(prestige, r -> r.isEligable(player) ? r.getRequirement("money").getValueDouble() : 0, 0)));
      case "prestige_money_formatted":
        return plugin.formatMoney(orElse(prestige, r -> r.isEligable(player) ? r.getRequirement("money").getValueDouble() : 0, 0D));
      case "current_rank":
        return orElsePlaceholder(rank, Rank::getRank, "not-in-ladder");
      case "current_rank_name":
        return orElsePlaceholder(rank, Rank::getName, "not-in-ladder");
      case "next_rank":
        return orElsePlaceholder(rank, r -> orElsePlaceholder(nextRank, Rank::getRank, "highest-rank"), "not-in-ladder");
      case "next_rank_name":
        return orElsePlaceholder(rank, r -> orElsePlaceholder(nextRank, Rank::getName, "highest-rank"), "not-in-ladder");
      case "money":
        return String.valueOf(orElse(rank, r -> simplify(r.getRequirement("money").getValueDouble()), 0));
      case "money_formatted":
        return plugin.formatMoney(orElse(rank, r -> r.getRequirement("money").getValueDouble(), 0D));
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
      default:
        return null;
    }
  }

  private String getPlaceholderRequirement(Player player, Rank rank, String requirementName, String params) {
    if (rank == null) {
      return "";
    }
    Requirement requirement = rank.getRequirement(requirementName);
    switch (params) {
      case "":
        return placeholders.getSimpleFormat().format(orElse(requirement, Requirement::getValueDouble, 0));
      case "left":
        return placeholders.getSimpleFormat().format(orElse(requirement, r -> r.getRemaining(player), 0));
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
}

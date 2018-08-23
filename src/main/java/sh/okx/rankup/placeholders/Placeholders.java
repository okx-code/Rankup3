package sh.okx.rankup.placeholders;

import lombok.Getter;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import sh.okx.rankup.Rankup;
import sh.okx.rankup.ranks.Rank;
import sh.okx.rankup.ranks.Rankups;
import sh.okx.rankup.ranks.requirements.Requirement;

import java.text.DecimalFormat;
import java.util.function.Function;

public class Placeholders extends PlaceholderExpansion {
  private final Rankup plugin;
  @Getter
  private final DecimalFormat moneyFormat;
  @Getter
  private final DecimalFormat percentFormat;
  @Getter
  private final DecimalFormat simpleFormat;

  public Placeholders(Rankup plugin) {
    this.plugin = plugin;
    this.moneyFormat = new DecimalFormat(plugin.getConfig().getString("placeholders.money-format"));
    this.percentFormat = new DecimalFormat(plugin.getConfig().getString("placeholders.percent-format"));
    this.simpleFormat = new DecimalFormat(plugin.getConfig().getString("placeholders.simple-format"));
  }

  @Override
  public String onPlaceholderRequest(Player player, String params) {
    if (player == null) {
      return "";
    }
    params = params.toLowerCase();

    Rankups rankups = plugin.getRankups();
    Rank rank = rankups.getRank(player);
    Rank next = null;
    if (rank != null) {
      next = rankups.nextRank(rank);
    }

    if(params.startsWith("requirement_")) {
      String[] parts = params.split("_", 3);
      return getPlaceholderRequirement(player, rank,
          parts[1], parts.length > 2 ? parts[2] : "");
    }

    switch (params) {
      case "current_rank":
        return orElsePlaceholder(rank, Rank::getRank, "not-in-ladder");
      case "current_rank_name":
        return orElsePlaceholder(rank, Rank::getRank, "not-in-ladder");
      case "current_rank_money":
        return orElsePlaceholder(rank, r -> simplify(r.getRequirement("money").getAmount()), 0);
      case "current_rank_money_formatted":
        return moneyFormat.format(orElsePlaceholder(rank, r -> r.getRequirement("money").getAmount(), 0));
      case "next_rank":
        if (rank == null) {
          return getPlaceholder("not-in-ladder");
        } else if (next == null) {
          return getPlaceholder("highest-rank");
        } else {
          return next.getRank();
        }
      case "next_rank_name":
        if (rank == null) {
          return getPlaceholder("not-in-ladder");
        } else if (next == null) {
          return getPlaceholder("highest-rank");
        } else {
          return next.getName();
        }
      case "next_rank_money":
        return orElsePlaceholder(next, r -> simplify(r.getRequirement("money").getAmount()), 0);
      case "next_rank_money_formatted":
        return moneyFormat.format(orElsePlaceholder(next, r -> r.getRequirement("money").getAmount(), 0));
      case "next_rank_money_left":
        return orElsePlaceholder(next, r -> simplify(plugin.getEconomy().getBalance(player) - r.getRequirement("money").getAmount()), 0);
      case "next_rank_money_left_formatted":
        return moneyFormat.format(orElsePlaceholder(next, r -> plugin.getEconomy().getBalance(player) - r.getRequirement("money").getAmount(), 0));
      case "next_rank_percent_left":
        return orElsePlaceholder(next, r -> (1-(plugin.getEconomy().getBalance(player) / r.getRequirement("money").getAmount())) * 100, 0);
      case "next_rank_percent_left_formatted":
        return percentFormat.format(orElsePlaceholder(next, r -> (1-(plugin.getEconomy().getBalance(player) / r.getRequirement("money").getAmount())) * 100, 0));
      case "next_rank_percent_done":
        return orElsePlaceholder(next, r -> (plugin.getEconomy().getBalance(player) / r.getRequirement("money").getAmount()) * 100, 0);
      case "next_rank_percent_done_formatted":
        return percentFormat.format(orElsePlaceholder(next, r -> (plugin.getEconomy().getBalance(player) / r.getRequirement("money").getAmount()) * 100, 0));
      default:
        return null;
    }
  }

  private String getPlaceholderRequirement(Player player, Rank rank, String requirementName, String params) {
    if(rank == null) {
      return "";
    }
    Requirement requirement = rank.getRequirement(requirementName);
    switch(params) {
      case "":
        return simpleFormat.format(orElse(requirement, Requirement::getAmount, 0));
      case "left":
        return simpleFormat.format(orElse(requirement, r -> r.getRemaining(player), 0));
      case "percent_left":
        return percentFormat.format(orElse(requirement, r -> (r.getRemaining(player) / r.getAmount()) * 100, 0));
      case "percent_done":
        return percentFormat.format(orElse(requirement, r -> (1-(r.getRemaining(player) / r.getAmount())) * 100, 100));
      default:
        return null;
    }
  }

  private Number simplify(Number number) {
    if (number instanceof Float) {
      return (float) number % 1 == 0 ? (int) number : number;
    } else if (number instanceof Double) {
      return (double) number % 1 == 0 ? (long) number : number;
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

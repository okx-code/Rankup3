package sh.okx.rankup.placeholders;

import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import sh.okx.rankup.RankupPlugin;
import sh.okx.rankup.prestige.Prestige;
import sh.okx.rankup.prestige.Prestiges;
import sh.okx.rankup.ranks.Rank;
import sh.okx.rankup.ranks.RankElement;
import sh.okx.rankup.ranks.Rankups;
import sh.okx.rankup.requirements.Requirement;

import java.util.Objects;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RequiredArgsConstructor
public class RankupExpansion implements Expansion {
    private static final Pattern PATTERN = Pattern.compile("(.*)#(.*)");

    private final RankupPlugin plugin;
    private final Placeholders placeholders;

    @Override
    public String placeholder(Player player, String params) {
        if (player == null) {
            return "";
        }
        params = params.toLowerCase();

        Rankups rankups = plugin.getRankups();
        RankElement<Rank> rankElement = rankups.getByPlayer(player);
        Rank rank = rankElement == null ? null : rankElement.getRank();

        Prestiges prestiges = plugin.getPrestiges();
        RankElement<Prestige> prestigeElement = null;
        Prestige prestige = null;
        if (prestiges != null) {
            prestigeElement = prestiges.getByPlayer(player);
            prestige = prestigeElement == null ? null : prestigeElement.getRank();
        }

        if (params.startsWith("requirement_")) {
            String[] parts = params.split("_", 3);
            return getPlaceholderRequirement(player, rank,
                    replacePattern(parts[1]), parts.length > 2 ? parts[2] : "");
        } else if (params.startsWith("rank_requirement_")) {
            String[] parts = params.split("_", 5);
            return getPlaceholderRequirement(player, rankups.getRankByName(parts[2]),
                    replacePattern(parts[3]), parts.length > 4 ? parts[4] : "");
        } else if (params.startsWith("rank_money_")) {
            String[] parts = params.split("_", 4);
            double amount = Objects.requireNonNull(rankups.getRankByName(parts[2]), "Rankup " + parts[2] + " does not exist").getRequirement(player, "money").getValueDouble();
            if (parts.length > 3 && parts[3].equalsIgnoreCase("left")) {
                amount = amount - plugin.getEconomy().getBalance(player);
            }
            return plugin.getPlaceholders().formatMoney(Math.max(0, amount));
        } else if (params.startsWith("status_")) {
            String[] parts = params.split("_",  2);
            Rank statusRank = rankups.getRankByName(parts[1]);

            if (statusRank == null) {
                return null;
            }
            if (rank == null) {
                return getPlaceholder("status.incomplete");
            }
            if (statusRank.equals(rank)) {
                return getPlaceholder("status.current");
            }

            // is playerRank before or after statusRank?
            for (RankElement<Rank> element : rankups.getTree().asList()) {
                if (element.getRank().equals(statusRank)) {
                    return getPlaceholder("status.complete");
                } else if (element.getRank().equals(rank)) {
                    return getPlaceholder("status.incomplete");
                }
            }

            // this should not happen
            return null;
        }

        switch (params) {
            case "current_prestige":
                requirePrestiging(prestiges, params);
                if (prestige == null || prestige.getRank() == null) {
                    return getPlaceholder("no-prestige");
                } else {
                    return prestige.getRank();
                }
            case "current_prestige_name":
                requirePrestiging(prestiges, params);
                if (prestige == null || prestige.getRank() == null) {
                    return getPlaceholder("no-prestige");
                } else {
                    return prestige.getDisplayName();
                }
            case "next_prestige":
                requirePrestiging(prestiges, params);
                if (prestigeElement != null && !prestigeElement.hasNext()) {
                    return getPlaceholder("highest-rank");
                }
                return orElse(prestigeElement, e -> e.getNext().getRank().getRank(), prestiges.getTree().getFirst().getNext().getRank().getRank());
            case "next_prestige_name":
                requirePrestiging(prestiges, params);
                if (prestigeElement != null && !prestigeElement.hasNext()) {
                    return getPlaceholder("highest-rank");
                } else {
                    return orElse(prestigeElement, e -> e.getNext().getRank().getDisplayName(), prestiges.getTree().getFirst().getNext().getRank().getDisplayName());
                }
            case "prestige_money":
                requirePrestiging(prestiges, params);
                return String.valueOf(simplify(orElse(prestige, r -> r.isIn(player) ? r.getRequirement(player, "money").getValueDouble() : 0, 0)));
            case "prestige_money_formatted":
                requirePrestiging(prestiges, params);
                return plugin.getPlaceholders().formatMoney(orElse(prestige, r -> r.isIn(player) ? r.getRequirement(player, "money").getValueDouble() : 0, 0D));
            case "current_rank":
                return orElse(rank, Rank::getRank, getPlaceholder("not-in-ladder"));
            case "current_rank_name":
                return orElse(rank, Rank::getDisplayName, getPlaceholder("not-in-ladder"));
            case "next_rank":
                if (rankElement != null && !rankElement.hasNext()) {
                    return getPlaceholder("highest-rank");
                } else {
                    return orElsePlaceholder(rankElement, e -> e.getNext().getRank().getRank(), "not-in-ladder");
                }
            case "next_rank_name":
                if (rankElement != null && !rankElement.hasNext()) {
                    return getPlaceholder("highest-rank");
                } else {
                    return orElsePlaceholder(rankElement, e -> e.getNext().getRank().getDisplayName(), "not-in-ladder");
                }
            case "money":
                return String.valueOf(getMoney(player, rank));
            case "money_formatted":
                return placeholders.formatMoney(getMoney(player, rank).doubleValue());
            case "money_left":
                return String.valueOf(Math.max(0, orElse(rank, r -> simplify(r.getRequirement(player, "money").getValueDouble() - plugin.getEconomy().getBalance(player)), 0).doubleValue()));
            case "money_left_formatted":
                return placeholders.formatMoney(Math.max(0D, orElse(rank, r -> r.getRequirement(player, "money").getValueDouble() - plugin.getEconomy().getBalance(player), 0D)));
            case "percent_left":
                return String.valueOf(Math.max(0D, orElse(rank, r -> (1 - (plugin.getEconomy().getBalance(player) / r.getRequirement(player, "money").getValueDouble())) * 100, 0).doubleValue()));
            case "percent_left_formatted":
                return placeholders.getPercentFormat().format(Math.max(0D, orElse(rank, r -> (1 - (plugin.getEconomy().getBalance(player) / r.getRequirement(player, "money").getValueDouble())) * 100, 0).doubleValue()));
            case "percent_done":
                return String.valueOf(Math.min(100D, orElse(rank, r -> (plugin.getEconomy().getBalance(player) / r.getRequirement(player, "money").getValueDouble()) * 100, 0).doubleValue()));
            case "percent_done_formatted":
                return placeholders.getPercentFormat().format(Math.min(100D, orElse(rank, r -> (plugin.getEconomy().getBalance(player) / r.getRequirement(player, "money").getValueDouble()) * 100, 0).doubleValue()));
            case "prestige_percent_left_formatted":
                return placeholders.getPercentFormat().format(Math.max(0D, orElse(prestige, r -> (1 - (plugin.getEconomy().getBalance(player) / r.getRequirement(player, "money").getValueDouble())) * 100, 0).doubleValue()));
            case "prestige_percent_done_formatted":
                return placeholders.getPercentFormat().format(Math.min(100D, orElse(prestige, r -> (plugin.getEconomy().getBalance(player) / r.getRequirement(player, "money").getValueDouble()) * 100, 0).doubleValue()));
            default:
                return null;
        }
    }

    private Number getMoney(Player player, Rank rank) {
        return orElse(rank, r -> simplify(r.getRequirement(player, "money").getValueDouble()), 0);
    }

    private void requirePrestiging(Prestiges prestiges, String params) {
        Objects.requireNonNull(prestiges, "Using %rankup_" + params + "% prestige placeholder but prestiging is disabled.");
    }

    private String getPlaceholderRequirement(Player player, Rank rank, String requirementName, String params) {
        if (rank == null) {
            return "";
        }
        Requirement requirement = rank.getRequirement(player, requirementName);
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
}

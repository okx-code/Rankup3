package sh.okx.rankup.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Statistic;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import sh.okx.rankup.RankupPlugin;
import sh.okx.rankup.prestige.Prestige;
import sh.okx.rankup.prestige.Prestiges;
import sh.okx.rankup.ranks.Rank;
import sh.okx.rankup.ranks.RankElement;
import sh.okx.rankup.ranks.Rankups;
import sh.okx.rankup.util.UpdateNotifier;

public class InfoCommand implements TabExecutor {
  private final RankupPlugin plugin;

  private final UpdateNotifier notifier;

  public InfoCommand(RankupPlugin plugin, UpdateNotifier notifier) {
    this.plugin = plugin;
    this.notifier = notifier;
  }

  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    if (args.length > 0) {
      if (args[0].equalsIgnoreCase("reload") && sender.hasPermission("rankup.reload")) {
        plugin.reload(false);
        if (!plugin.error(sender)) {
          sender.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "Rankup " + ChatColor.YELLOW + "Reloaded configuration files.");
        }
        return true;
      } else if (args[0].equalsIgnoreCase("forcerankup") && sender.hasPermission("rankup.force")) {
        if (args.length < 2) {
          sender.sendMessage(ChatColor.YELLOW + "Usage: /" + label + " forcerankup <player>");
          return true;
        }

        Player player = Bukkit.getPlayer(args[1]);
        if (player == null) {
          sender.sendMessage(ChatColor.YELLOW + "Player not found.");
          return true;
        }

        Rankups rankups = plugin.getRankups();
        RankElement<Rank> rankElement = rankups.getByPlayer(player);
        if (rankElement == null) {
          sender.sendMessage(ChatColor.YELLOW + "That player is not in any rankup groups.");
          return true;
        } else if (!rankElement.hasNext()) {
          sender.sendMessage(ChatColor.YELLOW + "That player is at the last rank.");
          return true;
        }

        Rank rank = rankElement.getRank();

        plugin.getHelper().doRankup(player, rankElement);
        plugin.getHelper().sendRankupMessages(player, rankElement);
        sender.sendMessage(ChatColor.GREEN + "Successfully forced "
            + ChatColor.GOLD + player.getName()
            + ChatColor.GREEN + " to rankup from " + ChatColor.GOLD + rank.getRank()
            + ChatColor.GREEN + " to " + ChatColor.GOLD + rank.getNext());
        return true;
      } else if (args[0].equalsIgnoreCase("forceprestige") && sender.hasPermission("rankup.force")) {
        if (plugin.getPrestiges() == null) {
          sender.sendMessage(ChatColor.RED + "Prestige is disabled.");
          return true;
        }

        if (args.length < 2) {
          sender.sendMessage(ChatColor.YELLOW + "Usage: /" + label + " forceprestige <player>");
          return true;
        }

        Player player = Bukkit.getPlayer(args[1]);
        if (player == null) {
          sender.sendMessage(ChatColor.YELLOW + "Player not found.");
          return true;
        }

        Prestiges prestiges = plugin.getPrestiges();
        RankElement<Prestige> rankElement = prestiges.getByPlayer(player);
        if (!rankElement.hasNext()) {
          sender.sendMessage(ChatColor.YELLOW + "That player is at the last prestige.");
          return true;
        }

        Prestige prestige = rankElement.getRank();
        if (prestige == null) {
          sender.sendMessage(ChatColor.YELLOW + "That player is not in any prestige groups.");
          return true;
        }

        plugin.getHelper().doPrestige(player, rankElement);
        plugin.getHelper().sendPrestigeMessages(player, rankElement);
        sender.sendMessage(ChatColor.GREEN + "Successfully forced "
            + ChatColor.GOLD + player.getName()
            + ChatColor.GREEN + " to prestige "
            + ChatColor.GOLD + prestige.getRank()
            + ChatColor.GREEN + " from " + ChatColor.GOLD + prestige.getFrom()
            + ChatColor.GREEN + " to " + ChatColor.GOLD + prestige.getTo());
        return true;
      } else if(args[0].equalsIgnoreCase("rankdown") && sender.hasPermission("rankup.force")) {
        if (args.length < 2) {
          sender.sendMessage(ChatColor.YELLOW + "Usage: /" + label + " rankdown <player>");
          return true;
        }

        Player player = Bukkit.getPlayer(args[1]);
        if (player == null) {
          sender.sendMessage(ChatColor.YELLOW + "Player not found.");
          return true;
        }

        RankElement<Rank> currentRankElement = plugin.getRankups().getByPlayer(player);
        if (currentRankElement == null) {
          sender.sendMessage(ChatColor.YELLOW + "That player is not in any rankup groups.");
          return true;
        }
        Rank currentRank = currentRankElement.getRank();

        if (currentRankElement.isRootNode()) {
          sender.sendMessage(ChatColor.YELLOW + "That player is in the first rank and cannot be ranked down.");
          return true;
        }

        RankElement<Rank> prevRankElement = plugin.getRankups().getTree().getFirst();
        while(prevRankElement.hasNext() && !prevRankElement.getNext().getRank().equals(currentRank)) {
          prevRankElement = prevRankElement.getNext();
        }

        if (!prevRankElement.hasNext()) {
          sender.sendMessage(ChatColor.YELLOW + "Could not match previous rank.");
          return true;
        }
        Rank prevRank = prevRankElement.getRank();

        if (prevRankElement.getRank() != null) {
          plugin.getPermissions().removeGroup(player.getUniqueId(), currentRank.getRank());
        }
        plugin.getPermissions().addGroup(player.getUniqueId(), prevRank.getRank());

        sender.sendMessage(ChatColor.GREEN + "Successfully forced "
                + ChatColor.GOLD + player.getName()
                + ChatColor.GREEN + " to rank down from " + ChatColor.GOLD + currentRank.getRank()
                + ChatColor.GREEN + " to " + ChatColor.GOLD + prevRank.getRank());
        return true;
      } else if (args[0].equalsIgnoreCase("placeholders") && sender.hasPermission("rankup.admin")) {
        sender.sendMessage("--- Rankup placeholders ---");
        if (args.length > 1 && args[1].equalsIgnoreCase("status")) {
          for (Rank rank : plugin.getRankups().getTree()) {
            String placeholder = "status_" + rank.getRank();
            sender.sendMessage(placeholder + ": " + plugin.getPlaceholders().getExpansion().placeholder(sender instanceof Player ? (Player) sender : null, placeholder));
          }
          return true;
        }

        String[] placeholders = new String[] {
                "prestige_money_formatted",
                "prestige_percent_left_formatted",
                "prestige_percent_done_formatted",
                "money_formatted",
                "money_left_formatted",
                "percent_left_formatted",
                "percent_done_formatted",
                "current_prestige",
                "next_prestige",
                "current_rank",
                "next_rank",
        };
        for (String placeholder : placeholders) {
          String result;
          try {
            result = plugin.getPlaceholders().getExpansion().placeholder(sender instanceof Player ? (Player) sender : null, placeholder);
          } catch (Exception e) {
            result = e.getClass().getSimpleName() + ", " + e.getMessage();
          }
          sender.sendMessage(placeholder + ": " + result);
        }
        return true;
      } else if (args[0].equalsIgnoreCase("tree") && sender.hasPermission("rankup.admin")) {
        RankElement<Rank> element = plugin.getRankups().getTree().getFirst();
        while (element.hasNext()) {
          Rank rank = element.getRank();
          RankElement<Rank> next = element.getNext();
          Rank nextRank = next.getRank();
          sender.sendMessage(rank.getRank() + " (" + rank.getNext() + ") -> " + nextRank.getRank() + " (" + nextRank.getNext() + ")");
          element = next;
        }
        return true;
      } else if (args[0].equalsIgnoreCase("playtime") && (sender.hasPermission("rankup.playtime.get") || sender.hasPermission("rankup.playtime"))) {
        Statistic playOneTick;
        try {
          playOneTick = Statistic.valueOf("PLAY_ONE_MINUTE");
        } catch (IllegalArgumentException e) {
          // statistic was changed in 1.13.
          playOneTick = Statistic.valueOf("PLAY_ONE_TICK");
        }

        if (args.length > 1) {
          if (args[1].equalsIgnoreCase("get") && sender.hasPermission("rankup.playtime.get")) {
            Player player;
            if (args.length > 2) {
              // pru playtime get Okx
              player = Bukkit.getPlayer(args[2]);
              if (player == null) {
                sender.sendMessage(ChatColor.GRAY + "Player not found");
                return true;
              }
            } else {
              if (sender instanceof Player) {
                player = (Player) sender;
              } else {
                sender.sendMessage(ChatColor.GREEN + "/" + label + " " + args[0] + " get [player] " + ChatColor.YELLOW
                    + " Get amount of minutes played");
                return true;
              }
            }

            int ticks = player.getStatistic(playOneTick);
            long minutes = (long) (ticks / 20D / 60);

            String who;
            if (player == sender) {
              who = "You have";
            } else {
              who = player.getName() + " has";
            }
            player.sendMessage(ChatColor.LIGHT_PURPLE + who + " played for " + minutes + " minutes.");
            return true;
          } else if (args[1].equalsIgnoreCase("set") && sender.hasPermission("rankup.playtime")) {
            if (args.length < 4) {
              sender.sendMessage(ChatColor.GREEN + "/" + label + " " + args[0] + " set <player> <minutes>" + ChatColor.YELLOW + " Update the playtime statistic for a player");
              return true;
            }

            Player player = Bukkit.getPlayer(args[2]);
            if (player == null) {
              sender.sendMessage(ChatColor.GRAY + "Player not found");
              return true;
            }

            int minutes;
            try {
              minutes = Integer.parseInt(args[3]);
            } catch (NumberFormatException e) {
              sender.sendMessage(ChatColor.GRAY + "Invalid number: " + args[3]);
              return true;
            }

            player.setStatistic(playOneTick, minutes * 20 * 60);
            player.sendMessage(ChatColor.LIGHT_PURPLE + "Updated playtime for " + player.getName() + " to " + minutes + " minutes");
            return true;
          } else if (args[1].equalsIgnoreCase("add") && sender.hasPermission("rankup.playtime")) {
            if (args.length < 4) {
              sender.sendMessage(ChatColor.GREEN + "/" + label + " " + args[0] + " add <player> <minutes>" + ChatColor.YELLOW + " Increase the playtime statistic for a player");
              return true;
            }

            Player player = Bukkit.getPlayer(args[2]);
            if (player == null) {
              sender.sendMessage(ChatColor.GRAY + "Player not found");
              return true;
            }

            int minutes;
            try {
              minutes = Integer.parseInt(args[3]);
            } catch (NumberFormatException e) {
              sender.sendMessage(ChatColor.GRAY + "Invalid number: " + args[3]);
              return true;
            }

            int oldMinutes = player.getStatistic(playOneTick) / 20 / 60;
            if (minutes > 0) {
              player.incrementStatistic(playOneTick, minutes * 20 * 60);
            } else if (minutes < 0) {
              if (oldMinutes + minutes < 0) {
                player.sendMessage(ChatColor.GRAY + "Playtime cannot be negative");
                return true;
              }
              player.decrementStatistic(playOneTick, -minutes * 20 * 60);
            }
            int newMinutes = oldMinutes + minutes;
            player.sendMessage(ChatColor.LIGHT_PURPLE + "Increased playtime for " + player.getName() + " to " + oldMinutes + (minutes >= 0 ? "+" : "") + minutes + "=" + newMinutes + " minutes");
            return true;
          }
        }
        if (sender.hasPermission("rankup.playtime.get")) {
          sender.sendMessage(
              ChatColor.GREEN + "/" + label + " " + args[0] + " get [player] " + ChatColor.YELLOW
                  + " Get amount of minutes played");
        }
        if (sender.hasPermission("rankup.playtime")) {
          sender.sendMessage(
              ChatColor.GREEN + "/" + label + " " + args[0] + " set <player> <minutes>"
                  + ChatColor.YELLOW + " Update the playtime statistic for a player");
          sender.sendMessage(
              ChatColor.GREEN + "/" + label + " " + args[0] + " add <player> <minutes>"
                  + ChatColor.YELLOW + " Increase the playtime statistic for a player");
        }
        return true;
      }
    }

    PluginDescriptionFile description = plugin.getDescription();
    String version = description.getVersion();
    sender.sendMessage(
        ChatColor.GREEN + "" + ChatColor.BOLD + description.getName() + " " + version +
            ChatColor.YELLOW + " by " + ChatColor.BLUE + ChatColor.BOLD + String.join(", ", description.getAuthors()));
    if (sender.hasPermission("rankup.reload")) {
      sender.sendMessage(ChatColor.GREEN + "/" + label + " reload " + ChatColor.YELLOW + "Reloads configuration files.");
    }
    if (sender.hasPermission("rankup.force")) {
      sender.sendMessage(ChatColor.GREEN + "/" + label + " forcerankup <player> " + ChatColor.YELLOW + "Force a player to rankup, bypassing requirements.");
      if (plugin.getPrestiges() != null) {
        sender.sendMessage(
            ChatColor.GREEN + "/" + label + " forceprestige <player> " + ChatColor.YELLOW
                + "Force a player to prestige, bypassing requirements.");
      }
      sender.sendMessage(ChatColor.GREEN + "/" + label + " rankdown <player> " + ChatColor.YELLOW + "Force a player to move down one rank.");
    }
    if (sender.hasPermission("rankup.playtime")) {
      sender.sendMessage(ChatColor.GREEN + "/" + label + " playtime " + ChatColor.YELLOW + "View your playtime");
    }

    if (sender.hasPermission("rankup.checkversion")) {
      notifier.notify(sender, false);
    }

    return true;
  }

  @Override
  public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
    if (args.length == 1) {
      List<String> list = new ArrayList<>();
      if (sender.hasPermission("rankup.reload")) {
        list.add("reload");
      }
      if (sender.hasPermission("rankup.force")) {
        list.add("forcerankup");
        list.add("forceprestige");
        list.add("rankdown");
      }
      if (sender.hasPermission("rankup.playtime.get") || sender.hasPermission("rankup.playtime")) {
        list.add("playtime");
      }
      return StringUtil.copyPartialMatches(args[0], list, new ArrayList<>());
    } else if (args.length == 2) {
      if (args[0].equalsIgnoreCase("forcerankup") && sender.hasPermission("rankup.force")) {
        return StringUtil.copyPartialMatches(args[1], players(), new ArrayList<>());
      } else if (args[0].equalsIgnoreCase("forceprestige") && sender.hasPermission("rankup.force") && plugin.getPrestiges() != null) {
        return StringUtil.copyPartialMatches(args[1], players(), new ArrayList<>());
      } else if (args[0].equalsIgnoreCase("rankdown") && sender.hasPermission("rankup.force")) {
        return StringUtil.copyPartialMatches(args[1], players(), new ArrayList<>());
      } else if (args[0].equalsIgnoreCase("playtime")) {
        List<String> options = new ArrayList<>();
        if (sender.hasPermission("rankup.playtime.get")) {
          options.add("get");
        }
        if (sender.hasPermission("rankup.playtime")) {
          options.add("set");
          options.add("add");
        }
        return StringUtil.copyPartialMatches(args[1], options, new ArrayList<>());
      }
    }
    return Collections.emptyList();
  }

  private Iterable<String> players() {
    Set<String> players = new HashSet<>();
    for (Player player : Bukkit.getOnlinePlayers()) {
      players.add(player.getName());
    }
    return players;
  }
}

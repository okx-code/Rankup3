package sh.okx.rankup.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import sh.okx.rankup.RankupPlugin;

import sh.okx.rankup.prestige.Prestige;
import sh.okx.rankup.prestige.Prestiges;
import sh.okx.rankup.ranks.Rank;
import sh.okx.rankup.ranks.Rankups;
import sh.okx.rankup.util.UpdateNotifier;

public class InfoCommand implements CommandExecutor {
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
        if (rankups.isLast(plugin.getPermissions(), player)) {
          sender.sendMessage(ChatColor.YELLOW + "That player is at the last rank.");
          return true;
        }

        Rank rank = rankups.getByPlayer(player);
        if (rank == null) {
          sender.sendMessage(ChatColor.YELLOW + "That player is not in any rankup groups.");
          return true;
        }

        plugin.getHelper().doRankup(player, rank);
        plugin.getHelper().sendRankupMessages(player, rank);
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
        if (prestiges.isLast(plugin.getPermissions(), player)) {
          sender.sendMessage(ChatColor.YELLOW + "That player is at the last prestige.");
          return true;
        }

        Prestige prestige = prestiges.getByPlayer(player);
        if (prestige == null) {
          sender.sendMessage(ChatColor.YELLOW + "That player is not in any prestige groups.");
          return true;
        }

        plugin.getHelper().doPrestige(player, prestige);
        plugin.getHelper().sendPrestigeMessages(player, prestige);
        sender.sendMessage(ChatColor.GREEN + "Successfully forced "
            + ChatColor.GOLD + player.getName()
            + ChatColor.GREEN + " to prestige "
            + ChatColor.GOLD + prestige.getRank()
            + ChatColor.GREEN + " from " + ChatColor.GOLD + prestige.getFrom()
            + ChatColor.GREEN + " to " + ChatColor.GOLD + prestige.getTo());
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
      sender.sendMessage(ChatColor.GREEN + "/" + label + " forcerankup <player> " + ChatColor.YELLOW + "Force a player to rankup, bypassing requirements.");
      if (plugin.getPrestiges() != null) {
        sender.sendMessage(
            ChatColor.GREEN + "/" + label + " forceprestige <player> " + ChatColor.YELLOW
                + "Force a player to prestige, bypassing requirements.");
      }
    }

    if (sender.hasPermission("rankup.checkversion")) {
      notifier.notify(sender, false);
    }

    return true;
  }
}

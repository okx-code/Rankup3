package sh.okx.rankup.commands;

import java.util.Arrays;
import java.util.Map;
import java.util.WeakHashMap;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import sh.okx.rankup.RankupPlugin;
import sh.okx.rankup.gui.Gui;
import sh.okx.rankup.messages.Message;
import sh.okx.rankup.ranks.Rank;
import sh.okx.rankup.ranks.RankElement;
import sh.okx.rankup.ranks.Rankups;

@RequiredArgsConstructor
public class RankupCommand implements CommandExecutor {
  // weak hash maps so players going offline are automatically removed.
  // otherwise there is a potential (albeit small) memory leak.
  private final Map<Player, Long> confirming = new WeakHashMap<>();
  private final RankupPlugin plugin;

  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    if (plugin.error(sender)) {
      return true;
    }

    if (plugin.getConfig().getBoolean("enable-noconfirm", true) && args.length > 0 && args[0].equalsIgnoreCase("noconfirm")) {
      handleNoConfirm(sender, label, Arrays.copyOfRange(args, 1, args.length));
      return true;
    }

    // check if player
    if (!(sender instanceof Player)) {
      return false;
    }
    Player player = (Player) sender;

    Rankups rankups = plugin.getRankups();
    if (!plugin.getHelper().checkRankup(player)) {
      return true;
    }

    RankElement<Rank> rankElement = rankups.getByPlayer(player);

    FileConfiguration config = plugin.getConfig();
    String confirmationType = config.getString("confirmation-type").toLowerCase();

    // if they are on text confirming, rank them up
    // clicking on the gui cannot confirm a rankup
    if (confirmationType.equals("text") && confirming.containsKey(player) && !(args.length > 0 && args[0].equalsIgnoreCase("gui"))) {
      long time = System.currentTimeMillis() - confirming.remove(player);
      if (time < config.getInt("text.timeout") * 1000L) {
        plugin.getHelper().rankup(player);
        return true;
      }
    }

    switch (confirmationType) {
      case "text":
        confirming.put(player, System.currentTimeMillis());
        plugin.getMessage(rankElement.getRank(), Message.CONFIRMATION)
            .replacePlayer(player)
            .replaceOldRank(rankElement.getRank())
            .replaceRank(rankElement.getNext().getRank())
            .send(player);
        break;
      case "gui":
        Gui gui = Gui.of(player, rankElement.getRank(), rankElement.getNext().getRank(), plugin, args.length > 0 && args[0].equalsIgnoreCase("gui"));
        if (gui == null) {
          player.sendMessage(ChatColor.RED + "GUI is not available. Check console for more information.");
          return true;
        }
        gui.open(player);
        break;
      case "none":
        plugin.getHelper().rankup(player);
        break;
      default:
        throw new IllegalArgumentException("Invalid confirmation type " + confirmationType);
    }
    return true;
  }

  private void handleNoConfirm(CommandSender sender, String label, String[] args) {
    if (sender.hasPermission("rankup.noconfirm.other") && args.length > 0) {
      Player player = Bukkit.getPlayer(args[0]);
      if (player == null) {
        sender.sendMessage(ChatColor.RED + "Player not found: " + args[0]);
      } else {
        plugin.getHelper().rankup(player);
        sender.sendMessage(ChatColor.GREEN + "Triggered no-confirmation rankup for " + player.getName());
      }
    } else {
      if (!(sender instanceof Player)) {
        sender.sendMessage("/" + label + " noconfirm <player>");
        return;
      }

      plugin.getHelper().rankup((Player) sender);
    }
  }
}

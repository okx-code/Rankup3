package sh.okx.rankup.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import sh.okx.rankup.Rankup;
import sh.okx.rankup.gui.Gui;
import sh.okx.rankup.messages.Message;
import sh.okx.rankup.ranks.Rank;
import sh.okx.rankup.ranks.Rankups;

import java.util.Map;
import java.util.WeakHashMap;

public class RankupCommand implements CommandExecutor {
  // weak hash maps so players going offline are automatically removed.
  // otherwise there is a potential (but small) memory leak.
  private final Map<Player, Long> confirming = new WeakHashMap<>();

  private final Rankup plugin;

  public RankupCommand(Rankup plugin) {
    this.plugin = plugin;
  }

  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    // check if player
    if (!(sender instanceof Player)) {
      return false;
    }
    Player player = (Player) sender;

    Rankups rankups = plugin.getRankups();
    Rank rank = rankups.getRank(player);
    if (!plugin.checkRankup(player)) {
      return true;
    }

    FileConfiguration config = plugin.getConfig();
    String confirmationType = config.getString("confirmation-type").toLowerCase();

    // if they are on text confirming, rank them up
    if (confirmationType.equals("text") && confirming.containsKey(player)) {
      long time = System.currentTimeMillis() - confirming.remove(player);
      if (time < config.getInt("text.timeout") * 1000) {
        plugin.rankup(player);
        return true;
      }
    }

    switch (confirmationType) {
      case "text":
        confirming.put(player, System.currentTimeMillis());
        plugin.getMessage(rank, Message.CONFIRMATION)
            .replaceAll(player, rank, rankups.nextRank(rank))
            .send(player);
        break;
      case "gui":
        Gui.of(player, rank, rankups.nextRank(rank), plugin).open(player);
        break;
      case "none":
        plugin.rankup(player);
        break;
      default:
        throw new IllegalArgumentException("Invalid confirmation type " + confirmationType);
    }
    return true;
  }
}

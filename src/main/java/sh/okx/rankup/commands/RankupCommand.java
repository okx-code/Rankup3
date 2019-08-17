package sh.okx.rankup.commands;

import lombok.RequiredArgsConstructor;
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

@RequiredArgsConstructor
public class RankupCommand implements CommandExecutor {
  // weak hash maps so players going offline are automatically removed.
  // otherwise there is a potential (but small) memory leak.
  private final Map<Player, Long> confirming = new WeakHashMap<>();
  private final Rankup plugin;

  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    if (plugin.error(sender)) {
      return true;
    }

    // check if player
    if (!(sender instanceof Player)) {
      return false;
    }
    Player player = (Player) sender;

    Rankups rankups = plugin.getRankups();
    Rank rank = rankups.getByPlayer(player);
    if (!plugin.getHelper().checkRankup(player)) {
      return true;
    }
    /*Rank next = rankups.next(rank);
    if (next == null) {
      plugin.getLogger().severe("Rankup from " + rank.getRank() + " to " + rank.getNext() +
          " is defined but " + rank.getNext() + " does not exist.");
      plugin.getMessage(Message.INVALID_RANKUP).failIfEmpty().send(player);
      return true;
    }*/
    String next = rank.getNext();

    FileConfiguration config = plugin.getConfig();
    String confirmationType = config.getString("confirmation-type").toLowerCase();

    // if they are on text confirming, rank them up
    if (confirmationType.equals("text") && confirming.containsKey(player)) {
      long time = System.currentTimeMillis() - confirming.remove(player);
      if (time < config.getInt("text.timeout") * 1000) {
        plugin.getHelper().rankup(player);
        return true;
      }
    }

    switch (confirmationType) {
      case "text":
        confirming.put(player, System.currentTimeMillis());
        plugin.replaceMoneyRequirements(plugin.getMessage(rank, Message.CONFIRMATION)
            .replaceRanks(player, rank, next), player, rank)
            .send(player);
        break;
      case "gui":
        Gui.of(player, rank, next, plugin).open(player);
        break;
      case "none":
        plugin.getHelper().rankup(player);
        break;
      default:
        throw new IllegalArgumentException("Invalid confirmation type " + confirmationType);
    }
    return true;
  }
}

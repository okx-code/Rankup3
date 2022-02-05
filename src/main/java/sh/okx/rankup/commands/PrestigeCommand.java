package sh.okx.rankup.commands;

import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import sh.okx.rankup.RankupPlugin;
import sh.okx.rankup.gui.Gui;
import sh.okx.rankup.messages.Message;
import sh.okx.rankup.prestige.Prestige;
import sh.okx.rankup.prestige.Prestiges;
import sh.okx.rankup.ranks.Rank;
import sh.okx.rankup.ranks.RankElement;

import java.util.Map;
import java.util.WeakHashMap;

@RequiredArgsConstructor
public class PrestigeCommand implements CommandExecutor {
  private final Map<Player, Long> confirming = new WeakHashMap<>();
  private final RankupPlugin plugin;

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

    Prestiges prestiges = plugin.getPrestiges();
    if (!plugin.getHelper().checkPrestige(player)) {
      return true;
    }
    RankElement<Prestige> rankElement = prestiges.getByPlayer(player);
    Prestige prestige = rankElement.getRank();

    FileConfiguration config = plugin.getConfig();
    String confirmationType = config.getString("confirmation-type").toLowerCase();
    if (confirmationType.equals("text") && confirming.containsKey(player)) {
      long time = System.currentTimeMillis() - confirming.remove(player);
      if (time < config.getInt("text.timeout") * 1000) {
        plugin.getHelper().prestige(player);
        return true;
      }
    }

    switch (confirmationType) {
      case "text":
        confirming.put(player, System.currentTimeMillis());
        Prestige next = rankElement.getNext().getRank();
        Rank nextRank = next == null ? prestiges.getTree().last().getRank() : next;

        plugin.getMessage(prestige, Message.PRESTIGE_CONFIRMATION)
            .replacePlayer(player)
            .replaceOldRank(prestige)
            .replaceRank(nextRank)
            .send(player);
        break;
      case "gui":
        Gui gui = Gui.of(player, prestige, rankElement.getNext().getRank(), plugin, false);
        if (gui == null) {
          player.sendMessage(ChatColor.RED + "GUI is not available. Check console for more information.");
          return true;
        }
        gui.open(player);
        break;
      case "none":
        plugin.getHelper().prestige(player);
        break;
      default:
        throw new IllegalArgumentException("Invalid confirmation type " + confirmationType);
    }
    return true;
  }
}

package sh.okx.rankup.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import sh.okx.rankup.Rankup;
import sh.okx.rankup.gui.Gui;
import sh.okx.rankup.messages.Message;
import sh.okx.rankup.prestige.Prestige;
import sh.okx.rankup.prestige.Prestiges;

import java.util.Map;
import java.util.WeakHashMap;

public class PrestigeCommand implements CommandExecutor {
  private final Map<Player, Long> confirming = new WeakHashMap<>();

  private final Rankup plugin;

  public PrestigeCommand(Rankup plugin) {
    this.plugin = plugin;
  }

  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    // check if player
    if (!(sender instanceof Player)) {
      return false;
    }
    Player player = (Player) sender;

    Prestiges prestiges = plugin.getPrestiges();
    Prestige prestige = prestiges.getByPlayer(player);
    if (!plugin.checkPrestige(player)) {
      return true;
    }

    FileConfiguration config = plugin.getConfig();
    String confirmationType = config.getString("confirmation-type").toLowerCase();
    if (confirmationType.equals("text") && confirming.containsKey(player)) {
      long time = System.currentTimeMillis() - confirming.remove(player);
      if (time < config.getInt("text.timeout") * 1000) {
        plugin.prestige(player);
        return true;
      }
    }

    switch (confirmationType) {
      case "text":
        confirming.put(player, System.currentTimeMillis());
        plugin.replaceMoneyRequirements(plugin.getMessage(prestige, Message.CONFIRMATION)
            .replaceRanks(player, prestige, prestiges.next(prestige)), player, prestige)
            .replaceFromTo(prestige)
            .send(player);
        break;
      case "gui":
        Gui.of(player, prestige, prestiges.next(prestige), plugin).open(player);
        break;
      case "none":
        plugin.prestige(player);
        break;
      default:
        throw new IllegalArgumentException("Invalid confirmation type " + confirmationType);
    }
    return true;
  }
}

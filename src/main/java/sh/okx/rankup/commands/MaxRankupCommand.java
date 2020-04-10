package sh.okx.rankup.commands;

import lombok.RequiredArgsConstructor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import sh.okx.rankup.RankupPlugin;
import sh.okx.rankup.RankupHelper;
import sh.okx.rankup.ranks.Rank;

@RequiredArgsConstructor
public class MaxRankupCommand implements CommandExecutor {
  private final RankupPlugin plugin;

  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    if (!(sender instanceof Player)) {
      return false;
    }
    RankupHelper helper = plugin.getHelper();

    Player player = (Player) sender;

    if (!helper.checkRankup(player)) {
      return true;
    }

    do {
      Rank rank = plugin.getRankups().getByPlayer(player);
      rank.applyRequirements(player);

      helper.doRankup(player, rank);

      // if the individual-messages setting is disabled, only send the "well done you ranked up"
      // messages if they can't rank up any more.
      if (plugin.getConfig().getBoolean("max-rankup.individual-messages")
          || !helper.checkRankup(player, false)) {
        helper.sendRankupMessages(player, rank);
      }
    } while (helper.checkRankup(player, false));

    return true;
  }
}

package sh.okx.rankup.commands;

import lombok.RequiredArgsConstructor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import sh.okx.rankup.RankupPlugin;
import sh.okx.rankup.messages.Message;
import sh.okx.rankup.ranks.Rank;
import sh.okx.rankup.ranks.Rankups;

@RequiredArgsConstructor
public class RanksCommand implements CommandExecutor {
  private final RankupPlugin plugin;

  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    if (plugin.error(sender)) {
      return true;
    }

    Rankups rankups = plugin.getRankups();
    Rank playerRank = null;
    if (sender instanceof Player) {
      playerRank = rankups.getByPlayer((Player) sender);
    }

    plugin.sendHeaderFooter(sender, playerRank, Message.RANKS_HEADER);

    Message message = !(sender instanceof Player && rankups.isLast(plugin.getPermissions(), (Player) sender))
        && playerRank == null ? Message.RANKS_INCOMPLETE : Message.RANKS_COMPLETE;
    Rank rank = rankups.getFirst();
    while (rank != null) {
      String name = rank.getNext();
      if (rank.equals(playerRank)) {
        plugin.getMessage(sender, Message.RANKS_CURRENT, rank, name).send(sender);
        message = Message.RANKS_INCOMPLETE;
      } else {
        plugin.getMessage(sender, message, rank, name).send(sender);
      }
      rank = rankups.getByName(name);
    }
    plugin.sendHeaderFooter(sender, playerRank, Message.RANKS_FOOTER);
    return true;
  }
}

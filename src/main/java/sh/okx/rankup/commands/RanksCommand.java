package sh.okx.rankup.commands;

import lombok.RequiredArgsConstructor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import sh.okx.rankup.RankupPlugin;
import sh.okx.rankup.messages.Message;
import sh.okx.rankup.ranks.Rank;
import sh.okx.rankup.ranks.RankElement;
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
    RankElement<Rank> playerRank = null;
    Rank pRank = null;
    if (sender instanceof Player) {
      playerRank = rankups.getByPlayer((Player) sender);
      pRank = playerRank == null ? null : playerRank.getRank();
    }

    plugin.sendHeaderFooter(sender, pRank, Message.RANKS_HEADER);

    Message message = !(sender instanceof Player && !(playerRank != null && playerRank.hasNext()))
        && playerRank == null ? Message.RANKS_INCOMPLETE : Message.RANKS_COMPLETE;
    RankElement<Rank> rank = rankups.getTree().getFirst();
    while (rank.hasNext()) {
      RankElement<Rank> next = rank.getNext();
      if (rank.getRank().equals(pRank)) {
        plugin.getMessage(sender, Message.RANKS_CURRENT, rank.getRank(), next.getRank()).failIfEmpty().send(sender);
        message = Message.RANKS_INCOMPLETE;
      } else {
        plugin.getMessage(sender, message, rank.getRank(), next.getRank()).failIfEmpty().send(sender);
      }
      rank = next;
    }
    plugin.sendHeaderFooter(sender, pRank, Message.RANKS_FOOTER);
    return true;
  }
}

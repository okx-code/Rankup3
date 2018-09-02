package sh.okx.rankup.commands;

import lombok.RequiredArgsConstructor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import sh.okx.rankup.Rankup;
import sh.okx.rankup.ranks.Rankups;
import sh.okx.rankup.messages.Message;
import sh.okx.rankup.messages.MessageBuilder;
import sh.okx.rankup.messages.Variable;
import sh.okx.rankup.ranks.Rank;

@RequiredArgsConstructor
public class RanksCommand implements CommandExecutor {
  private final Rankup plugin;

  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    Rankups rankups = plugin.getRankups();
    Rank playerRank = null;
    if (sender instanceof Player) {
      playerRank = rankups.getByPlayer((Player) sender);
    }

    sendHeaderFooter(sender, playerRank, Message.RANKS_HEADER);

    Message message = playerRank == null ? Message.RANKS_INCOMPLETE : Message.RANKS_COMPLETE;
    Rank rank = rankups.getFirst();
    do {
      Rank next = rankups.next(rank);
      if (rank.equals(playerRank)) {
        sendMessage(sender, Message.RANKS_CURRENT, rank, next);
        message = Message.RANKS_INCOMPLETE;
      } else {
        sendMessage(sender, message, rank, next);
      }
      rank = next;
    } while (!rank.isLast());

    sendHeaderFooter(sender, playerRank, Message.RANKS_FOOTER);
    return true;
  }

  private void sendHeaderFooter(CommandSender sender, Rank rank, Message type) {
    MessageBuilder builder = plugin.getMessage(rank, type)
        .failIfEmpty();
    if (rank == null) {
      builder.replace(Variable.PLAYER, sender.getName());
    } else {
      builder.replaceRanks(sender, rank);
    }
    builder.send(sender);
  }

  private void sendMessage(CommandSender player, Message message, Rank oldRank, Rank rank) {
    plugin.replaceMoneyRequirements(plugin.getMessage(oldRank, message)
        .replaceRanks(player, oldRank, rank), player, oldRank)
        .send(player);
  }
}

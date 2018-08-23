package sh.okx.rankup.commands;

import lombok.RequiredArgsConstructor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import sh.okx.rankup.Rankup;
import sh.okx.rankup.messages.Message;
import sh.okx.rankup.messages.MessageBuilder;
import sh.okx.rankup.messages.Variable;
import sh.okx.rankup.ranks.Rank;
import sh.okx.rankup.ranks.Rankups;
import sh.okx.rankup.ranks.requirements.Requirement;

@RequiredArgsConstructor
public class RankListCommand implements CommandExecutor {
  private final Rankup plugin;

  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    Rankups rankups = plugin.getRankups();
    Rank playerRank = null;
    if(sender instanceof Player) {
      playerRank = rankups.getRank((Player) sender);
    }

    sendHeaderFooter(sender, playerRank, Message.RANKS_HEADER);

    int state = playerRank == null ? 2 : 0;
    Rank rank = rankups.getFirstRank();
    do {
      Rank next = rankups.nextRank(rank);
      if(rank.equals(playerRank)) {
        sendMessage(sender, 1, rank, next);
        state = 2;
      } else {
        sendMessage(sender, state, rank, next);
      }
      rank = next;
    } while(!rank.isLastRank());

    sendHeaderFooter(sender, playerRank, Message.RANKS_FOOTER);
    return true;
  }

  private void sendHeaderFooter(CommandSender sender, Rank rank, Message type) {
    MessageBuilder builder = plugin.getMessage(rank, type)
        .failIfEmpty();
    if(rank == null) {
      builder.replace(Variable.PLAYER, sender.getName());
    } else {
      builder.replaceAll(sender, rank);
    }
    builder.send(sender);
  }

  private void sendMessage(CommandSender player, int state, Rank oldRank, Rank rank) {
    if(state == 0) {
      replaceRequirements(plugin.getMessage(oldRank, Message.RANKS_COMPLETE)
          .replaceAll(player, oldRank, rank), player, oldRank)
          .send(player);
    } else if(state == 1) {
      replaceRequirements(plugin.getMessage(oldRank, Message.RANKS_CURRENT)
          .replaceAll(player, oldRank, rank), player, oldRank)
          .send(player);
    } else if(state == 2) {
      replaceRequirements(plugin.getMessage(oldRank, Message.RANKS_INCOMPLETE)
          .replaceAll(player, oldRank, rank), player, oldRank)
          .send(player);
    }
  }

  private MessageBuilder replaceRequirements(MessageBuilder builder, CommandSender sender, Rank rank) {
    Requirement money = rank.getRequirement("money");
    Double amount = null;
    if(sender instanceof Player && rank.isInRank((Player) sender)) {
      if(money != null && plugin.getEconomy() != null) {
        amount = money.getRemaining((Player) sender);
      }
      plugin.replaceRequirements((Player) sender, builder, rank);
    } else {
      amount = money.getAmount();
    }
    if(amount != null && plugin.getEconomy() != null) {
      builder.replace(Variable.MONEY_NEEDED, plugin.formatMoney(amount));
      builder.replace(Variable.MONEY, plugin.formatMoney(money.getAmount()));
    }
    return builder;

  }
}

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

import java.text.DecimalFormat;

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
    MessageBuilder builder = plugin.getMessage(type)
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
      replaceCost(plugin.getMessage(oldRank, Message.RANKS_COMPLETE)
          .replaceAll(player, oldRank, rank), player, oldRank)
          .send(player);
    } else if(state == 1) {
      replaceCost(plugin.getMessage(oldRank, Message.RANKS_CURRENT)
          .replaceAll(player, oldRank, rank), player, oldRank)
          .send(player);
    } else if(state == 2) {
      replaceCost(plugin.getMessage(oldRank, Message.RANKS_INCOMPLETE)
          .replaceAll(player, oldRank, rank), player, oldRank)
          .send(player);
    }
  }

  private MessageBuilder replaceCost(MessageBuilder builder, CommandSender sender, Rank rank) {
    Requirement money = rank.getRequirement("money");
    if(money == null || plugin.getEconomy() == null) {
      return builder;
    }
    double amount;
    if(sender instanceof Player && rank.isInRank((Player) sender)) {
      amount = money.getRemaining((Player) sender);
    } else {
      amount = money.getAmount();
    }
    DecimalFormat moneyFormat = plugin.getPlaceholders().getMoneyFormat();
    DecimalFormat percentFormat = plugin.getPlaceholders().getPercentFormat();
    return builder
        .replace(Variable.MONEY_NEEDED, moneyFormat.format(amount))
        .replace(Variable.PERCENT_LEFT, percentFormat.format((amount / money.getAmount()) * 100))
        .replace(Variable.PERCENT_DONE, percentFormat.format((1-(amount / money.getAmount())) * 100))
        .replace(Variable.MONEY, moneyFormat.format(money.getAmount()));
  }
}

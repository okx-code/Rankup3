package sh.okx.rankup.messages;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import sh.okx.rankup.ranks.Rank;

public interface MessageBuilder {
  MessageBuilder replaceKey(String key, Object value);
  MessageBuilder replacePlayer(CommandSender sender);
  MessageBuilder replaceRank(Rank rank);
  MessageBuilder replaceOldRank(Rank rank);
  MessageBuilder replaceSeconds(long seconds, long secondsLeft);

  void send(CommandSender sender);
  void broadcast();

  String toString();

  MessageBuilder failIfEmpty();
  MessageBuilder failIf(boolean b);

  default String toString(Player player) {
    return toString();
  }
}

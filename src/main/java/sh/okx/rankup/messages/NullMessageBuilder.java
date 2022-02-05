package sh.okx.rankup.messages;

import org.bukkit.command.CommandSender;
import sh.okx.rankup.ranks.Rank;

/**
 * A no-op implementation of MessageBuilder
 */
public class NullMessageBuilder implements MessageBuilder {

  @Override
  public MessageBuilder replaceKey(String key, Object value) {
    return this;
  }

  @Override
  public MessageBuilder replacePlayer(CommandSender sender) {
    return this;
  }

  @Override
  public MessageBuilder replaceRank(Rank rank) {
    return this;
  }

  @Override
  public MessageBuilder replaceOldRank(Rank rank) {
    return this;
  }

  @Override
  public MessageBuilder replaceSeconds(long seconds, long secondsLeft) {
    return null;
  }

  @Override
  public void send(CommandSender sender) {

  }

  @Override
  public void broadcast() {

  }

  @Override
  public String toString() {
    return null;
  }

  @Override
  public MessageBuilder failIfEmpty() {
    return this;
  }

  @Override
  public MessageBuilder failIf(boolean b) {
    return this;
  }
}

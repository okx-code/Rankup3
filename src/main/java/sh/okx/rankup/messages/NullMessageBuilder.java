package sh.okx.rankup.messages;

import org.bukkit.command.CommandSender;

/**
 * A no-op implementation of MessageBuilder
 */
public class NullMessageBuilder extends MessageBuilder {
  NullMessageBuilder() {
    super(null);
  }

  @Override
  public MessageBuilder failIf(boolean value) {
    return this;
  }

  @Override
  public MessageBuilder replace(Variable variable, Object value) {
    return this;
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
}

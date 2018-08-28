package sh.okx.rankup.messages;

import org.bukkit.command.CommandSender;

class EmptyMessageBuilder extends MessageBuilder {
  EmptyMessageBuilder() {
    super(null);
  }

  /**
   * what are you doing failing if empty when it has already failed?
   */
  @Override
  public MessageBuilder failIfEmpty() {
    throw new UnsupportedOperationException();
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

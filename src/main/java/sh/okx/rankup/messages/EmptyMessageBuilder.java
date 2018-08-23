package sh.okx.rankup.messages;

import org.bukkit.command.CommandSender;

class EmptyMessageBuilder extends MessageBuilder {
  EmptyMessageBuilder() {
    super(null);
  }

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

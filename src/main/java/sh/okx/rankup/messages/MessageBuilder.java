package sh.okx.rankup.messages;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import sh.okx.rankup.ranks.Rank;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MessageBuilder {
  private String message;

  public MessageBuilder(String message) {
    this.message = message;
  }

  public static MessageBuilder of(ConfigurationSection config, Message message) {
    return new MessageBuilder(ChatColor.translateAlternateColorCodes('&', config.getString(message.getName())));
  }

  public MessageBuilder replace(Variable variable, Object value) {
    return replace(variable.name(), value);
  }

  public MessageBuilder replace(String name, Object value) {
    Pattern pattern = Pattern.compile("\\{" + name + "}", Pattern.CASE_INSENSITIVE);
    Matcher matcher = pattern.matcher(message);
    this.message = matcher.replaceAll(String.valueOf(value));
    return this;
  }

  public MessageBuilder replaceAll(CommandSender player, Rank rank) {
    replace(Variable.PLAYER, player.getName());
    replaceAll(rank);
    return this;
  }

  public MessageBuilder replaceAll(CommandSender player, Rank oldRank, Rank rank) {
    replace(Variable.PLAYER, player.getName());
    replaceAll(oldRank, rank);
    return this;
  }

  public MessageBuilder replaceAll(Rank rank) {
    replace(Variable.RANK, rank.getRank());
    replace(Variable.RANK_NAME, rank.getName());
    return this;
  }

  public MessageBuilder replaceAll(Rank oldRank, Rank rank) {
    replace(Variable.RANK, rank.getRank());
    replace(Variable.RANK_NAME, rank.getName());
    replace(Variable.OLD_RANK, oldRank.getRank());
    replace(Variable.OLD_RANK_NAME, oldRank.getName());
    return this;
  }

  /**
   * Fails the MessageBuilder if the message is empty.
   * if this fails, all subsequent calls to that MessageBuilder will do nothing
   *
   * @return an EmptyMessageBuilder if the message is empty, itself otherwise
   */
  public MessageBuilder failIfEmpty() {
    if (message.isEmpty()) {
      return new EmptyMessageBuilder();
    } else {
      return this;
    }
  }

  public void send(CommandSender sender) {
    sender.sendMessage(message);
  }

  /**
   * Sends the message to all players
   * ie, calls MessageBuilder#send(Player) for all players online, and sends the message in the console.
   */
  public void broadcast() {
    for (Player player : Bukkit.getOnlinePlayers()) {
      send(player);
    }
    send(Bukkit.getConsoleSender());
  }

  @Override
  public String toString() {
    return message;
  }
}

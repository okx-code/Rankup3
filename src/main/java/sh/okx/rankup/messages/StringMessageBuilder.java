package sh.okx.rankup.messages;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import sh.okx.rankup.prestige.Prestige;
import sh.okx.rankup.prestige.Prestiges;
import sh.okx.rankup.ranks.Rank;
import sh.okx.rankup.util.Colour;

public class StringMessageBuilder implements MessageBuilder {

  private String message;

  public StringMessageBuilder(String message) {
    this.message = message;
  }

  public static StringMessageBuilder of(ConfigurationSection config, Message message) {
    return StringMessageBuilder.of(config, message.getName());
  }

  private static StringMessageBuilder of(ConfigurationSection config, String message) {
    String string = config.getString(message);
    Objects.requireNonNull(string, "Configuration message '" + message + "' not found!");
    return new StringMessageBuilder(Colour.translate(string));
  }

  public StringMessageBuilder replace(Variable variable, Object value) {
    return replaceKey(variable.name(), value);
  }

  @Override
  public StringMessageBuilder replaceKey(String name, Object value) {
    if (value == null) {
      return this;
    }
    Pattern pattern = Pattern.compile("\\{" + name + "}", Pattern.CASE_INSENSITIVE);
    Matcher matcher = pattern.matcher(message);
    this.message = matcher.replaceAll(String.valueOf(value));
    return this;
  }

  public StringMessageBuilder replaceFirstPrestige(Rank rank, Prestiges prestiges, String with) {
    if (prestiges != null && prestiges.getFirst().equals(rank)) {
      replace(Variable.OLD_RANK, with);
    }
    return this;
  }

  /**
   * Fails the MessageBuilder if the message is empty. if this fails, all subsequent calls to that
   * MessageBuilder will do nothing
   *
   * @return a NullMessageBuilder if the message is empty, itself otherwise
   */
  public MessageBuilder failIfEmpty() {
    return failIf(message.isEmpty());
  }

  public MessageBuilder failIf(boolean value) {
    if (value) {
      return new NullMessageBuilder();
    } else {
      return this;
    }
  }

  @Override
  public MessageBuilder replacePlayer(CommandSender sender) {
    return replace(Variable.PLAYER, sender.getName());
  }

  @Override
  public MessageBuilder replaceRank(Rank rank) {
    return replace(Variable.RANK, rank.getRank())
        .replace(Variable.RANK_NAME, rank.getDisplayName());
  }

  @Override
  public MessageBuilder replaceOldRank(Rank rank) {
    if (rank instanceof Prestige) {
      Prestige prestige = (Prestige) rank;
      replace(Variable.FROM, prestige.getFrom());
      replace(Variable.TO, prestige.getTo());
    }
    return replace(Variable.OLD_RANK, rank.getRank())
        .replace(Variable.OLD_RANK_NAME, rank.getDisplayName());
  }

  @Override
  public MessageBuilder replaceSeconds(long seconds, long secondsLeft) {
    return replace(Variable.SECONDS, seconds)
        .replace(Variable.SECONDS_LEFT, secondsLeft);
  }

  public void send(CommandSender sender) {
    String msg = message;
    if (sender instanceof Player && Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
      msg = PlaceholderAPI.setPlaceholders((Player) sender, msg);
    }
    sender.sendMessage(msg);
  }

  /**
   * Sends the message to all players ie, calls MessageBuilder#send(Player) for all players online,
   * and sends the message in the console.
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

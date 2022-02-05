package sh.okx.rankup.messages.pebble;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import sh.okx.rankup.RankupPlugin;
import sh.okx.rankup.messages.MessageBuilder;
import sh.okx.rankup.messages.NullMessageBuilder;
import sh.okx.rankup.prestige.Prestige;
import sh.okx.rankup.ranks.Rank;
import sh.okx.rankup.text.TextProcessor;
import sh.okx.rankup.text.TextProcessorBuilder;

public class PebbleMessageBuilder implements MessageBuilder {

  private final RankupPlugin plugin;
  private final String message;
  private final Map<String, Object> context = new HashMap<>();
  private final Map<String, Function<Player, Object>> lastMinuteContext = new HashMap<>();

  public PebbleMessageBuilder(RankupPlugin plugin, String message) {
    this.plugin = plugin;
    this.message = message;
    replaceInitial();
  }

  private void replaceInitial() {
    lastMinuteContext.put("ranks", player -> {
      List<RankContext> ranks = new ArrayList<>();
      for (Rank rank : plugin.getRankups().getTree()) {
        ranks.add(new RankContext(plugin, player, rank));
      }
      return ranks;
    });
  }

  @Override
  public PebbleMessageBuilder replaceKey(String key, Object value) {
    context.put(key, value);
    return this;
  }

  @Override
  public PebbleMessageBuilder replacePlayer(CommandSender sender) {
    context.put("player", sender.getName());
    return this;
  }

  @Override
  public PebbleMessageBuilder replaceRank(Rank rank) {
    Function<Player, Object> fun = player -> new RankContext(plugin, player, rank);
    context.put("next", fun.apply(null)); // for console
    lastMinuteContext.put("next", fun);
    return this;
  }

  @Override
  public PebbleMessageBuilder replaceOldRank(Rank rank) {
    Function<Player, Object> object;
    if (rank instanceof Prestige) {
      object = player -> new PrestigeContext(plugin, player, (Prestige) rank);
    } else {
      object = player -> new RankContext(plugin, player, rank);
    }
    context.put("rank", object.apply(null)); // for console
    lastMinuteContext.put("rank", object);
    return this;
  }

  @Override
  public MessageBuilder replaceSeconds(long seconds, long secondsLeft) {
    context.put("seconds", seconds);
    context.put("seconds_left", secondsLeft);
    return this;
  }

  @Override
  public void send(CommandSender sender) {
    Player player = null;
    if (sender instanceof Player) {
      player = (Player) sender;
    }
    sender.sendMessage(processor(player).process(message));
  }

  @Override
  public void broadcast() {
    for (Player player : Bukkit.getOnlinePlayers()) {
      send(player);
    }
    send(Bukkit.getConsoleSender());
  }

  @Override
  public String toString() {
    return processor(null).process(message);
  }

  @Override
  public String toString(Player player) {
    return processor(player).process(message);
  }

  private TextProcessor processor(Player player) {
    Map<String, Object> context = getContext(player);
    return new TextProcessorBuilder()
        .legacy(context, plugin.getPlaceholders())
        .papi(player)
        .pebble(context, plugin.getPlaceholders())
        .papi(player)
        .colour()
        .create();
  }

  private Map<String, Object> getContext(Player player) {
    Map<String, Object> context = new HashMap<>(this.context);
    if (player != null) {
      for (Map.Entry<String, Function<Player, Object>> lastMinute : lastMinuteContext.entrySet()) {
        context.put(lastMinute.getKey(), lastMinute.getValue().apply(player));
      }
      if (!context.containsKey("player")) {
        context.put("player", player.getName());
      }
    }
    return context;
  }

  @Override
  public MessageBuilder failIfEmpty() {
    return failIf(message.isEmpty());
  }

  @Override
  public MessageBuilder failIf(boolean b) {
    return b ? new NullMessageBuilder() : this;
  }
}

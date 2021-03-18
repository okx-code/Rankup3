package sh.okx.rankup.messages.pebble;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import sh.okx.rankup.RankupPlugin;
import sh.okx.rankup.messages.MessageBuilder;
import sh.okx.rankup.messages.NullMessageBuilder;
import sh.okx.rankup.placeholders.Placeholders;
import sh.okx.rankup.prestige.Prestige;
import sh.okx.rankup.ranks.Rank;
import sh.okx.rankup.text.TextProcessor;
import sh.okx.rankup.text.TextProcessorBuilder;
import sh.okx.rankup.text.pebble.PebbleOptions;

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
    Function<Player, Object> lastMinute = player -> {
      List<RankContext> ranks = new ArrayList<>();
      for (Rank rank : plugin.getRankups().getTree()) {
        ranks.add(new RankContext(plugin, player, rank));
      }
      return ranks;
    };
    lastMinuteContext.put("ranks", lastMinute);
    lastMinuteContext.put("player", HumanEntity::getName);
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
    lastMinuteContext.put("next", player -> new RankContext(plugin, player, rank));
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
    lastMinuteContext.put("rank", object);
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
    PebbleOptions options = getOptions();
    return new TextProcessorBuilder()
        .legacy(context, options)
        .papi(player)
        .pebble(context, options)
        .papi(player)
        .colour()
        .create();
  }

  private Map<String, Object> getContext(Player player) {
    Map<String, Object> context = new HashMap<>(this.context);
    if (player != null) {
      for (Map.Entry<String, Function<Player, Object>> lastMinute : lastMinuteContext.entrySet()) {
        context.putIfAbsent(lastMinute.getKey(), lastMinute.getValue().apply(player));
      }
    }
    return context;
  }

  @Override
  public MessageBuilder failIfEmpty() {
    if (message.isEmpty()) {
      return new NullMessageBuilder();
    } else {
      return this;
    }
  }

  private PebbleOptions getOptions() {
    Placeholders placeholders = plugin.getPlaceholders();
    return new PebbleOptions(placeholders.getMoneyFormat(),
        placeholders.getPercentFormat(),
        placeholders.getSimpleFormat());
  }
}

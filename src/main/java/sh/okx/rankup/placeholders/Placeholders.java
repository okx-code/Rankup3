package sh.okx.rankup.placeholders;

import lombok.Getter;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import sh.okx.rankup.RankupPlugin;

import java.text.DecimalFormat;

public class Placeholders {
  private final RankupPlugin plugin;
  @Getter
  private final DecimalFormat moneyFormat;
  @Getter
  private final DecimalFormat percentFormat;
  @Getter
  private final DecimalFormat simpleFormat;
  @Getter
  private RankupExpansion expansion;
  private boolean registered;

  public Placeholders(RankupPlugin plugin) {
    this.plugin = plugin;
    this.moneyFormat = new DecimalFormat(plugin.getConfig().getString("placeholders.money-format"));
    this.percentFormat = new DecimalFormat(plugin.getConfig().getString("placeholders.percent-format"));
    this.simpleFormat = new DecimalFormat(plugin.getConfig().getString("placeholders.simple-format"));
  }

  public void register() {
    if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
      expansion = new RankupExpansion(plugin, this);
      expansion.register();
      registered = true;
    } else {
      registered = false;
    }
  }

  public void unregister() {
    if (registered) {
      PlaceholderAPI.unregisterPlaceholderHook("rankup");
    }
  }
}

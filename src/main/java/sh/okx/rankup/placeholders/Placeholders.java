package sh.okx.rankup.placeholders;

import lombok.Getter;
import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import sh.okx.rankup.Rankup;
import sh.okx.rankup.prestige.Prestige;
import sh.okx.rankup.prestige.Prestiges;
import sh.okx.rankup.ranks.Rank;
import sh.okx.rankup.ranks.Rankups;
import sh.okx.rankup.requirements.Requirement;

import java.text.DecimalFormat;
import java.util.function.Function;

public class Placeholders {
  private final Rankup plugin;
  private boolean registered;
  @Getter
  private final DecimalFormat moneyFormat;
  @Getter
  private final DecimalFormat percentFormat;
  @Getter
  private final DecimalFormat simpleFormat;

  public Placeholders(Rankup plugin) {
    this.plugin = plugin;
    this.moneyFormat = new DecimalFormat(plugin.getConfig().getString("placeholders.money-format"));
    this.percentFormat = new DecimalFormat(plugin.getConfig().getString("placeholders.percent-format"));
    this.simpleFormat = new DecimalFormat(plugin.getConfig().getString("placeholders.simple-format"));
  }

  public void register() {
    if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
      new RankupExpansion(plugin, this).register();
      registered = true;
    } else {
      registered = false;
    }
  }

  public void unregister() {
    if(registered) {
      PlaceholderAPI.unregisterPlaceholderHook("rankup");
    }
  }
}

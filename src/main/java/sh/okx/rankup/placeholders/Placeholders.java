package sh.okx.rankup.placeholders;

import java.text.DecimalFormat;
import java.util.List;
import lombok.Getter;
import org.bukkit.Bukkit;
import sh.okx.rankup.RankupPlugin;

public class Placeholders {
  private final RankupPlugin plugin;
  @Getter
  private final DecimalFormat moneyFormat;
  @Getter
  private final DecimalFormat percentFormat;
  @Getter
  private final DecimalFormat simpleFormat;
  private final List<String> shortened;
  @Getter
  private RankupExpansion expansion;

  private RankupPlaceholderExpansion papiExpansion;

  public Placeholders(RankupPlugin plugin) {
    this.plugin = plugin;
    this.shortened = plugin.getConfig().getStringList("shorten");
    this.moneyFormat = new DecimalFormat(plugin.getConfig().getString("placeholders.money-format"));
    this.percentFormat = new DecimalFormat(plugin.getConfig().getString("placeholders.percent-format"));
    this.simpleFormat = new DecimalFormat(plugin.getConfig().getString("placeholders.simple-format"));
  }

  public String formatMoney(double money) {
    String suffix = "";

    for (int i = shortened.size(); i > 0; i--) {
      double value = Math.pow(10, 3 * i);
      if (money >= value) {
        money /= value;
        suffix = shortened.get(i - 1);
        break;
      }
    }

    return moneyFormat.format(money) + suffix;
  }


  public void register() {
    expansion = new RankupExpansion(plugin, this);
    if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
      papiExpansion = new RankupPlaceholderExpansion(plugin, expansion);
      papiExpansion.register();
    }
  }

  public void unregister() {
    if (papiExpansion != null) {
      papiExpansion.unregister();
    }
  }
}

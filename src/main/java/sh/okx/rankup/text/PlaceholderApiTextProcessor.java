package sh.okx.rankup.text;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class PlaceholderApiTextProcessor implements TextProcessor {

  private final Player player;

  public PlaceholderApiTextProcessor(Player player) {
    this.player = player;
  }

  @Override
  public String process(String string) {
    if (player == null || !Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
      return string;
    } else {
      return PlaceholderAPI.setPlaceholders(player, string);
    }
  }
}

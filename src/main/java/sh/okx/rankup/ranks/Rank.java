package sh.okx.rankup.ranks;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import sh.okx.rankup.RankupPlugin;
import sh.okx.rankup.messages.MessageBuilder;
import sh.okx.rankup.ranks.requirements.RankRequirements;
import sh.okx.rankup.requirements.Requirement;

import java.util.List;

@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class Rank {
  @Getter
  protected final ConfigurationSection section;
  protected final RankupPlugin plugin;
  @Getter
  protected final String next;
  @Getter
  protected final String rank;
  @Getter
  protected final RankRequirements requirements;
  protected final List<String> commands;

  public boolean isIn(Player player) {
    return plugin.getPermissions().inGroup(player.getUniqueId(), rank);
  }

  public boolean hasRequirements(Player player) {
    return requirements.hasRequirements(player);
  }

  public Requirement getRequirement(Player player, String name) {
    return requirements.getRequirement(player, name);
  }

  public void applyRequirements(Player player) {
    requirements.applyRequirements(player);
  }


  public void runCommands(Player player) {
    for (String command : commands) {
      String string = new MessageBuilder(command).replaceRanks(player, this, next).toString();
      if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
        string = PlaceholderAPI.setPlaceholders(player, string);
      }
      Bukkit.dispatchCommand(Bukkit.getConsoleSender(), string);
    }
  }

  @Override
  public String toString() {
    return "Rank{" +
        "next='" + next + '\'' +
        ", rank='" + rank + '\'' +
        ", commands=" + commands +
        '}';
  }
}

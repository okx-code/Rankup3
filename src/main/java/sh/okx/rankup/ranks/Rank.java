package sh.okx.rankup.ranks;

import java.util.List;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import sh.okx.rankup.RankupPlugin;
import sh.okx.rankup.ranks.requirements.RankRequirements;
import sh.okx.rankup.requirements.Requirement;

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
  protected final String displayName;
  @Getter
  protected final RankRequirements requirements;
  @Getter
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

  public void runCommands(Player player, Rank next) {
    for (String command : commands) {
      String string = plugin.newMessageBuilder(command).replacePlayer(player).replaceOldRank(this).replaceRank(next).toString(player);
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

package sh.okx.rankup.prestige;

import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import sh.okx.rankup.RankupPlugin;
import sh.okx.rankup.messages.pebble.PebbleMessageBuilder;
import sh.okx.rankup.ranks.Rank;
import sh.okx.rankup.ranks.requirements.ListRankRequirements;
import sh.okx.rankup.ranks.requirements.RankRequirements;
import sh.okx.rankup.requirements.Requirement;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Prestige extends Rank {
  @Getter
  private final String from;
  @Getter
  private final String to;

  protected Prestige(ConfigurationSection section, RankupPlugin plugin, String next, String rank, RankRequirements requirements, List<String> commands, String from, String to) {
    super(section, plugin, next, rank, rank, requirements, commands);
    this.from = from;
    this.to = to;
  }

  public static Prestige deserialize(RankupPlugin plugin, ConfigurationSection section) {
    List<String> requirementsList = section.getStringList("requirements");
    List<Requirement> requirements = plugin.getRequirements().getRequirements(requirementsList);

    return new Prestige(section, plugin,
        section.getString("next"),
        section.getString("rank"),
        new ListRankRequirements(requirements),
        section.getStringList("commands"),
        section.getString("from"),
        section.getString("to"));
  }

  @Override
  public void runCommands(Player player, Rank next) {
    for (String command : commands) {
      String string = new PebbleMessageBuilder(this.plugin, command)
          .replacePlayer(player)
          .replaceOldRank(this)
          .replaceRank(next)
          .toString();
      if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
        string = PlaceholderAPI.setPlaceholders(player, string);
      }
      Bukkit.dispatchCommand(Bukkit.getConsoleSender(), string);
    }
  }

  @Override
  public boolean isIn(Player player) {
    // first prestige does not have a rank
    boolean inFrom = plugin.getPermissions().inGroup(player.getUniqueId(), from);
    if (rank == null && inFrom) {
      // not in any other prestiges
      for (Prestige prestige : plugin.getPrestiges().getTree()) {
        if (prestige != this && prestige.isIn(player)) {
          return false;
        }
      }
      return true;
    }

    if (rank == null) {
      return false;
    }

    // subsequent prestiges
    boolean inRank = plugin.getPermissions().inGroup(player.getUniqueId(), rank);
    if (inRank) {
      return true;
    }

    return false;
  }

  public boolean isEligible(Player player) {
    return plugin.getPermissions().inGroup(player.getUniqueId(), from);
  }
}

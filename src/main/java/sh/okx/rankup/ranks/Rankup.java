package sh.okx.rankup.ranks;

import java.util.List;
import org.bukkit.configuration.ConfigurationSection;
import sh.okx.rankup.RankupPlugin;
import sh.okx.rankup.ranks.requirements.RankRequirements;
import sh.okx.rankup.ranks.requirements.RankRequirementsFactory;

public class Rankup extends Rank {
  public static Rank deserialize(RankupPlugin plugin, ConfigurationSection section) {
    String next = section.getString("next");
    String rank = section.getString("rank");

    if (next != null && next.isEmpty()) {
      plugin.getLogger().warning("Rankup section '" + section.getName() + "' has a blank 'next' field, will be ignored.");
      return null;
    }

    return new Rank(section, plugin,
        next,
        rank,
        RankRequirementsFactory.getRequirements(plugin, section),
        section.getStringList("commands"));
  }

  protected Rankup(ConfigurationSection section,
      RankupPlugin plugin, String next, String rank,
      RankRequirements requirements,
      List<String> commands) {
    super(section, plugin, next, rank, requirements, commands);
  }
}

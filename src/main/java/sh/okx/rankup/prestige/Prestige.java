package sh.okx.rankup.prestige;

import lombok.Getter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import sh.okx.rankup.Rankup;
import sh.okx.rankup.ranks.Rank;
import sh.okx.rankup.requirements.Operation;
import sh.okx.rankup.requirements.Requirement;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Prestige extends Rank {
  @Getter
  private final String from;
  @Getter
  private final String to;

  private Prestige(Rankup plugin, String name, String next, String rank, Set<Requirement> requirements, Operation operation, List<String> commands, String from, String to) {
    super(plugin, name, next, rank, requirements, operation, commands);
    this.from = from;
    this.to = to;
  }

  public static Prestige deserialize(Rankup plugin, ConfigurationSection section) {
    Set<Requirement> requirements = new HashSet<>();
    Operation operation = null;
    ConfigurationSection requirementsSection = section.getConfigurationSection("requirements");

    if (requirementsSection != null) {
      requirements = plugin.getRequirementRegistry().getRequirements(requirementsSection);
      operation = plugin.getOperationRegistry().getOperation(section.getString("operation"));
    }

    return new Prestige(plugin,
        section.getName(),
        section.getString("next"),
        section.getString("rank"),
        requirements,
        operation,
        section.getStringList("commands"),
        section.getString("from"),
        section.getString("to"));
  }

  public boolean isEligable(Player player) {
    String[] groups = plugin.getPermissions().getPlayerGroups(null, player);
    for (String group : groups) {
      if (group.equalsIgnoreCase(from)) {
        return true;
      }
    }
    return false;
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof Prestige)) {
      return false;
    }
    return ((Prestige) o).name.equals(name);
  }
}

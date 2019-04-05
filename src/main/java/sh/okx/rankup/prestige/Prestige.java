package sh.okx.rankup.prestige;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.apache.commons.lang3.Validate;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import sh.okx.rankup.Rankup;
import sh.okx.rankup.ranks.Rank;
import sh.okx.rankup.requirements.Requirement;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
public class Prestige extends Rank {
  @Getter
  private final String from;
  @Getter
  private final String to;

  private Prestige(ConfigurationSection section, Rankup plugin, String next, String rank, Set<Requirement> requirements, List<String> commands, String from, String to) {
    super(section, plugin, next, rank, requirements, commands);
    this.from = from;
    this.to = to;
  }

  public static Prestige deserialize(Rankup plugin, ConfigurationSection section) {
    List<String> requirementsList = section.getStringList("requirements");
    Set<Requirement> requirements = plugin.getRequirementRegistry().getRequirements(requirementsList);

    return new Prestige(section, plugin,
        section.getString("next"),
        section.getString("rank"),
        requirements,
        section.getStringList("commands"),
        section.getString("from"),
        section.getString("to"));
  }

  @Override
  public boolean isIn(Player player) {
    String[] groups = plugin.getPermissions().getPlayerGroups(null, player);
    for (String group : groups) {
      if (group.equalsIgnoreCase(from) && rank == null) {
        for (Prestige prestige : plugin.getPrestiges().getOrderedList()) {
          if (prestige != this && prestige.isIn(player)) {
            return false;
          }
        }
        return true;
      } else if(group.equalsIgnoreCase(rank)) {
        return true;
      }
    }
    return false;
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
  public boolean isLast() {
    return plugin.getPrestiges().getByName(next) == null;
  }
}

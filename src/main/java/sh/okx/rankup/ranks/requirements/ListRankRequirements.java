package sh.okx.rankup.ranks.requirements;

import java.util.Set;
import org.bukkit.entity.Player;
import sh.okx.rankup.requirements.DeductibleRequirement;
import sh.okx.rankup.requirements.Requirement;

public class ListRankRequirements implements RankRequirements {
  private final Set<Requirement> requirements;

  public ListRankRequirements(Set<Requirement> requirements) {
    this.requirements = requirements;
  }

  @Override
  public Set<Requirement> getRequirements(Player player) {
    return requirements;
  }

  @Override
  public boolean hasRequirements(Player player) {
    for (Requirement requirement : requirements) {
      if (!requirement.check(player)) {
        return false;
      }
    }
    return true;
  }

  @Override
  public Requirement getRequirement(Player player, String name) {
    for (Requirement requirement : requirements) {
      if (requirement.getFullName().equalsIgnoreCase(name)) {
        return requirement;
      }
    }
    return null;
  }

  @Override
  public void applyRequirements(Player player) {
    for (Requirement requirement : requirements) {
      if (requirement instanceof DeductibleRequirement) {
        ((DeductibleRequirement) requirement).apply(player);
      }
    }
  }
}

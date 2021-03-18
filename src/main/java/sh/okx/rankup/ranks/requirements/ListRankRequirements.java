package sh.okx.rankup.ranks.requirements;

import java.util.List;
import org.bukkit.entity.Player;
import sh.okx.rankup.requirements.DeductibleRequirement;
import sh.okx.rankup.requirements.Requirement;

public class ListRankRequirements implements RankRequirements {
  private final List<Requirement> requirements;

  public ListRankRequirements(List<Requirement> requirements) {
    this.requirements = requirements;
  }

  @Override
  public Iterable<Requirement> getRequirements(Player player) {
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

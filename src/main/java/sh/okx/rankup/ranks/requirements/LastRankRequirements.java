package sh.okx.rankup.ranks.requirements;

import java.util.Collections;
import org.bukkit.entity.Player;
import sh.okx.rankup.requirements.Requirement;

public class LastRankRequirements implements RankRequirements {

  @Override
  public Iterable<Requirement> getRequirements(Player player) {
    return Collections.emptyList();
  }

  @Override
  public boolean hasRequirements(Player player) {
    return false;
  }

  @Override
  public Requirement getRequirement(Player player, String name) {
    return null;
  }

  @Override
  public void applyRequirements(Player player) {

  }
}

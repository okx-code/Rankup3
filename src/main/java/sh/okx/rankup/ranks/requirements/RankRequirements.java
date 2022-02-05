package sh.okx.rankup.ranks.requirements;

import org.bukkit.entity.Player;
import sh.okx.rankup.requirements.Requirement;

public interface RankRequirements {
  Iterable<Requirement> getRequirements(Player player);

  boolean hasRequirements(Player player);
  Requirement getRequirement(Player player, String name);
  void applyRequirements(Player player);
}

package sh.okx.rankup.requirements.requirement;

import org.bukkit.Statistic;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import sh.okx.rankup.RankupPlugin;
import sh.okx.rankup.requirements.ProgressiveRequirement;
import sh.okx.rankup.requirements.Requirement;

import java.util.Objects;

public class MobKillsRequirement extends ProgressiveRequirement {
  public MobKillsRequirement(RankupPlugin plugin) {
    super(plugin, "mob-kills", true);
  }

  protected MobKillsRequirement(Requirement clone) {
    super(clone);
  }

  @SuppressWarnings("deprecation")
  @Override
  public double getProgress(Player player) {
    EntityType entity = EntityType.fromName(getSub());
    if (entity == null) {
      EntityType entityFromId;
      try {
        entityFromId = EntityType.valueOf(getSub().toUpperCase());
      } catch (IllegalArgumentException e) {
        entityFromId = null;
      }
      entity = Objects.requireNonNull(entityFromId, "Invalid entity type '" + getSub() + "' in mob-kills requirement.");
    }
    return player.getStatistic(Statistic.KILL_ENTITY, entity);
  }

  @Override
  public Requirement clone() {
    return new MobKillsRequirement(this);
  }
}

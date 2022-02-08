package sh.okx.rankup.requirements.requirement;

import org.bukkit.Statistic;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import sh.okx.rankup.RankupPlugin;
import sh.okx.rankup.requirements.ProgressiveRequirement;
import sh.okx.rankup.requirements.Requirement;

import java.util.Objects;

public class MobKillsRequirement extends ProgressiveRequirement {
  public MobKillsRequirement(RankupPlugin plugin, String name) {
    super(plugin, name, true);
  }

  protected MobKillsRequirement(Requirement clone) {
    super(clone);
  }

  @SuppressWarnings("deprecation")
  @Override
  public double getProgress(Player player) {
    EntityType entity = Objects.requireNonNull(EntityType.fromName(getSub()), "Invalid entity type '" + getSub() + "' in mob-kills requirement.");
    return player.getStatistic(Statistic.KILL_ENTITY, entity);
  }

  @Override
  public Requirement clone() {
    return new MobKillsRequirement(this);
  }
}

package sh.okx.rankup.requirements.requirement;

import org.bukkit.Statistic;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import sh.okx.rankup.Rankup;
import sh.okx.rankup.requirements.ProgressiveRequirement;
import sh.okx.rankup.requirements.Requirement;

public class MobKillsRequirement extends ProgressiveRequirement {
  public MobKillsRequirement(Rankup plugin) {
    super(plugin, "mob-kills", true);
  }

  protected MobKillsRequirement(Requirement clone) {
    super(clone);
  }

  @Override
  public double getProgress(Player player) {
    return player.getStatistic(Statistic.KILL_ENTITY, EntityType.fromName(getSub()));
  }

  @Override
  public Requirement clone() {
    return new MobKillsRequirement(this);
  }
}

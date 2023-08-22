package sh.okx.rankup.requirements.requirement;

import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import sh.okx.rankup.RankupPlugin;
import sh.okx.rankup.requirements.ProgressiveRequirement;
import sh.okx.rankup.requirements.Requirement;

public class TotalMobKillsRequirement extends ProgressiveRequirement {
  public TotalMobKillsRequirement(RankupPlugin plugin, String name) {
    super(plugin, name);
  }

  private TotalMobKillsRequirement(Requirement clone) {
    super(clone);
  }

  @Override
  public double getProgress(Player player) {
    return player.getStatistic(Statistic.MOB_KILLS);
  }

  @Override
  public Requirement clone() {
    return new TotalMobKillsRequirement(this);
  }
}

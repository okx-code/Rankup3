package sh.okx.rankup.requirements.requirement;

import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import sh.okx.rankup.RankupPlugin;
import sh.okx.rankup.requirements.ProgressiveRequirement;
import sh.okx.rankup.requirements.Requirement;

public class PlayerKillsRequirement extends ProgressiveRequirement {
  public PlayerKillsRequirement(RankupPlugin plugin, String name) {
    super(plugin, name);
  }

  protected PlayerKillsRequirement(Requirement clone) {
    super(clone);
  }

  @Override
  public double getProgress(Player player) {
    return player.getStatistic(Statistic.PLAYER_KILLS);
  }

  @Override
  public Requirement clone() {
    return new PlayerKillsRequirement(this);
  }
}

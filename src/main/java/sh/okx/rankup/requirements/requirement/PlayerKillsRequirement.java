package sh.okx.rankup.requirements.requirement;

import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import sh.okx.rankup.Rankup;
import sh.okx.rankup.requirements.ProgressiveRequirement;
import sh.okx.rankup.requirements.Requirement;

public class PlayerKillsRequirement extends ProgressiveRequirement {
  public PlayerKillsRequirement(Rankup plugin) {
    super(plugin, "player-kills");
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

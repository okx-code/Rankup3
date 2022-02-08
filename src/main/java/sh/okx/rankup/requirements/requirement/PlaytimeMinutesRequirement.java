package sh.okx.rankup.requirements.requirement;

import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import sh.okx.rankup.RankupPlugin;
import sh.okx.rankup.requirements.ProgressiveRequirement;
import sh.okx.rankup.requirements.Requirement;

public class PlaytimeMinutesRequirement extends ProgressiveRequirement {
  private static final int TICKS_PER_MINUTE = 20 * 60;
  private Statistic playOneTick;

  public PlaytimeMinutesRequirement(RankupPlugin plugin, String name) {
    super(plugin, name);
    try {
      playOneTick = Statistic.valueOf("PLAY_ONE_MINUTE");
    } catch (IllegalArgumentException e) {
      // statistic was changed in 1.13.
      playOneTick = Statistic.valueOf("PLAY_ONE_TICK");
    }
  }

  protected PlaytimeMinutesRequirement(PlaytimeMinutesRequirement clone) {
    super(clone);
    this.playOneTick = clone.playOneTick;
  }

  @Override
  public double getProgress(Player player) {
    return player.getStatistic(playOneTick) / TICKS_PER_MINUTE;
  }

  @Override
  public Requirement clone() {
    return new PlaytimeMinutesRequirement(this);
  }
}

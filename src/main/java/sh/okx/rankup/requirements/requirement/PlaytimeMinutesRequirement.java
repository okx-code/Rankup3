package sh.okx.rankup.requirements.requirement;

import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import sh.okx.rankup.Rankup;
import sh.okx.rankup.requirements.Requirement;

public class PlaytimeMinutesRequirement extends Requirement {
  private static final int TICKS_PER_MINUTE = 20 * 60;
  private Statistic playOneTick;

  public PlaytimeMinutesRequirement(Rankup plugin) {
    super(plugin, "playtime-minutes");
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
  public boolean check(Player player) {
    return getRemaining(player) <= 0;
  }

  @Override
  public double getRemaining(Player player) {
    return Math.max(0, getValueDouble() - (player.getStatistic(playOneTick) / TICKS_PER_MINUTE));
  }

  @Override
  public Requirement clone() {
    return new PlaytimeMinutesRequirement(this);
  }
}

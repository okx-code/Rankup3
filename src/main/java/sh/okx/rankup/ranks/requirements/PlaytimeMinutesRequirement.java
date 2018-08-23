package sh.okx.rankup.ranks.requirements;

import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import sh.okx.rankup.Rankup;

public class PlaytimeMinutesRequirement extends Requirement {
  private static final int TICKS_PER_MINUTE = 20 * 60;
  private Statistic playOneTick;

  public PlaytimeMinutesRequirement(Rankup plugin, String name) {
    super(plugin, name);
    try {
      playOneTick = Statistic.valueOf("PLAY_ONE_MINUTE");
    } catch(IllegalArgumentException e) {
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
    return player.getStatistic(playOneTick) * TICKS_PER_MINUTE >= amount;
  }

  @Override
  public void apply(Player player) {
    // well, we can't really take hours of playtime away, can we?
  }

  @Override
  public double getRemaining(Player player) {
    return amount - (player.getStatistic(playOneTick) * TICKS_PER_MINUTE);
  }

  @Override
  public Requirement clone() {
    return new PlaytimeMinutesRequirement(this);
  }
}

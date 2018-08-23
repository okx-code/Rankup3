package sh.okx.rankup.ranks.requirements;

import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import sh.okx.rankup.Rankup;

public class PlaytimeHoursRequirement extends Requirement {
  private static final int TICKS_PER_HOUR = 20 * 60 * 60;

  public PlaytimeHoursRequirement(Rankup plugin, String name) {
    super(plugin, name);
  }

  protected PlaytimeHoursRequirement(Requirement clone) {
    super(clone);
  }

  @Override
  public boolean check(Player player) {
    return player.getStatistic(Statistic.PLAY_ONE_MINUTE) * TICKS_PER_HOUR >= amount;
  }

  @Override
  public void apply(Player player) {
    // well, we can't really take hours of playtime away, can we?
  }

  @Override
  public double getRemaining(Player player) {
    return amount - (player.getStatistic(Statistic.PLAY_ONE_MINUTE) * TICKS_PER_HOUR);
  }

  @Override
  public Requirement clone() {
    return new PlaytimeHoursRequirement(this);
  }
}

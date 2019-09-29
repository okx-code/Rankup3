package sh.okx.rankup.requirements;

import org.bukkit.entity.Player;
import sh.okx.rankup.Rankup;

public interface DeductibleRequirement {
  /**
   * Apply the effect of this requirement to the player.
   * For money, this could be taking money away from the player.
   * You can assume that <code>Requirement#check(Player)</code> has been called,
   * and has returned true immediately prior to this.
   *
   * @param player the player to take from
   * @param multiplier The multiplier for the value
   */
  void apply(Player player, double multiplier);

  default void apply(Player player) {
    apply(player, 1);
  }
}

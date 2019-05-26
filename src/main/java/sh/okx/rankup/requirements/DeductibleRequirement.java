package sh.okx.rankup.requirements;

import org.bukkit.entity.Player;
import sh.okx.rankup.Rankup;

public abstract class DeductibleRequirement extends ProgressiveRequirement {
  public DeductibleRequirement(Rankup plugin, String name) {
    super(plugin, name);
  }

  public DeductibleRequirement(Rankup plugin, String name, boolean subRequirement) {
    super(plugin, name, subRequirement);
  }

  protected DeductibleRequirement(Requirement clone) {
    super(clone);
  }

  /**
   * Apply the effect of this requirement to the player.
   * For money, this could be taking money away from the player.
   * You can assume that <code>Requirement#check(Player)</code> has been called,
   * and has returned true immediately prior to this.
   *
   * @param player the player to take from
   * @param multiplier The multiplier for the value
   */
  public abstract void apply(Player player, double multiplier);

  public final void apply(Player player) {
    apply(player, 1);
  }
}

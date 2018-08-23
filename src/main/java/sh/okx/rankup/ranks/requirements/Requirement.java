package sh.okx.rankup.ranks.requirements;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import sh.okx.rankup.Rankup;

public abstract class Requirement implements Cloneable {
  protected Rankup plugin;
  @Getter
  protected String name;
  @Getter
  @Setter
  protected double amount;

  public Requirement(Rankup plugin, String name) {
    this.plugin = plugin;
    this.name = name;
  }

  protected Requirement(Requirement clone) {
    if(clone != null) {
      this.plugin = clone.plugin;
      this.name = clone.name;
      this.amount = clone.amount;
    }
  }

  /**
   * Check if a player meets this requirement
   * @param player the player to check
   * @return true if they meet the requirement, false otherwise
   */
  public abstract boolean check(Player player);

  /**
   * Apply the effect of this requirement to the player.
   * For money, this could be taking money away from the player.
   * You can assume that <code>Requirement#check(Player)</code> has been called,
   * and has returned true immediately prior to this.
   * @param player the player to take from
   */
  public abstract void apply(Player player);

  /**
   * Get the remaining amount needed for <code>Requirement#check(Player)</code> to yield true.
   * This is not required and is only used in placeholders.
   * @param player the player to find the remaining amount of
   * @return the remaining amount needed. Should be non-negative.
   */
  public double getRemaining(Player player) {
    return amount;
  }
  public abstract Requirement clone();
}

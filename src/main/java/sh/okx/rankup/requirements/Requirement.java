package sh.okx.rankup.requirements;

import lombok.Getter;
import org.bukkit.entity.Player;
import sh.okx.rankup.RankupPlugin;

public abstract class Requirement implements Cloneable {
  protected final RankupPlugin plugin;
  @Getter
  protected final String name;
  private String value;
  @Getter
  private String sub;
  private boolean subRequirement;

  public Requirement(RankupPlugin plugin, String name) {
    this(plugin, name, false);
  }

  public Requirement(RankupPlugin plugin, String name, boolean subRequirement) {
    this.plugin = plugin;
    this.name = name;
    this.subRequirement = subRequirement;
  }

  protected Requirement(Requirement clone) {
    this.plugin = clone.plugin;
    this.name = clone.name;
    this.value = clone.value;
    this.sub = clone.sub;
    this.subRequirement = clone.subRequirement;
  }

  public void setValue(String value) {
    if (hasSubRequirement()) {
      String[] parts = value.split(" ", 2);
      if (parts.length < 2) {
        throw new IllegalArgumentException("Amount and sub-requirement not present for requirement '" + getName() + "'. You must use the format '" + getName() + " <sub-requirement> <amount>'");
      }

      this.sub = parts[0];
      this.value = parts[1];
    } else {
      this.value = value;
    }
  }

  public String getValueString() {
    return value;
  }

  public String[] getValuesString() {
    return value.split(" ");
  }

  public double getValueDouble() {
    return Double.parseDouble(value);
  }

  public int getValueInt() {
    return Integer.parseInt(value);
  }

  public boolean getValueBoolean() {
    return Boolean.parseBoolean(value);
  }

  public String getFullName() {
    if (hasSubRequirement()) {
      return name + "#" + sub;
    } else {
      return name;
    }
  }

  /**
   * Check if a player meets this requirement
   *
   * @param player the player to check
   * @return true if they meet the requirement, false otherwise
   */
  public abstract boolean check(Player player);

  /**
   * Get the remaining amount needed for <code>Requirement#check(Player)</code> to yield true.
   * This is not required and is only used in placeholders.
   *
   * @param player the player to find the remaining amount of
   * @return the remaining amount needed. Should be non-negative.
   */
  public double getRemaining(Player player) {
    return check(player) ? 0 : 1;
  }

  public final boolean hasSubRequirement() {
    return subRequirement;
  }

  public abstract Requirement clone();

  public double getTotal(Player player) {
    return getValueDouble();
  }
}

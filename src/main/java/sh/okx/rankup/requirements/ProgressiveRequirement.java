package sh.okx.rankup.requirements;

import org.bukkit.entity.Player;
import sh.okx.rankup.RankupPlugin;

public abstract class ProgressiveRequirement extends Requirement {
  public ProgressiveRequirement(RankupPlugin plugin, String name) {
    super(plugin, name);
  }

  public ProgressiveRequirement(RankupPlugin plugin, String name, boolean subRequirement) {
    super(plugin, name, subRequirement);
  }

  protected ProgressiveRequirement(Requirement clone) {
    super(clone);
  }

  @Override
  public boolean check(Player player) {
    return getRemaining(player) <= 0;
  }

  @Override
  public final double getRemaining(Player player) {
    return getRemaining(player, 1);
  }

  public double getRemaining(Player player, double multiplier) {
    return Math.max(0, (multiplier * getTotal(player)) - getProgress(player));
  }

  public abstract double getProgress(Player player);
}

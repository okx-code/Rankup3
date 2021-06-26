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
  public double getRemaining(Player player) {
    return Math.max(0, getTotal(player) - getProgress(player));
  }

  @Override
  public double getTotal(Player player) {
    return getValueDouble();
  }

  public abstract double getProgress(Player player);
}

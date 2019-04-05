package sh.okx.rankup.requirements;

import org.bukkit.entity.Player;
import sh.okx.rankup.Rankup;
import sh.okx.rankup.requirements.Requirement;

public abstract class ProgressiveRequirement extends Requirement {
  public ProgressiveRequirement(Rankup plugin, String name) {
    super(plugin, name);
  }

  protected ProgressiveRequirement(Requirement clone) {
    super(clone);
  }

  @Override
  public boolean check(Player player) {
    return getRemaining(player) == 0;
  }

  @Override
  public double getRemaining(Player player) {
    return Math.max(0, getValueDouble() - getProgress(player));
  }

  public abstract double getProgress(Player player);
}

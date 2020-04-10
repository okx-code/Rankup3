package sh.okx.rankup.requirements.requirement;

import org.bukkit.entity.Player;
import sh.okx.rankup.RankupPlugin;
import sh.okx.rankup.requirements.ProgressiveRequirement;
import sh.okx.rankup.requirements.Requirement;

public class MoneyRequirement extends ProgressiveRequirement {
  public MoneyRequirement(RankupPlugin plugin, String name) {
    super(plugin, name);
  }

  protected MoneyRequirement(MoneyRequirement clone) {
    super(clone);
  }

  @Override
  public double getProgress(Player player) {
    return plugin.getEconomy().getBalance(player);
  }

  @Override
  public Requirement clone() {
    return new MoneyRequirement(this);
  }
}
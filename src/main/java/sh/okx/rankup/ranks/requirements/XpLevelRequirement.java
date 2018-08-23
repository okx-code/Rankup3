package sh.okx.rankup.ranks.requirements;

import org.bukkit.entity.Player;
import sh.okx.rankup.Rankup;

public class XpLevelRequirement extends Requirement {
  public XpLevelRequirement(Rankup plugin, String name) {
    super(plugin, name);
  }

  protected XpLevelRequirement(Requirement clone) {
    super(clone);
  }

  @Override
  public void setAmount(double amount) {
    // experience level should be a whole number
    super.setAmount(Math.round(amount));
  }

  @Override
  public boolean check(Player player) {
    return player.getLevel() >= amount;
  }

  @Override
  public void apply(Player player) {
    player.setLevel(player.getLevel() - (int) amount);
  }

  @Override
  public double getRemaining(Player player) {
    return Math.max(0, amount - player.getLevel());
  }

  @Override
  public Requirement clone() {
    return new XpLevelRequirement(this);
  }
}
package sh.okx.rankup.ranks.requirements;

import org.bukkit.entity.Player;
import sh.okx.rankup.Rankup;

public class XpLevelRequirement extends DeductibleRequirement {
  public XpLevelRequirement(Rankup plugin, String name) {
    super(plugin, name);
  }

  protected XpLevelRequirement(Requirement clone) {
    super(clone);
  }

  @Override
  public boolean check(Player player) {
    return player.getLevel() >= getValueInt();
  }

  @Override
  public void apply(Player player) {
    player.setLevel(player.getLevel() - getValueInt());
  }

  @Override
  public double getRemaining(Player player) {
    return Math.max(0, getValueInt() - player.getLevel());
  }

  @Override
  public Requirement clone() {
    return new XpLevelRequirement(this);
  }
}
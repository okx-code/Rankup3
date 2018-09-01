package sh.okx.rankup.requirements.requirement;

import org.bukkit.entity.Player;
import sh.okx.rankup.Rankup;
import sh.okx.rankup.requirements.DeductibleRequirement;
import sh.okx.rankup.requirements.Requirement;

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
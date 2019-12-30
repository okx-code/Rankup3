package sh.okx.rankup.requirements;

import org.bukkit.entity.Player;
import sh.okx.rankup.Rankup;
import sh.okx.rankup.requirements.requirement.XpLevelRequirement;

public class XpLevelDeductibleRequirement extends XpLevelRequirement implements DeductibleRequirement {

  public XpLevelDeductibleRequirement(Rankup plugin, String name) {
    super(plugin, name);
  }

  private XpLevelDeductibleRequirement(XpLevelDeductibleRequirement clone) {
    super(clone);
  }

  @Override
  public void apply(Player player, double multiplier) {
    player.setLevel(player.getLevel() - (int) Math.round(getValueInt() * multiplier));
  }

  @Override
  public Requirement clone() {
    return new XpLevelDeductibleRequirement(this);
  }
}

package sh.okx.rankup.requirements.requirement;

import org.bukkit.entity.Player;
import sh.okx.rankup.RankupPlugin;
import sh.okx.rankup.requirements.DeductibleRequirement;
import sh.okx.rankup.requirements.Requirement;

public class XpLevelDeductibleRequirement extends XpLevelRequirement implements DeductibleRequirement {

  public XpLevelDeductibleRequirement(RankupPlugin plugin, String name) {
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

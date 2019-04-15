package sh.okx.rankup.requirements.requirement;

import org.bukkit.entity.Player;
import sh.okx.rankup.Rankup;
import sh.okx.rankup.requirements.DeductibleRequirement;
import sh.okx.rankup.requirements.Requirement;

public class XpLevelRequirement extends DeductibleRequirement {
  public XpLevelRequirement(Rankup plugin) {
    super(plugin, "xp-level");
  }

  protected XpLevelRequirement(Requirement clone) {
    super(clone);
  }

  @Override
  public void apply(Player player, double multiplier) {
    player.setLevel(player.getLevel() - (int) Math.round(getValueInt() * multiplier));
  }

  @Override
  public double getProgress(Player player) {
    return player.getLevel();
  }

  @Override
  public Requirement clone() {
    return new XpLevelRequirement(this);
  }
}
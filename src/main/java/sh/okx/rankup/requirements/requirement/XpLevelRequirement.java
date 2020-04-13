package sh.okx.rankup.requirements.requirement;

import org.bukkit.entity.Player;
import sh.okx.rankup.RankupPlugin;
import sh.okx.rankup.requirements.ProgressiveRequirement;
import sh.okx.rankup.requirements.Requirement;

public class XpLevelRequirement extends ProgressiveRequirement {
  public XpLevelRequirement(RankupPlugin plugin, String name) {
    super(plugin, name);
  }

  protected XpLevelRequirement(XpLevelRequirement clone) {
    super(clone);
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
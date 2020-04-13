package sh.okx.rankup.requirements.requirement.mcmmo;

import org.bukkit.entity.Player;
import sh.okx.rankup.RankupPlugin;
import sh.okx.rankup.requirements.Requirement;
import sh.okx.rankup.requirements.ProgressiveRequirement;

public class McMMOSkillRequirement extends ProgressiveRequirement {
  public McMMOSkillRequirement(RankupPlugin plugin) {
    super(plugin, "mcmmo", true);
  }

  protected McMMOSkillRequirement(McMMOSkillRequirement clone) {
    super(clone);
  }

  @Override
  public double getProgress(Player player) {
    return McMMOSkillUtil.getInstance().getSkillLevel(player, getSub());
  }

  @Override
  public Requirement clone() {
    return new McMMOSkillRequirement(this);
  }
}

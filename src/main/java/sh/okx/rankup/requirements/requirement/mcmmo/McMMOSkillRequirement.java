package sh.okx.rankup.requirements.requirement.mcmmo;

import org.bukkit.entity.Player;
import sh.okx.rankup.Rankup;
import sh.okx.rankup.requirements.Requirement;

public class McMMOSkillRequirement extends Requirement {
  private String skill;

  public McMMOSkillRequirement(Rankup plugin, String skill) {
    super(plugin, "mcmmo-" + skill.toLowerCase());
    this.skill = skill;
  }

  protected McMMOSkillRequirement(McMMOSkillRequirement clone) {
    super(clone);
    this.skill = clone.skill;
  }

  @Override
  public boolean check(Player player) {
    return getRemaining(player) <= 0;
  }

  @Override
  public double getRemaining(Player player) {
    return Math.max(0, getValueInt() - McMMOSkillUtil.getInstance().getSkillLevel(player, skill));
  }

  @Override
  public Requirement clone() {
    return new McMMOSkillRequirement(this);
  }
}

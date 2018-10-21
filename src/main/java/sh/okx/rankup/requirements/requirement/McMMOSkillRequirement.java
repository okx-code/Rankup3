package sh.okx.rankup.requirements.requirement;

import com.gmail.nossr50.datatypes.skills.SkillType;
import com.gmail.nossr50.util.player.UserManager;
import org.bukkit.entity.Player;
import sh.okx.rankup.Rankup;
import sh.okx.rankup.requirements.Requirement;

public class McMMOSkillRequirement extends Requirement {
  private SkillType skill;

  public McMMOSkillRequirement(Rankup plugin, SkillType skill) {
    super(plugin, "mcmmo-" + skill.toString().toLowerCase());
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
    return Math.max(0, getValueInt() - UserManager.getPlayer(player).getSkillLevel(skill));
  }

  @Override
  public Requirement clone() {
    return new McMMOSkillRequirement(this);
  }
}

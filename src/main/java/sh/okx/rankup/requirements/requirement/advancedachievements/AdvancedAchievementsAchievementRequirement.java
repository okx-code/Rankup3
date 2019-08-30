package sh.okx.rankup.requirements.requirement.advancedachievements;

import com.hm.achievement.api.AdvancedAchievementsAPI;
import com.hm.achievement.api.AdvancedAchievementsAPIFetcher;
import org.bukkit.entity.Player;
import sh.okx.rankup.Rankup;
import sh.okx.rankup.requirements.Requirement;

public class AdvancedAchievementsAchievementRequirement extends Requirement {
  public AdvancedAchievementsAchievementRequirement(Rankup plugin) {
    super(plugin, "advancedachievements-achievement");
  }

  protected AdvancedAchievementsAchievementRequirement(Requirement clone) {
    super(clone);
  }

  @Override
  public boolean check(Player player) {
    AdvancedAchievementsAPI api = AdvancedAchievementsAPIFetcher.fetchInstance().get();
    return api.hasPlayerReceivedAchievement(player.getUniqueId(), getValueString());
  }

  @Override
  public String getFullName() {
    return super.getFullName() + "#" + getValueString();
  }

  @Override
  public Requirement clone() {
    return new AdvancedAchievementsAchievementRequirement(this);
  }
}

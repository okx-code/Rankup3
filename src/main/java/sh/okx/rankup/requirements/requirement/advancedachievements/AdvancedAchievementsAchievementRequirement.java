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
    return getRemaining(player) < 1;
  }

  @Override
  public double getRemaining(Player player) {
    AdvancedAchievementsAPI api = AdvancedAchievementsAPIFetcher.fetchInstance().get();

    int total = 0;
    String[] achievements = getValueString().split(" ");
    for (String achievement : achievements) {
      if (api.hasPlayerReceivedAchievement(player.getUniqueId(), achievement)) {
        total++;
      }
    }
    return achievements.length - total;
  }

  @Override
  public Requirement clone() {
    return new AdvancedAchievementsAchievementRequirement(this);
  }
}

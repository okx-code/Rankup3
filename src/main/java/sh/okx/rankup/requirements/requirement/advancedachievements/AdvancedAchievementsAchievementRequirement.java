package sh.okx.rankup.requirements.requirement.advancedachievements;

import com.hm.achievement.api.AdvancedAchievementsAPI;
import com.hm.achievement.api.AdvancedAchievementsAPIFetcher;
import org.bukkit.entity.Player;
import sh.okx.rankup.RankupPlugin;
import sh.okx.rankup.requirements.ProgressiveRequirement;

public class AdvancedAchievementsAchievementRequirement extends ProgressiveRequirement
{
  public AdvancedAchievementsAchievementRequirement(RankupPlugin plugin, String name) {
    super(plugin, name);
  }

  protected AdvancedAchievementsAchievementRequirement(ProgressiveRequirement clone) {
    super(clone);
  }

  @Override
  public boolean check(Player player) {
    AdvancedAchievementsAPI api = AdvancedAchievementsAPIFetcher.fetchInstance().get();
    for (String achievement : getValuesString()) {
      if (api.hasPlayerReceivedAchievement(player.getUniqueId(), achievement)) {
        return true;
      }
    }

    return false;
  }

  @Override
  public String getFullName() {
    return super.getFullName() + "#" + getValue();
  }

  @Override
  public double getTotal(Player player) {
    return 1;
  }

  @Override
  public double getProgress(Player player)
  {
    return this.check(player) ? 1 : 0;
  }

  @Override
  public ProgressiveRequirement clone() {
    return new AdvancedAchievementsAchievementRequirement(this);
  }
}

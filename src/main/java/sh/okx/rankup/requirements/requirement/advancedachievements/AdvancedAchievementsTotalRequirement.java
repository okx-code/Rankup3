package sh.okx.rankup.requirements.requirement.advancedachievements;

import com.hm.achievement.api.AdvancedAchievementsAPI;
import com.hm.achievement.api.AdvancedAchievementsAPIFetcher;
import org.bukkit.entity.Player;
import sh.okx.rankup.Rankup;
import sh.okx.rankup.requirements.Requirement;

public class AdvancedAchievementsTotalRequirement extends Requirement {
  public AdvancedAchievementsTotalRequirement(Rankup plugin) {
    super(plugin, "advancedachievements-total");
  }

  protected AdvancedAchievementsTotalRequirement(Requirement clone) {
    super(clone);
  }

  @Override
  public boolean check(Player player) {
    return getRemaining(player) < 1;
  }

  @Override
  public double getRemaining(Player player) {
    AdvancedAchievementsAPI api = AdvancedAchievementsAPIFetcher.fetchInstance().get();
    return getValueInt() - api.getPlayerTotalAchievements(player.getUniqueId());
  }

  @Override
  public Requirement clone() {
    return new AdvancedAchievementsTotalRequirement(this);
  }
}

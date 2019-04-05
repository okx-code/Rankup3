package sh.okx.rankup.requirements.requirement.mcmmo;

import com.gmail.nossr50.util.player.UserManager;
import org.bukkit.entity.Player;
import sh.okx.rankup.Rankup;
import sh.okx.rankup.requirements.Requirement;
import sh.okx.rankup.requirements.ProgressiveRequirement;

public class McMMOPowerLevelRequirement extends ProgressiveRequirement {
  public McMMOPowerLevelRequirement(Rankup plugin) {
    super(plugin, "mcmmo-power-level");
  }

  protected McMMOPowerLevelRequirement(Requirement clone) {
    super(clone);
  }

  @Override
  public double getProgress(Player player) {
    return UserManager.getPlayer(player).getPowerLevel();
  }

  @Override
  public Requirement clone() {
    return new McMMOPowerLevelRequirement(this);
  }
}

package sh.okx.rankup.requirements.requirement.mcmmo;

import com.gmail.nossr50.util.player.UserManager;
import org.bukkit.entity.Player;
import sh.okx.rankup.Rankup;
import sh.okx.rankup.requirements.Requirement;

public class McMMOPowerLevelRequirement extends Requirement {
  public McMMOPowerLevelRequirement(Rankup plugin) {
    super(plugin, "mcmmo-power-level");
  }

  protected McMMOPowerLevelRequirement(Requirement clone) {
    super(clone);
  }

  @Override
  public boolean check(Player player) {
    return getRemaining(player) <= 0;
  }

  @Override
  public double getRemaining(Player player) {
    return Math.max(0, getValueInt() - UserManager.getPlayer(player).getPowerLevel());
  }

  @Override
  public Requirement clone() {
    return new McMMOPowerLevelRequirement(this);
  }
}

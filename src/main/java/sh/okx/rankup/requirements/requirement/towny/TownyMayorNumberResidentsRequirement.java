package sh.okx.rankup.requirements.requirement.towny;

import org.bukkit.entity.Player;
import sh.okx.rankup.RankupPlugin;
import sh.okx.rankup.requirements.ProgressiveRequirement;
import sh.okx.rankup.requirements.Requirement;

public class TownyMayorNumberResidentsRequirement extends ProgressiveRequirement {
  public TownyMayorNumberResidentsRequirement(RankupPlugin plugin, String name) {
    super(plugin, name);
  }

  protected TownyMayorNumberResidentsRequirement(Requirement clone) {
    super(clone);
  }

  @Override
  public double getProgress(Player player) {
    if (TownyUtils.getInstance().isMayor(player)) {
      return TownyUtils.getInstance().getTown(player).getNumResidents();
    } else {
      return 0;
    }
  }

  @Override
  public Requirement clone() {
    return new TownyMayorNumberResidentsRequirement(this);
  }
}

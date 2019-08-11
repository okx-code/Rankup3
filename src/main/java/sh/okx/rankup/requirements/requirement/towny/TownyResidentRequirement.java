package sh.okx.rankup.requirements.requirement.towny;

import org.bukkit.entity.Player;
import sh.okx.rankup.Rankup;
import sh.okx.rankup.requirements.Requirement;

public class TownyResidentRequirement extends Requirement {
  public TownyResidentRequirement(Rankup plugin) {
    super(plugin, "towny-resident");
  }

  public TownyResidentRequirement(Rankup plugin, String name) {
    super(plugin, name);
  }

  protected TownyResidentRequirement(Requirement clone) {
    super(clone);
  }

  @Override
  public boolean check(Player player) {
    return  TownyUtils.getInstance().isResident(player);
  }

  @Override
  public Requirement clone() {
    return new TownyResidentRequirement(this);
  }
}

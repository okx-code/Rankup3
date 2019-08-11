package sh.okx.rankup.requirements.requirement.towny;

import org.bukkit.entity.Player;
import sh.okx.rankup.Rankup;
import sh.okx.rankup.requirements.Requirement;

public class TownyMayorRequirement extends Requirement {
  public TownyMayorRequirement(Rankup plugin) {
    super(plugin, "towny-mayor");
  }

  protected TownyMayorRequirement(Requirement clone) {
    super(clone);
  }

  @Override
  public boolean check(Player player) {
    return TownyUtils.getInstance().isMayor(player) == getValueBoolean();
  }

  @Override
  public Requirement clone() {
    return new TownyMayorRequirement(this);
  }
}

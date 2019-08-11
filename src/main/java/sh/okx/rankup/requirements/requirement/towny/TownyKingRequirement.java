package sh.okx.rankup.requirements.requirement.towny;

import org.bukkit.entity.Player;
import sh.okx.rankup.Rankup;
import sh.okx.rankup.requirements.Requirement;

public class TownyKingRequirement extends Requirement {
  public TownyKingRequirement(Rankup plugin) {
    super(plugin, "towny-king");
  }

  protected TownyKingRequirement(Requirement clone) {
    super(clone);
  }

  @Override
  public boolean check(Player player) {
    return TownyUtils.getInstance().isKing(player) == getValueBoolean();
  }

  @Override
  public Requirement clone() {
    return new TownyKingRequirement(this);
  }
}

package sh.okx.rankup.requirements.requirement.towny;

import org.bukkit.entity.Player;
import sh.okx.rankup.RankupPlugin;
import sh.okx.rankup.requirements.Requirement;

public class TownyKingRequirement extends Requirement {
  public TownyKingRequirement(RankupPlugin plugin, String name) {
    super(plugin, name);
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

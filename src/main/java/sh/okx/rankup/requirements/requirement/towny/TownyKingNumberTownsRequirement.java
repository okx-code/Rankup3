package sh.okx.rankup.requirements.requirement.towny;

import org.bukkit.entity.Player;
import sh.okx.rankup.RankupPlugin;
import sh.okx.rankup.requirements.ProgressiveRequirement;
import sh.okx.rankup.requirements.Requirement;

public class TownyKingNumberTownsRequirement extends ProgressiveRequirement {
  public TownyKingNumberTownsRequirement(RankupPlugin plugin, String name) {
    super(plugin, name);
  }

  protected TownyKingNumberTownsRequirement(TownyKingNumberTownsRequirement clone) {
    super(clone);
  }

  @Override
  public double getProgress(Player player) {
    if (TownyUtils.getInstance().isKing(player)) {
      return TownyUtils.getInstance().getNation(player).getNumTowns();
    } else {
      return 0;
    }
  }

  @Override
  public Requirement clone() {
    return new TownyKingNumberTownsRequirement(this);
  }
}

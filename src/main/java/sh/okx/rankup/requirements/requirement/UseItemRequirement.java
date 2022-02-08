package sh.okx.rankup.requirements.requirement;

import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import sh.okx.rankup.RankupPlugin;
import sh.okx.rankup.requirements.ProgressiveRequirement;
import sh.okx.rankup.requirements.Requirement;

public class UseItemRequirement extends ProgressiveRequirement {
  public UseItemRequirement(RankupPlugin plugin, String name) {
    super(plugin, name, true);
  }

  protected UseItemRequirement(UseItemRequirement clone) {
    super(clone);
  }

  @Override
  public double getProgress(Player player) {
    Material material = Material.matchMaterial(getSub());
    if (material == null) {
      throw new IllegalArgumentException("'" + getSub() + "' is not a valid item");
    }
    return player.getStatistic(Statistic.USE_ITEM, material);
  }

  @Override
  public Requirement clone() {
    return new UseItemRequirement(this);
  }
}

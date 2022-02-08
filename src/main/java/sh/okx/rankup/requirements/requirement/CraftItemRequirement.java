package sh.okx.rankup.requirements.requirement;

import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import sh.okx.rankup.RankupPlugin;
import sh.okx.rankup.requirements.ProgressiveRequirement;
import sh.okx.rankup.requirements.Requirement;

public class CraftItemRequirement extends ProgressiveRequirement {
  public CraftItemRequirement(RankupPlugin plugin, String name) {
    super(plugin, name, true);
  }

  protected CraftItemRequirement(CraftItemRequirement clone) {
    super(clone);
  }

  @Override
  public double getProgress(Player player) {
    Material material = Material.matchMaterial(getSub());
    if (material == null) {
      throw new IllegalArgumentException("'" + getSub() + "' is not a valid item");
    }
    return player.getStatistic(Statistic.CRAFT_ITEM, material);
  }

  @Override
  public Requirement clone() {
    return new CraftItemRequirement(this);
  }
}

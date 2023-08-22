package sh.okx.rankup.requirements.requirement;

import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import sh.okx.rankup.RankupPlugin;
import sh.okx.rankup.requirements.ProgressiveRequirement;
import sh.okx.rankup.requirements.Requirement;

public class BlockBreakRequirement extends ProgressiveRequirement {
  public BlockBreakRequirement(RankupPlugin plugin, String name) {
    super(plugin, name, true);
  }

  protected BlockBreakRequirement(BlockBreakRequirement clone) {
    super(clone);
  }

  @Override
  public double getProgress(Player player) {
    Material material = Material.matchMaterial(getSub());
    if (material == null || !material.isBlock()) {
      throw new IllegalArgumentException("'" + getSub() + "' is not a valid block");
    }
    return player.getStatistic(Statistic.MINE_BLOCK, material);
  }

  @Override
  public Requirement clone() {
    return new BlockBreakRequirement(this);
  }
}

package sh.okx.rankup.requirements.requirement;

import org.bukkit.entity.Player;
import sh.okx.rankup.RankupPlugin;
import sh.okx.rankup.requirements.Requirement;

public class WorldRequirement extends Requirement {
  public WorldRequirement(RankupPlugin plugin, String name) {
    super(plugin, name);
  }

  protected WorldRequirement(Requirement clone) {
    super(clone);
  }

  @Override
  public boolean check(Player player) {
    for (String world : getValuesString()) {
      return player.getWorld().getName().equalsIgnoreCase(world);
    }
    return false;
  }

  @Override
  public double getTotal(Player player) {
    return 1;
  }

  @Override
  public Requirement clone() {
    return new WorldRequirement(this);
  }
}

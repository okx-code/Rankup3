package sh.okx.rankup.requirements.requirement;

import org.bukkit.entity.Player;
import sh.okx.rankup.Rankup;
import sh.okx.rankup.requirements.Requirement;

public class WorldRequirement extends Requirement {
  public WorldRequirement(Rankup plugin) {
    super(plugin, "world");
  }

  protected WorldRequirement(Requirement clone) {
    super(clone);
  }

  @Override
  public boolean check(Player player) {
    String[] worlds = getValuesString();
    for (String world : worlds) {
      return player.getWorld().getName().equalsIgnoreCase(world);
    }
    return false;
  }

  @Override
  public Requirement clone() {
    return new WorldRequirement(this);
  }
}

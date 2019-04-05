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
    return player.getWorld().getName().equalsIgnoreCase(getValueString());
  }

  @Override
  public Requirement clone() {
    return new WorldRequirement(this);
  }
}

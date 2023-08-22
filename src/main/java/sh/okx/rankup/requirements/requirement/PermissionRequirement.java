package sh.okx.rankup.requirements.requirement;

import org.bukkit.entity.Player;
import sh.okx.rankup.RankupPlugin;
import sh.okx.rankup.requirements.Requirement;

public class PermissionRequirement extends Requirement {
  public PermissionRequirement(RankupPlugin plugin, String name) {
    super(plugin, name);
  }

  protected PermissionRequirement(Requirement clone) {
    super(clone);
  }

  @Override
  public boolean check(Player player) {
    for (String permission : getValuesString()) {
      if (player.hasPermission(permission)) {
        return true;
      }
    }
    return false;
  }

  @Override
  public Requirement clone() {
    return new PermissionRequirement(this);
  }
}

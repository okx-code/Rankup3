package sh.okx.rankup.requirements.requirement;

import org.bukkit.entity.Player;
import sh.okx.rankup.Rankup;
import sh.okx.rankup.requirements.Requirement;

public class PermissionRequirement extends Requirement {
  public PermissionRequirement(Rankup plugin) {
    super(plugin, "permission");
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

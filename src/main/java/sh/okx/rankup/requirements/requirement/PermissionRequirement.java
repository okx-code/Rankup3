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
    return getRemaining(player) < 1;
  }

  @Override
  public double getRemaining(Player player) {
    int matched = 0;
    String[] permissions = getValueString().split(" ");
    for (String permission : permissions) {
      if (player.hasPermission(permission)) {
        matched++;
      }
    }
    return permissions.length - matched;
  }

  @Override
  public Requirement clone() {
    return new PermissionRequirement(this);
  }
}

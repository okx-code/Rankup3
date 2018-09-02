package sh.okx.rankup.requirements.requirement;

import org.bukkit.entity.Player;
import sh.okx.rankup.Rankup;
import sh.okx.rankup.requirements.Requirement;

public class GroupRequirement extends Requirement {
  public GroupRequirement(Rankup plugin) {
    super(plugin, "group");
  }

  protected GroupRequirement(Requirement clone) {
    super(clone);
  }

  @Override
  public boolean check(Player player) {
    OUTER:
    for (String requiredGroup : getValueString().split(" ")) {
      for (String group : plugin.getPermissions().getPlayerGroups(player)) {
        if (group.equalsIgnoreCase(requiredGroup)) {
          continue OUTER;
        }
      }
      return false;
    }
    return true;
  }

  @Override
  public Requirement clone() {
    return new GroupRequirement(this);
  }
}

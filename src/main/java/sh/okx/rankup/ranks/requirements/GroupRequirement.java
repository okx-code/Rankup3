package sh.okx.rankup.ranks.requirements;

import org.bukkit.entity.Player;
import sh.okx.rankup.Rankup;

public class GroupRequirement extends Requirement {
  public GroupRequirement(Rankup plugin, String name) {
    super(plugin, name);
  }

  protected GroupRequirement(Requirement clone) {
    super(clone);
  }

  @Override
  public boolean check(Player player) {
    OUTER:
    for(String requiredGroup : getValueString().split(" ")) {
      for(String group : plugin.getPermissions().getPlayerGroups(player)) {
        if(group.equalsIgnoreCase(requiredGroup)) {
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

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
    return getRemaining(player) < 1;
  }

  @Override
  public double getRemaining(Player player) {
    int matched = 0;
    String[] groups = getValueString().split(" ");
    for (String requiredGroup : groups) {
      for (String group : plugin.getPermissions().getPlayerGroups(null, player)) {
        if (group.equalsIgnoreCase(requiredGroup)) {
          matched++;
          break;
        }
      }
    }
    return groups.length - matched;
  }

  @Override
  public Requirement clone() {
    return new GroupRequirement(this);
  }
}

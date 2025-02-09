package sh.okx.rankup.hook;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.context.ContextSet;
import net.luckperms.api.context.ImmutableContextSet;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.types.InheritanceNode;

import java.util.UUID;

public class LuckPermsGroupProvider implements GroupProvider {

  private final LuckPerms luckPerms;
  private final ContextSet contextSet;

  public LuckPermsGroupProvider(LuckPerms luckPerms, ContextSet contextSet) {
    this.luckPerms = luckPerms;
    this.contextSet = contextSet;
  }

  public static LuckPermsGroupProvider createFromString(LuckPerms luckPerms, String context) {
    try {
      ImmutableContextSet.Builder builder = ImmutableContextSet.builder();
      for (String contextPair : context.split(" ")) {
        String[] keyValue = contextPair.split("=", 2);
        if (keyValue.length == 2) {
          builder.add(keyValue[0], keyValue[1]);
        }
      }

      return new LuckPermsGroupProvider(luckPerms, builder.build());
    } catch (NullPointerException | IllegalArgumentException ex) {
      throw new IllegalArgumentException("Context is invalid: " + context, ex);
    }
  }


  @Override
  public boolean inGroup(UUID uuid, String group) {
    User user = luckPerms.getUserManager().getUser(uuid);
    for (Group lpGroup : user.getInheritedGroups(user.getQueryOptions().toBuilder().context(contextSet).build())) {
      if (lpGroup.getName().equalsIgnoreCase(group)) {
        return true;
      }
    }
    return false;
  }

  @Override
  public void transferGroup(UUID uuid, String oldGroup, String group) {
    User user = luckPerms.getUserManager().getUser(uuid);
    if (oldGroup != null) {
      user.data().remove(InheritanceNode.builder(oldGroup).context(contextSet).build());
    }
    user.data().add(InheritanceNode.builder(group).context(contextSet).build());

    luckPerms.getUserManager().saveUser(user);
  }
}

package sh.okx.rankup.hook;

import java.util.UUID;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.context.ContextSet;
import net.luckperms.api.context.ImmutableContextSet;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.types.InheritanceNode;

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
      if (lpGroup.getName().equals(group)) {
        return true;
      }
    }
    return false;
  }

  @Override
  public void addGroup(UUID uuid, String group) {
    User user = luckPerms.getUserManager().getUser(uuid);
    user.data().add(InheritanceNode.builder(group).context(contextSet).build());

    luckPerms.getUserManager().saveUser(user);
  }

  @Override
  public void removeGroup(UUID uuid, String group) {
    User user = luckPerms.getUserManager().getUser(uuid);
    user.data().remove(InheritanceNode.builder(group).context(contextSet).build());

    luckPerms.getUserManager().saveUser(user);
  }
}

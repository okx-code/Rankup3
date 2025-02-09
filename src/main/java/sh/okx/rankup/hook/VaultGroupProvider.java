package sh.okx.rankup.hook;

import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;

import java.util.Objects;
import java.util.UUID;

public class VaultGroupProvider implements GroupProvider {
  private final Permission permission;

  public VaultGroupProvider(Permission permission) {
    this.permission = permission;
  }

  @Override
  public boolean inGroup(UUID uuid, String group) {
    Objects.requireNonNull(uuid);
    Objects.requireNonNull(group);

    String[] playerGroups = permission.getPlayerGroups(null, Bukkit.getOfflinePlayer(uuid));
    for (String playerGroup : playerGroups) {
      if (group.equalsIgnoreCase(playerGroup)) {
        return true;
      }
    }
    return false;
  }

  @Override
  public void transferGroup(UUID uuid, String oldGroup, String group) {
    Objects.requireNonNull(uuid);
    Objects.requireNonNull(group);

    if (oldGroup != null) {
      permission.playerRemoveGroup(null, Bukkit.getOfflinePlayer(uuid), oldGroup);
    }

    permission.playerAddGroup(null, Bukkit.getOfflinePlayer(uuid), group);
  }
}

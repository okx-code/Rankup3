package sh.okx.rankup.hook;

import java.util.Objects;
import java.util.UUID;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;

public class VaultPermissionProvider implements PermissionProvider {
  private final Permission permission;

  public VaultPermissionProvider(Permission permission) {
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
  public void addGroup(UUID uuid, String group) {
    Objects.requireNonNull(uuid);
    Objects.requireNonNull(group);

    permission.playerAddGroup(null, Bukkit.getOfflinePlayer(uuid), group);
  }

  @Override
  public void removeGroup(UUID uuid, String group) {
    Objects.requireNonNull(uuid);
    Objects.requireNonNull(group);

    permission.playerRemoveGroup(null, Bukkit.getOfflinePlayer(uuid), group);
  }
}

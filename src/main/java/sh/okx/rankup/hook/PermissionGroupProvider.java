package sh.okx.rankup.hook;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class PermissionGroupProvider implements GroupProvider {

  @Override
  public boolean inGroup(UUID uuid, String group) {
    Player player = getPlayer(uuid);
    return player.hasPermission("rankup.rank." + group);
  }

  @Override
  public void transferGroup(UUID uuid, String oldGroup, String group) {
    // no-op
  }

  private Player getPlayer(UUID uuid) {
    Player player = Bukkit.getPlayer(uuid);
    if (player == null) {
      throw new IllegalArgumentException("Player not online!");
    }
    return player;
  }
}

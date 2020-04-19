package sh.okx.rankup.hook;

import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class PermissionPermissionProvider implements PermissionProvider {

  @Override
  public boolean inGroup(UUID uuid, String group) {
    Player player = getPlayer(uuid);
    return player.hasPermission("rankup.rank." + group);
  }

  @Override
  public void addGroup(UUID uuid, String group) {
    // no-op
  }

  @Override
  public void removeGroup(UUID uuid, String group) {
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

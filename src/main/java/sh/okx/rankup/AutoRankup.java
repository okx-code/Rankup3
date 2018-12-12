package sh.okx.rankup;

import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

@RequiredArgsConstructor
public class AutoRankup extends BukkitRunnable {
  private final Rankup rankup;

  @Override
  public void run() {
    for (Player player : Bukkit.getOnlinePlayers()) {
      if (player.hasPermission("rankup.auto")) {
        if (rankup.checkRankup(player, false)) {
          rankup.rankup(player);
        } else if (rankup.getPrestiges() != null && rankup.checkPrestige(player, false)) {
          rankup.prestige(player);
        }
      }
    }
  }
}

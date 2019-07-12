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
    RankupHelper helper = rankup.getHelper();
    for (Player player : Bukkit.getOnlinePlayers()) {
      if (player.hasPermission("rankup.auto")) {
        if (helper.checkRankup(player, false)) {
          helper.rankup(player);
        } else if (rankup.getPrestiges() != null && helper.checkPrestige(player, false)) {
          helper.prestige(player);
        }
      }
    }
  }
}

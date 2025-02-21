package sh.okx.rankup;

import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import sh.okx.rankup.util.folia.FoliaScheduler;
import sh.okx.rankup.util.folia.TaskWrapper;

@RequiredArgsConstructor
public class AutoRankup implements Runnable {
  private final RankupPlugin rankup;

  private TaskWrapper task;
  private BukkitTask bukkitTask;

  @Override
  public void run() {
    if (rankup.error()) {
      return;
    }

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

  public void runTaskTimer(RankupPlugin rankupPlugin, long delay, long period) {
      if (FoliaScheduler.isFolia()) {
        task = FoliaScheduler.getAsyncScheduler().runAtFixedRate(rankupPlugin, $ -> this.run(), delay, period);
      } else {
        bukkitTask = new BukkitRunnable() {
          @Override
          public void run() {
            AutoRankup.this.run();
          }
        }.runTaskTimer(rankupPlugin, delay, period);
      }
  }

  public boolean isCancelled() {
    return task == null ? bukkitTask.isCancelled() : task.isCancelled();
  }

  public void cancel() {
    if (task == null) {
      bukkitTask.cancel();
    } else {
      task.cancel();
    }
  }
}

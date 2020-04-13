package sh.okx.rankup;

import java.util.function.Supplier;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import sh.okx.rankup.util.UpdateNotifier;

public class JoinUpdateNotifier implements Listener {
  private final UpdateNotifier notifier;
  private final Supplier<Boolean> enabledSupplier;
  private final String permission;

  public JoinUpdateNotifier(UpdateNotifier notifier,
      Supplier<Boolean> enabledSupplier, String permission) {
    this.notifier = notifier;
    this.enabledSupplier = enabledSupplier;
    this.permission = permission;
  }

  @EventHandler
  public void on(PlayerJoinEvent e) {
    if (enabledSupplier.get()) {
      Player player = e.getPlayer();
      if (player.hasPermission(permission)) {
        notifier.notify(player, true);
      }
    }
  }
}

package sh.okx.rankup.gui;

import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import sh.okx.rankup.RankupPlugin;

@RequiredArgsConstructor
public class GuiListener implements Listener {
  private final RankupPlugin plugin;

  @EventHandler
  public void on(InventoryClickEvent e) {
    Inventory inventory = e.getInventory();
    if (inventory == null
        || !(inventory.getHolder() instanceof Gui)
        || !inventory.equals(e.getClickedInventory())) {
      return;
    }
    e.setCancelled(true);

    Player player = (Player) e.getWhoClicked();
    Gui gui = (Gui) inventory.getHolder();

    if (gui.getRankup().isSimilar(e.getCurrentItem())) {
      Bukkit.getScheduler().runTask(plugin, player::closeInventory);
      if (gui.isPrestige()) {
        plugin.getHelper().prestige(player);
      } else {
        plugin.getHelper().rankup(player);
      }
    } else if (gui.getCancel().isSimilar(e.getCurrentItem())) {
      Bukkit.getScheduler().runTask(plugin, () -> {
        player.closeInventory();
        if (gui.isReturnToRanksGui()) {
          Bukkit.dispatchCommand(player, "ranksgui");
        }
      });
    }
  }
}

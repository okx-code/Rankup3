package sh.okx.rankup.ranksgui;

import java.util.HashMap;
import java.util.Map;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

public class RanksGuiListener implements Listener {

  private final Map<Player, RanksGui> guiMap = new HashMap<>();

  @EventHandler
  public void on(InventoryCloseEvent event) {
    if (!(event.getPlayer() instanceof Player)) {
      return;
    }
    Player player = (Player) event.getPlayer();
    if (guiMap.containsKey(player)) {
      RanksGui ranksGui = guiMap.get(player);
      if (ranksGui.getInventory() != null
          && ranksGui.getInventory() == event.getInventory()) {
        guiMap.remove(player);
      }
    }
  }

  @EventHandler
  public void on(InventoryClickEvent event) {
    if (!(event.getWhoClicked() instanceof Player)) {
      return;
    }
    Player player = (Player) event.getWhoClicked();
    RanksGui ranksGui = guiMap.get(player);
    if (ranksGui != null && event.getInventory() == ranksGui.getInventory()) {
      ranksGui.click(event);
      event.setCancelled(true);
    }
  }

  public void open(RanksGui gui) {
    guiMap.put(gui.getPlayer(), gui);
    gui.open();
  }
}
    
package sh.okx.rankup.gui;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import sh.okx.rankup.Rankup;
import sh.okx.rankup.messages.Message;
import sh.okx.rankup.messages.MessageBuilder;
import sh.okx.rankup.ranks.Rank;

import java.util.Arrays;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Gui implements InventoryHolder {
  @Getter
  private Inventory inventory;
  @Getter
  private ItemStack rankup;
  @Getter
  private ItemStack cancel;

  public static Gui of(Player player, Rank oldRank, Rank rank, Rankup plugin) {
    ConfigurationSection config = plugin.getConfig().getConfigurationSection("gui");
    ItemStack[] items = new ItemStack[config.getInt("rows") * 9];
    addItem(items, config.getConfigurationSection("rankup"), player, oldRank, rank);
    addItem(items, config.getConfigurationSection("cancel"), player, oldRank, rank);
    addItem(items, config.getConfigurationSection("fill"), player, oldRank, rank);

    Gui gui = new Gui();
    gui.rankup = getItem(config.getConfigurationSection("rankup"), player, oldRank, rank);
    gui.cancel = getItem(config.getConfigurationSection("cancel"), player, oldRank, rank);
    Inventory inventory = Bukkit.createInventory(gui,
        items.length,
        plugin.getMessage(rank, Message.TITLE)
            .replaceAll(player, oldRank, rank)
            .toString());
    inventory.setContents(items);
    gui.inventory = inventory;
    return gui;
  }

  private static ItemStack getItem(ConfigurationSection section, Player player, Rank oldRank, Rank rank) {
    boolean legacy = !Bukkit.getVersion().contains("1.13");

    String materialName = section.getString("material").toUpperCase();
    // handle default material correctly on older versions
    if (legacy && materialName.equals("BLACK_STAINED_GLASS_PANE")) {
      materialName = "STAINED_GLASS_PANE:15";
    }

    ItemStack item;
    if (legacy) {
      String[] parts = materialName.split(":");
      Material material = Material.valueOf(parts[0]);

      short type = parts.length > 1 ? Short.parseShort(parts[1]) : 0;
      item = new ItemStack(material, 1, type);
    } else {
      Material material = Material.valueOf(materialName);
      item = new ItemStack(material);
    }

    if(item.getType() == Material.AIR && section.getName().equalsIgnoreCase("fill")) {
      return item;
    }

    ItemMeta meta = item.getItemMeta();
    if (section.contains("lore")) {
      meta.setLore(Arrays.stream(format(section.getString("lore"), player, oldRank, rank).split("\n"))
          .map(string -> ChatColor.RESET + string)
          .collect(Collectors.toList()));
    }
    if (section.contains("name")) {
      meta.setDisplayName(ChatColor.RESET + format(section.getString("name"), player, oldRank, rank));
    }
    item.setItemMeta(meta);

    return item;
  }

  private static String format(String message, Player player, Rank oldRank, Rank rank) {
    return new MessageBuilder(ChatColor.translateAlternateColorCodes('&', message))
        .replaceAll(player, oldRank, rank)
        .toString();
  }

  private static void addItem(ItemStack[] items, ConfigurationSection section, Player player, Rank oldRank, Rank rank) {
    ItemStack item = getItem(section, player, oldRank, rank);
    if (section.getName().equalsIgnoreCase("fill")) {
      for (int i = 0; i < items.length; i++) {
        if (items[i] == null) {
          items[i] = item;
        }
      }
      return;
    }

    String[] locations = section.getString("index").split(" ");
    for (String location : locations) {
      String[] parts = location.split("-");
      if (parts.length == 1) {
        items[Integer.parseInt(parts[0])] = item;
      } else {
        for (int i = Integer.parseInt(parts[0]); i <= Integer.parseInt(parts[1]); i++) {
          items[i] = item;
        }
      }
    }
  }

  public void open(Player player) {
    player.openInventory(inventory);
  }
}

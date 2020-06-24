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
import sh.okx.rankup.RankupPlugin;
import sh.okx.rankup.messages.Message;
import sh.okx.rankup.messages.MessageBuilder;
import sh.okx.rankup.prestige.Prestige;
import sh.okx.rankup.ranks.Rank;
import sh.okx.rankup.util.ItemUtil;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Gui implements InventoryHolder {
  @Getter
  private Inventory inventory;
  @Getter
  private ItemStack rankup;
  @Getter
  private ItemStack cancel;
  @Getter
  private boolean prestige;

  public static Gui of(Player player, Rank oldRank, Rank rank, RankupPlugin plugin) {
    Gui gui = new Gui();
    gui.prestige = oldRank instanceof Prestige;

    String type = gui.prestige ? "prestige" : "rankup";
    String basePath = type + ".gui";
    ConfigurationSection config = plugin.getSection(oldRank, basePath);
    ItemStack[] items = new ItemStack[config.getInt("rows", 1) * 9];

    ItemStack fill = getItem(plugin, plugin.getSection(oldRank, basePath + ".fill"), player, oldRank, rank);
    ItemStack cancel = getItem(plugin, plugin.getSection(oldRank, basePath + ".cancel"), player, oldRank, rank);
    ItemStack rankup = getItem(plugin, plugin.getSection(oldRank, basePath + ".rankup"), player, oldRank, rank);

    addItem(items, plugin.getSection(oldRank, basePath + ".rankup"), rankup);
    addItem(items, plugin.getSection(oldRank, basePath + ".cancel"), cancel);
    addItem(items, plugin.getSection(oldRank, basePath + ".fill"), fill);

    gui.rankup = rankup;
    gui.cancel = cancel;

    Inventory inventory = Bukkit.createInventory(gui, items.length,
        plugin.replaceMoneyRequirements(
            plugin.getMessage(oldRank, gui.prestige ? Message.PRESTIGE_TITLE : Message.TITLE)
                .replaceRanks(player, oldRank, rank)
                .replaceFromTo(oldRank), player, oldRank).toString());
    inventory.setContents(items);
    gui.inventory = inventory;
    return gui;
  }

  @SuppressWarnings("deprecation")
  private static ItemStack getItem(RankupPlugin plugin, ConfigurationSection section, Player player, Rank oldRank, Rank rank) {
    String materialName = section.getString("material").toUpperCase();

    ItemStack item;
    if (ItemUtil.isServerFlattened()) {
      Material material = Material.valueOf(materialName);
      item = new ItemStack(material);
    } else {
      // handle default material correctly on older versions
      if (materialName.equals("BLACK_STAINED_GLASS_PANE")) {
        materialName = "STAINED_GLASS_PANE:15";
      }

      String[] parts = materialName.split(":");
      Material material = Material.valueOf(parts[0]);

      short type = parts.length > 1 ? Short.parseShort(parts[1]) : 0;
      item = new ItemStack(material, 1, type);
    }

    if (item.getType() == Material.AIR && section.getName().equalsIgnoreCase("fill")) {
      return item;
    }

    ItemMeta meta = item.getItemMeta();
    if (section.contains("lore")) {
      meta.setLore(Arrays.stream(format(plugin, section.getString("lore"), player, oldRank, rank).split("\n"))
          .map(string -> ChatColor.RESET + string)
          .collect(Collectors.toList()));
    }
    if (section.contains("name")) {
      meta.setDisplayName(ChatColor.RESET + format(plugin, section.getString("name"), player, oldRank, rank));
    }
    item.setItemMeta(meta);

    return item;
  }

  private static String format(RankupPlugin plugin, String message, Player player, Rank oldRank, Rank rank) {
    return plugin.replaceMoneyRequirements(new MessageBuilder(ChatColor.translateAlternateColorCodes('&', message))
        .replaceRanks(player, oldRank, rank), player, oldRank)
        .toString();
  }

  private static void addItem(ItemStack[] items, ConfigurationSection section, ItemStack item) {
    Objects.requireNonNull(section, "GUI configuration section not found");
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

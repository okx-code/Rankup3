package sh.okx.rankup.requirements.requirement;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import sh.okx.rankup.RankupPlugin;
import sh.okx.rankup.requirements.ProgressiveRequirement;
import sh.okx.rankup.requirements.Requirement;

import java.util.Arrays;

public class ItemRequirement extends ProgressiveRequirement {
  public static final boolean USE_STORAGE_CONTENTS;

  static {
    boolean getStorageContentsMethodExists;
    try {
      Inventory.class.getMethod("getStorageContents");
      getStorageContentsMethodExists = true;
    } catch (NoSuchMethodException e) {
      getStorageContentsMethodExists = false;
    }
    USE_STORAGE_CONTENTS = getStorageContentsMethodExists;
  }

  public ItemRequirement(RankupPlugin plugin, String name) {
    super(plugin, name, true);
  }

  protected ItemRequirement(ItemRequirement clone) {
    super(clone);
  }

  @Override
  public Requirement clone() {
    return new ItemRequirement(this);
  }

  @Override
  public double getProgress(Player player) {
    PlayerInventory inventory = player.getInventory();
    ItemStack[] contents;
    if (USE_STORAGE_CONTENTS) {
      contents = inventory.getStorageContents();
    } else {
      contents = inventory.getContents();
    }
    return Arrays.stream(contents)
        .filter(this::matchItem)
        .mapToInt(ItemStack::getAmount).sum();
  }

  @SuppressWarnings("deprecation")
  protected boolean matchItem(ItemStack item) {
    if (item == null) {
      return false;
    }

    String sub = getSub();
    String[] parts = sub.split(":");

    Material material = Material.matchMaterial(parts[0]);
    if (material == null) {
      throw new IllegalArgumentException("[item requirement] could not find material name: " + parts[0]);
    }

    if (parts.length > 1) {
      int durability;
      try {
        durability = Integer.parseInt(parts[1]);
      } catch (NumberFormatException e) {
        throw new IllegalArgumentException("[item requirement] durability '" + parts[1] + "' must be a number in item: '" + sub + "'");
      }

      if (durability != item.getDurability()) {
        return false;
      }
    }

    return material == item.getType();
  }
}

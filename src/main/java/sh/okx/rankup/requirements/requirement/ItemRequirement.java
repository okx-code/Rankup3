package sh.okx.rankup.requirements.requirement;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import sh.okx.rankup.RankupPlugin;
import sh.okx.rankup.requirements.ProgressiveRequirement;
import sh.okx.rankup.requirements.Requirement;

import java.util.Arrays;

public class ItemRequirement extends ProgressiveRequirement {
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
    return Arrays.stream(player.getInventory().getStorageContents())
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

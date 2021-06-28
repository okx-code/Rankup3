package sh.okx.rankup.requirements.requirement;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import sh.okx.rankup.RankupPlugin;
import sh.okx.rankup.requirements.DeductibleRequirement;
import sh.okx.rankup.requirements.Requirement;

public class ItemDeductibleRequirement extends ItemRequirement implements DeductibleRequirement {

  public ItemDeductibleRequirement(RankupPlugin plugin, String name) {
    super(plugin, name);
  }

  public ItemDeductibleRequirement(ItemDeductibleRequirement clone) {
    super(clone);
  }

  @Override
  public void apply(Player player, double multiplier) {
    int count = (int) (getTotal(player) * multiplier);

    PlayerInventory inventory = player.getInventory();
    ItemStack[] contents;
    if (ItemRequirement.USE_STORAGE_CONTENTS) {
      contents = inventory.getStorageContents();
    } else {
      contents = inventory.getContents();
    }
    for (int i = 0; i < contents.length && count > 0; i++) {
      ItemStack item = contents[i];

      if (matchItem(item)) {
        if (count < item.getAmount()) {
          item.setAmount(item.getAmount() - count);
          count = 0;
        } else {
          count -= item.getAmount();
          contents[i] = null;
        }
      }
    }

    if (ItemRequirement.USE_STORAGE_CONTENTS) {
      inventory.setStorageContents(contents);
    } else {
      inventory.setContents(contents);
    }

    if (count > 0) {
      throw new IllegalStateException("REPORT THIS ERROR TO THE DEV - COULD NOT DEDUCT ALL ITEMS");
    }
  }

  @Override
  public Requirement clone() {
    return new ItemDeductibleRequirement(this);
  }
}
